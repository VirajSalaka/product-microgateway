/*
 *  Copyright (c) 2024, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package envoyconf

import (
	"os"

	clusterv3 "github.com/envoyproxy/go-control-plane/envoy/config/cluster/v3"
	routev3 "github.com/envoyproxy/go-control-plane/envoy/config/route/v3"
)

// TODO: (VirajSalaka) remove later
const EnforceValidationENVVar = "ENFORCE_XDS_VALIDATION"

func ValidateRoute(route *routev3.Route) (error) {
	if !enforceXDSValidation() {
		return nil
	}
	return route.ValidateAll()
}

func ValidateCluster(cluster *clusterv3.Cluster) (error) {
	if !enforceXDSValidation() {
		return nil
	}
	return cluster.ValidateAll()
}

func enforceXDSValidation() bool {
	envVar, found := os.LookupEnv(EnforceValidationENVVar)
	if found {
		return envVar == "true"
	}
	return false
}
