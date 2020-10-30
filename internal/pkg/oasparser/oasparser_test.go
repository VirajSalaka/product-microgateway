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

package oasparser_test

import (
	"os"
	"testing"

	"github.com/envoyproxy/go-control-plane/pkg/cache/types"
	"github.com/golang/protobuf/ptypes/wrappers"
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

// func TestUpdateEnvoy(t *testing.T) {
// 	_, filename, _, _ := runtime.Caller(0)
// 	t.Logf("Current test filename: %s", filename)
// 	fileDir := filepath.Dir(filename)

// 	fileContent, err := ioutil.ReadFile(fileDir + "/../../test-resources/xds-server/petstorev3-extensions.yaml")
// 	if err != nil {
// 		t.Errorf("OpenAPI file cannot be opened : %v", err)
// 	}

// 	routes, clusters, _ := oasParser.GetProductionRoutesClustersEndpoints(fileContent)

// 	if len(routes) =
// }
