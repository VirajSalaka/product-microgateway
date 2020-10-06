/*
 *  Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package mgw

import (
	"log"
	"time"

	corev3 "github.com/envoyproxy/go-control-plane/envoy/config/core/v3"
	discoveryv3 "github.com/envoyproxy/go-control-plane/envoy/service/discovery/v3"
	cachev3 "github.com/envoyproxy/go-control-plane/pkg/cache/v3"
	xdsv3 "github.com/envoyproxy/go-control-plane/pkg/server/v3"

	// testv3 "github.com/envoyproxy/go-control-plane/pkg/test/v3"

	"context"
	"flag"
	"fmt"
	"net"
	"os"
	"os/signal"
	"sync/atomic"

	"github.com/fsnotify/fsnotify"
	"github.com/wso2/micro-gw/configs"
	mgwconfig "github.com/wso2/micro-gw/configs/confTypes"
	logger "github.com/wso2/micro-gw/internal/loggers"
	apiserver "github.com/wso2/micro-gw/internal/pkg/api"
	oasParser "github.com/wso2/micro-gw/internal/pkg/oasparser"
	"google.golang.org/grpc"
)

var (
	debug       bool
	onlyLogging bool

	localhost = "0.0.0.0"

	port        uint
	gatewayPort uint
	alsPort     uint

	mode string

	version int32

	cache cachev3.SnapshotCache
)

const (
	XdsCluster = "xds_cluster"
	Ads        = "ads"
	Xds        = "xds"
	Rest       = "rest"
)

func init() {
	flag.BoolVar(&debug, "debug", true, "Use debug logging")
	flag.BoolVar(&onlyLogging, "onlyLogging", false, "Only demo AccessLogging Service")
	flag.UintVar(&port, "port", 18002, "Management server port")
	flag.UintVar(&gatewayPort, "gateway", 18001, "Management server port for HTTP gateway")
	flag.UintVar(&alsPort, "als", 18090, "Accesslog server port")
	flag.StringVar(&mode, "ads", Ads, "Management server type (ads, xds, rest)")
}

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

const grpcMaxConcurrentStreams = 1000000

/**
 * This starts an xDS server at the given port.
 *
 * @param ctx   Context
 * @param server   Xds server instance
 * @param port   Management server port
 */
func RunManagementServer(ctx context.Context, server xdsv3.Server, port uint) {
	var grpcOptions []grpc.ServerOption
	grpcOptions = append(grpcOptions, grpc.MaxConcurrentStreams(grpcMaxConcurrentStreams))
	grpcServer := grpc.NewServer()

	lis, err := net.Listen("tcp", fmt.Sprintf(":%d", port))
	if err != nil {
		logger.LoggerMgw.Fatal("failed to listen: ", err)
	}

	// register services
	discoveryv3.RegisterAggregatedDiscoveryServiceServer(grpcServer, server)
	// endpointservicev3.RegisterEndpointDiscoveryServiceServer(grpcServer, server)
	// clusterservicev3.RegisterClusterDiscoveryServiceServer(grpcServer, server)
	// routeservicev3.RegisterRouteDiscoveryServiceServer(grpcServer, server)
	// listenerservicev3.RegisterListenerDiscoveryServiceServer(grpcServer, server)

	logger.LoggerMgw.Info("port: ", port, " management server listening")
	//log.Fatalf("", Serve(lis))
	//go func() {
	go func() {
		if err = grpcServer.Serve(lis); err != nil {
			logger.LoggerMgw.Error(err)
		}
	}()
	// <-ctx.Done()
	//grpcServer.GracefulStop()
	//}()

}

/**
 * Recreate the envoy instances from swaggers.
 *
 * @param location   Swagger files location
 */
func updateEnvoy(location string) {

	listeners, clusters, routes, endpoints := oasParser.GetProductionSources(location)

	atomic.AddInt32(&version, 1)
	logger.LoggerMgw.Infof(">>>>>>>>>>>>>>>>>>> creating snapshot Version " + fmt.Sprint(version))
	snap := cachev3.NewSnapshot(fmt.Sprint(version), endpoints, clusters, routes, listeners, nil, nil)
	snap.Consistent()

	if len(cache.GetStatusKeys()) > 0 {
		for i := 0; i < len(cache.GetStatusKeys()); i++ {
			var nodeId string
			nodeId = cache.GetStatusKeys()[i]
			err := cache.SetSnapshot(nodeId, snap)
			if err != nil {
				logger.LoggerMgw.Error(err)
			}
		}
	}
}

/**
 * Recreate the envoy instances from swaggers using envoy id.
 *
 * @param location   Swagger files location
 */
func updateEnvoyForSpecificNode(location string, nodeId string) {

	//todo (VirajSalaka): avoid printing the error message
	_, error := cache.GetSnapshot(nodeId)
	//if the snapshot exists, it means that the envoy is already updated.
	//todo: change the logic as move forward
	if error != nil {
		listeners, clusters, routes, endpoints := oasParser.GetProductionSources(location)

		atomic.AddInt32(&version, 1)
		logger.LoggerMgw.Infof(">>>>>>>>>>>>>>>>>>> creating snapshot Version for node " + nodeId + " : " + fmt.Sprint(version))
		snap := cachev3.NewSnapshot(fmt.Sprint(version), endpoints, clusters, routes, listeners, nil, nil)
		snap.Consistent()

		err := cache.SetSnapshot(nodeId, snap)
		if err != nil {
			logger.LoggerMgw.Error(err)
		}
	}
}

/**
 * Run the management grpc server.
 *
 * @param conf  Swagger files location
 */
func Run(conf *mgwconfig.Config) {
	sig := make(chan os.Signal)
	signal.Notify(sig, os.Interrupt)
	watcher, _ := fsnotify.NewWatcher()
	err := watcher.Add(conf.Apis.Location)

	signal := make(chan struct{})
	//todo: implement own set of callbacks.
	cbv3 := &Callbacks{Signal: signal, Debug: true}

	if err != nil {
		logger.LoggerMgw.Fatal("Error reading the api definitions.", err)
	}

	flag.Parse()

	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()

	//log config watcher
	watcherLogConf, _ := fsnotify.NewWatcher()
	errC := watcherLogConf.Add("resources/conf/log_config.toml")

	if errC != nil {
		logger.LoggerMgw.Fatal("Error reading the log configs. ", err)
	}

	logger.LoggerMgw.Info("Starting control plane ....")

	cache = cachev3.NewSnapshotCache(mode != Ads, IDHash{}, nil)

	srv := xdsv3.NewServer(ctx, cache, cbv3)

	//als := &myals.AccessLogService{}
	//go RunAccessLogServer(ctx, als, alsPort)

	// start the xDS server
	RunManagementServer(ctx, srv, port)
	go apiserver.Start(conf)

	log.Println("waiting for the first request...")
	select {
	case <-sig:
		logger.LoggerMgw.Error("Interrupted explicitly.")
		os.Exit(1)
		break
	case <-signal:
		break
	case <-time.After(100 * time.Second):
		logger.LoggerMgw.Error("timeout waiting for the first request")
		os.Exit(1)
	}
	//updateEnvoy(conf.Apis.Location)
OUTER:
	for {
		select {
		case c := <-watcher.Events:
			switch c.Op.String() {
			case "WRITE":
				logger.LoggerMgw.Info("Loading updated swagger definition...")
				updateEnvoy(conf.Apis.Location)
			}
		case l := <-watcherLogConf.Events:
			switch l.Op.String() {
			case "WRITE":
				logger.LoggerMgw.Info("Loading updated log config file...")
				configs.ClearLogConfigInstance()
				logger.UpdateLoggers()
			}
		case s := <-sig:
			switch s {
			case os.Interrupt:
				logger.LoggerMgw.Info("Shutting down...")
				break OUTER
			}
		}
	}
	logger.LoggerMgw.Info("Bye!")
}
