package xds

import (
	"fmt"
	"sync/atomic"

	corev3 "github.com/envoyproxy/go-control-plane/envoy/config/core/v3"
	cachev3 "github.com/envoyproxy/go-control-plane/pkg/cache/v3"
	logger "github.com/wso2/micro-gw/internal/loggers"
	oasParser "github.com/wso2/micro-gw/internal/pkg/oasparser"
)

var (
	version int32

	cache cachev3.SnapshotCache
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

func GetXdsCache() cachev3.SnapshotCache {
	if cache == nil {
		cache = cachev3.NewSnapshotCache(false, IDHash{}, nil)
	}
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
