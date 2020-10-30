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
package xds_test

import (
	"io/ioutil"
	"os"
	"path/filepath"
	"runtime"
	"testing"

	"github.com/envoyproxy/go-control-plane/pkg/cache/types"
	rsrcv3 "github.com/envoyproxy/go-control-plane/pkg/resource/v3"
	wrappers "github.com/golang/protobuf/ptypes/wrappers"
	"github.com/wso2/micro-gw/internal/pkg/xds"
)

func testResource(s string) types.Resource {
	return &wrappers.StringValue{Value: s}
}

//openapiv3Sample :=

func TestMain(m *testing.M) {
	setup()
	code := m.Run()
	//teardown()
	os.Exit(code)
}

func setup() {
	// Do something here.
	xds.Init()
}

func TestUpdateEnvoy(t *testing.T) {
	_, filename, _, _ := runtime.Caller(0)
	t.Logf("Current test filename: %s", filename)
	fileDir := filepath.Dir(filename)

	fileContent, err := ioutil.ReadFile(fileDir + "/../../test-resources/xds-server/petstorev3-extensions.yaml")
	if err != nil {
		t.Errorf("OpenAPI file cannot be opened : %v", err)
	}

	xds.UpdateEnvoyByteArr(fileContent)

	snap, err := xds.GetXdsCache().GetSnapshot("default")

	if err != nil {
		t.Errorf("Error while obtaining the snapshot from xds cache : %v", err)
	}
	// TODO: (VirajSalaka) provide endpoints
	// endpoints := snap.GetResources(rsrcv3.EndpointType)
	clusters := snap.GetResources(rsrcv3.ClusterType)
	// listeners := snap.GetResources(rsrcv3.ListenerType)
	// routes := snap.GetResources(rsrcv3.RouteType)
	for k, v := range clusters {
		t.Logf("key : %v\n value %v \n----\n", k, v)
	}
}
