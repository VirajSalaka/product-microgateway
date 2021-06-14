/*
 *  Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package ga

import (
	"context"
	"fmt"
	"io"
	"log"
	"time"

	core "github.com/envoyproxy/go-control-plane/envoy/config/core/v3"
	discovery "github.com/envoyproxy/go-control-plane/envoy/service/discovery/v3"
	stub "github.com/wso2/product-microgateway/adapter/pkg/discovery/api/wso2/discovery/service/ga"
	"google.golang.org/genproto/googleapis/rpc/status"
	"google.golang.org/grpc"
)

var (
	apiRevisionMap        map[string]string
	lastSuccessfulVersion string
	laskAckedResponse     *discovery.DiscoveryResponse
	lastReceivedResponse  *discovery.DiscoveryResponse
	xdsStream             stub.ApiGADiscoveryService_StreamGAApisClient
)

const (
	apiTypeURL string = "type.googleapis.com/wso2.discovery.ga.Api"
)

func init() {
	apiRevisionMap = make(map[string]string)
	laskAckedResponse = &discovery.DiscoveryResponse{}
}

func initConnection(xdsURL string) {
	// ctx := context.Background()
	// TODO: (VirajSalaka) Dial or DialContext
	conn, err := grpc.Dial(xdsURL, grpc.WithInsecure())
	if err != nil {
		log.Fatal(err)
		return
	}
	// defer conn.Close()
	client := stub.NewApiGADiscoveryServiceClient(conn)

	streamContext := context.Background()
	fmt.Println(conn.GetState().String())

	time.Sleep(11 * time.Second)

	xdsStream, err = client.StreamGAApis(streamContext)

	fmt.Println(conn.GetState().String())

	if err != nil {
		// TODO: (VirajSalaka) handle error
		fmt.Printf("error while starting client %s \n", err.Error())
		return
	}
}

func watchAPIs() {
	for {
		fmt.Println("started.")
		discoveryResponse, err := xdsStream.Recv()
		fmt.Println("received.")
		if err == io.EOF {
			// read done.
			// TODO: (VirajSalaka) observe the behavior when grpc connection terminates
			fmt.Println("EOF")
			return
		}
		if err != nil {
			fmt.Printf("Failed to receive a note : %v", err)
			nack(err.Error())
		} else {
			lastReceivedResponse = discoveryResponse
			fmt.Printf("response %v", discoveryResponse)
			ack()
		}
	}
}

func ack() {
	discoveryRequest := &discovery.DiscoveryRequest{
		Node:          getAdapterNode(),
		VersionInfo:   laskAckedResponse.VersionInfo,
		TypeUrl:       apiTypeURL,
		ResponseNonce: lastReceivedResponse.Nonce,
	}
	xdsStream.Send(discoveryRequest)
	laskAckedResponse = lastReceivedResponse
}

func nack(errorMessage string) {
	if laskAckedResponse == nil {
		return
	}
	discoveryRequest := &discovery.DiscoveryRequest{
		Node:        getAdapterNode(),
		VersionInfo: laskAckedResponse.VersionInfo,
		TypeUrl:     apiTypeURL,
		// TODO: (VirajSalaka) check with the XDS protocol
		ResponseNonce: lastReceivedResponse.Nonce,
		ErrorDetail: &status.Status{
			Message: errorMessage,
		},
	}
	xdsStream.Send(discoveryRequest)
}

func getAdapterNode() *core.Node {
	return &core.Node{
		// TODO: (VirajSalaka) read from config.
		Id: "default",
	}
}

// InitAPIXds initializes the connection to the global adapter.
func InitAPIXds(xdsURL string) {
	initConnection(xdsURL)
	go watchAPIs()
	discoveryRequest := &discovery.DiscoveryRequest{
		Node:        getAdapterNode(),
		VersionInfo: "abcd",
		TypeUrl:     apiTypeURL,
	}
	xdsStream.Send(discoveryRequest)
	fmt.Println("sent")
	select {}
}
