package xds

import (
	"fmt"
	"reflect"
	"sync"
	"sync/atomic"

	clusterv3 "github.com/envoyproxy/go-control-plane/envoy/config/cluster/v3"
	corev3 "github.com/envoyproxy/go-control-plane/envoy/config/core/v3"
	listenerv3 "github.com/envoyproxy/go-control-plane/envoy/config/listener/v3"
	routev3 "github.com/envoyproxy/go-control-plane/envoy/config/route/v3"
	"github.com/envoyproxy/go-control-plane/pkg/cache/types"
	cachev3 "github.com/envoyproxy/go-control-plane/pkg/cache/v3"
	openAPI3 "github.com/getkin/kin-openapi/openapi3"
	openAPI2 "github.com/go-openapi/spec"
	logger "github.com/wso2/micro-gw/internal/loggers"
	oasParser "github.com/wso2/micro-gw/internal/pkg/oasparser"
	"github.com/wso2/micro-gw/internal/pkg/oasparser/models/apiDefinition"
	swaggerOperator "github.com/wso2/micro-gw/internal/pkg/oasparser/swaggerOperator"
)

var (
	version int32

	cache cachev3.SnapshotCache

	openAPIV3Map     map[string]openAPI3.Swagger
	openAPIV2Map     map[string]openAPI2.Swagger
	openAPIEnvoyMap  map[string][]string
	openAPIRoutesMap map[string][]*routev3.Route
	//openAPIListenersMap    map[string][]types.Resource
	openAPIClustersMap     map[string][]*clusterv3.Cluster
	openAPIEndpointsMap    map[string][]*corev3.Address
	envoyUpdateVersionMap  map[string]int64
	envoyListenerConfigMap map[string]*listenerv3.Listener
	envoyRouteConfigMap    map[string]*routev3.RouteConfiguration
	//listenerEnvoyConfig   types.Resource
)

// IDHash uses ID field as the node hash.
type IDHash struct{}

// ID uses the node ID field
func (IDHash) ID(node *corev3.Node) string {
	if node == nil {
		return "unknown"
	}
	return node.Id
}

var _ cachev3.NodeHash = IDHash{}

func Init() {
	cache = cachev3.NewSnapshotCache(false, IDHash{}, nil)
	openAPIV3Map = make(map[string]openAPI3.Swagger)
	openAPIV2Map = make(map[string]openAPI2.Swagger)
	openAPIEnvoyMap = make(map[string][]string)
	openAPIRoutesMap = make(map[string][]*routev3.Route)
	//openAPIListenersMap = make(map[string][]types.Resource)
	openAPIClustersMap = make(map[string][]*clusterv3.Cluster)
	openAPIEndpointsMap = make(map[string][]*corev3.Address)
	//TODO: (VirajSalaka) Swagger or project should contain the version as a meta information
	envoyUpdateVersionMap = make(map[string]int64)
	envoyListenerConfigMap = make(map[string]*listenerv3.Listener)
	envoyRouteConfigMap = make(map[string]*routev3.RouteConfiguration)

}

func GetXdsCache() cachev3.SnapshotCache {
	return cache
}

/**
 * Recreate the envoy instances from swaggers byte array.
 *
 * @param []byte   Swagger file as byte array
 */
func UpdateEnvoyByteArr(byteArr []byte) {
	var apiMapKey string
	var newLabels []string

	//TODO: (VirajSalaka) Optimize locking
	var l sync.Mutex
	l.Lock()
	defer l.Unlock()

	openAPIVersion, jsonContent, _ := swaggerOperator.GetOpenAPIVersionAndJsonContent(byteArr)
	if openAPIVersion == "3" {
		openAPIV3Struct, _ := swaggerOperator.GetOpenAPIV3Struct(jsonContent)
		apiMapKey = openAPIV3Struct.Info.Title + ":" + openAPIV3Struct.Info.Version
		existingOpenAPI, ok := openAPIV3Map[apiMapKey]
		if ok {
			if reflect.DeepEqual(openAPIV3Struct, existingOpenAPI) {
				//Works as the openAPI already contains the label feature.
				return
			}
		}
		openAPIV3Map[apiMapKey] = openAPIV3Struct
		//TODO: (VirajSalaka) Handle OpenAPIs which does not have label (Current Impl , it will be labelled as default)
		newLabels = apiDefinition.GetXWso2Label(openAPIV3Struct.ExtensionProps)
	} else {
		//TODO: (VirajSalaka) add openAPI v2 support
		openAPIV2Struct, _ := swaggerOperator.GetOpenAPIV2Struct(jsonContent)
		apiMapKey = openAPIV2Struct.Info.Title + ":" + openAPIV2Struct.Info.Version
		existingOpenAPI, ok := openAPIV2Map[apiMapKey]
		if ok {
			if reflect.DeepEqual(openAPIV2Struct, existingOpenAPI) {
				//Works as the openAPI already contains the label feature.
				return
			}
		}
		newLabels = swaggerOperator.GetXWso2Labels(openAPIV2Struct.Extensions)
	}
	oldLabels, _ := openAPIEnvoyMap[apiMapKey]
	openAPIEnvoyMap[apiMapKey] = newLabels
	//TODO: (VirajSalaka) Routes populated is wrong here. It has to follow https://github.com/envoyproxy/envoy/blob/v1.16.0/api/envoy/config/route/v3/route.proto
	//TODO: (VirajSalaka) Can bring VHDS (Delta), but since the gateway would contain only one domain, it won't have much impact.
	routes, clusters, endpoints := oasParser.GetProductionRoutesClustersEndpoints(byteArr)
	//TODO: (VirajSalaka) Decide if the routes and listeners need their own map since it is not going to be changed based on API at the moment.
	openAPIRoutesMap[apiMapKey] = routes
	//openAPIListenersMap[apiMapKey] = listeners
	openAPIClustersMap[apiMapKey] = clusters
	openAPIEndpointsMap[apiMapKey] = endpoints

	updateXdsCacheOnAPIAdd(oldLabels, newLabels)
}

/**
 * Recreate the envoy instances from swaggers.
 *
 * @param location   Swagger files location
 */
func UpdateEnvoy(location string) {
	var nodeId string
	//TODO: (VirajSalaka) Keep a hard coded value for the nodeID for the initial setup.
	// if len(cache.GetStatusKeys()) > 0 {
	// 	nodeId = cache.GetStatusKeys()[0]
	// }
	nodeId = "test-id"

	listeners, clusters, routes, endpoints := oasParser.GetProductionSourcesFromFile(location)

	atomic.AddInt32(&version, 1)
	logger.LoggerMgw.Infof(">>>>>>>>>>>>>>>>>>> creating snapshot Version " + fmt.Sprint(version))
	snap := cachev3.NewSnapshot(fmt.Sprint(version), endpoints, clusters, routes, listeners, nil)
	snap.Consistent()

	err := cache.SetSnapshot(nodeId, snap)
	if err != nil {
		logger.LoggerMgw.Error(err)
	}
}

func arrayContains(a []string, x string) bool {
	for _, n := range a {
		if x == n {
			return true
		}
	}
	return false
}

func mergeResourceArrays(resourceArrays [][]types.Resource) []types.Resource {
	var totalLength int
	var compositeArray []types.Resource
	for _, resourceArray := range resourceArrays {
		totalLength += len(resourceArray)
	}
	compositeArray = make([]types.Resource, totalLength)
	startingIndex := 0
	lastIndex := 0
	for _, resourceArray := range resourceArrays {
		lastIndex += len(resourceArray)
		copy(compositeArray[startingIndex:lastIndex], resourceArray)
		startingIndex = lastIndex
	}
	return compositeArray
}

//by the time this method is called, openAPIEnvoy map is updated.
//We are keeping old labels in a different label
func updateXdsCacheOnAPIAdd(oldLabels []string, newLabels []string) {

	//TODO: (VirajSalaka) check possible optimizations, Since the number of labels are low by design it should not be an issue
	for _, oldLabel := range oldLabels {
		if !arrayContains(newLabels, oldLabel) {
			listeners, clusters, routes, endpoints := generateEnvoyResoucesForLabel(oldLabel)
			updateXdsCache(oldLabel, endpoints, clusters, routes, listeners)
		}
	}

	for _, newLabel := range newLabels {
		listeners, clusters, routes, endpoints := generateEnvoyResoucesForLabel(newLabel)
		updateXdsCache(newLabel, endpoints, clusters, routes, listeners)
	}
}

// func generateEnvoyResoucesForLabel(label string) ([]types.Resource, []types.Resource, []types.Resource, []types.Resource) {
// 	var clusterArrays [][]types.Resource
// 	var routeArrays [][]types.Resource
// 	var endpointArrays [][]types.Resource
// 	//TODO: (VirajSalaka) Listeners should not be repeated
// 	var listenerArrays [][]types.Resource
// 	for apiKey, labels := range openAPIEnvoyMap {
// 		if arrayContains(labels, label) {
// 			clusterArrays = append(clusterArrays, openAPIClustersMap[apiKey])
// 			routeArrays = append(routeArrays, openAPIRoutesMap[apiKey])
// 			endpointArrays = append(endpointArrays, openAPIEndpointsMap[apiKey])
// 			//listenerArrays = append(listenerArrays, openAPIListenersMap[apiKey])
// 		}
// 	}
// 	return mergeResourceArrays(endpointArrays), mergeResourceArrays(clusterArrays), mergeResourceArrays(routeArrays),
// 		mergeResourceArrays(listenerArrays)
// }

//TO
func generateEnvoyResoucesForLabel(label string) ([]types.Resource, []types.Resource, []types.Resource, []types.Resource) {
	var clusterArray []*clusterv3.Cluster
	var routeArray []*routev3.Route
	var endpointArray []*corev3.Address
	//TODO: (VirajSalaka) Listeners should not be repeated
	//var listenerArrays [][]types.Resource
	for apiKey, labels := range openAPIEnvoyMap {
		if arrayContains(labels, label) {
			clusterArray = append(clusterArray, openAPIClustersMap[apiKey]...)
			routeArray = append(routeArray, openAPIRoutesMap[apiKey]...)
			endpointArray = append(endpointArray, openAPIEndpointsMap[apiKey]...)
			//listenerArrays = append(listenerArrays, openAPIListenersMap[apiKey])
		}
	}
	listener, listenerFound := envoyListenerConfigMap[label]
	routesConfig, routesConfigFound := envoyRouteConfigMap[label]
	if !listenerFound && !routesConfigFound {
		listener, routesConfig = oasParser.GetProductionListenerAndRouteConfig(routeArray)
		envoyListenerConfigMap[label] = listener
		envoyRouteConfigMap[label] = routesConfig
	} else {
		//If the routesConfig exists, the listener exists too
		oasParser.UpdateRoutesConfig(routesConfig, routeArray)
	}
	return oasParser.GetCacheResources(endpointArray, clusterArray, listener, routesConfig)
}

func updateXdsCache(label string, endpoints []types.Resource, clusters []types.Resource, routes []types.Resource, listeners []types.Resource) {
	version, ok := envoyUpdateVersionMap[label]
	if ok {
		version += 1
	} else {
		//TODO : (VirajSalaka) Fix control plane restart scenario
		version = 1
	}
	//TODO: (VirajSalaka) kept same version for all the resources as we are using simple cache implementation.
	//Will be updated once we moved to incremental XDS
	snap := cachev3.NewSnapshot(fmt.Sprint(version), endpoints, clusters, routes, listeners, nil)
	snap.Consistent()
	err := cache.SetSnapshot(label, snap)
	if err != nil {
		logger.LoggerMgw.Error(err)
	}
	envoyUpdateVersionMap[label] = version
	logger.LoggerMgw.Infof("New cache update for the label: " + label + " version: " + fmt.Sprint(version))
}

//TODO: (VirajSalaka) Remove
// func checkAndUpdate(nodeId string, endpoints []types.Resource, clusters []types.Resource, routes []types.Resource,
// 		listeners []types.Resource) bool {
// 	snap, err := cache.GetSnapshot(nodeId)
// 	snap.getR

// 	return true
// }
