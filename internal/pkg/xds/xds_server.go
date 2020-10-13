package xds

import (
	"fmt"
	"reflect"
	"sync/atomic"

	corev3 "github.com/envoyproxy/go-control-plane/envoy/config/core/v3"
	cachev3 "github.com/envoyproxy/go-control-plane/pkg/cache/v3"
	openAPI3 "github.com/getkin/kin-openapi/openapi3"
	logger "github.com/wso2/micro-gw/internal/loggers"
	oasParser "github.com/wso2/micro-gw/internal/pkg/oasparser"
	swaggerOperator "github.com/wso2/micro-gw/internal/pkg/oasparser/swaggerOperator"
)

var (
	version int32

	cache cachev3.SnapshotCache

	openAPIMap map[string]openAPI3.Swagger
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
	openAPIMap = make(map[string]openAPI3.Swagger)
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
	var nodeId string
	//TODO: (VirajSalaka) Keep a hard coded value for the nodeID for the initial setup.
	// if len(cache.GetStatusKeys()) > 0 {
	// 	nodeId = cache.GetStatusKeys()[0]
	// }
	nodeId = "test-id"
	var apiMapKey string
	var openAPIV3Struct openAPI3.Swagger

	openAPIVersion, jsonContent, _ := swaggerOperator.GetOpenAPIVersionAndJsonContent(byteArr)
	if openAPIVersion == "3" {
		openAPIV3Struct, _ = swaggerOperator.GetOpenAPIV3Struct(jsonContent)
		apiMapKey = openAPIV3Struct.Info.Title + ":" + openAPIV3Struct.Info.Version
		existingOpenAPI, ok := openAPIMap[apiMapKey]
		if ok {
			if reflect.DeepEqual(openAPIV3Struct, existingOpenAPI) {
				return
			}
		}
		openAPIMap[apiMapKey] = openAPIV3Struct
	}

	listeners, clusters, routes, endpoints := oasParser.GetProductionSourcesFromByteArray(byteArr)

	atomic.AddInt32(&version, 1)
	logger.LoggerMgw.Infof(">>>>>>>>>>>>>>>>>>> creating snapshot Version " + fmt.Sprint(version))
	snap := cachev3.NewSnapshot(fmt.Sprint(version), endpoints, clusters, routes, listeners, nil)
	snap.Consistent()

	err := cache.SetSnapshot(nodeId, snap)
	if err != nil {
		logger.LoggerMgw.Error(err)
	}
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

//TODO: (VirajSalaka) Remove
// func checkAndUpdate(nodeId string, endpoints []types.Resource, clusters []types.Resource, routes []types.Resource,
// 		listeners []types.Resource) bool {
// 	snap, err := cache.GetSnapshot(nodeId)
// 	snap.getR

// 	return true
// }
