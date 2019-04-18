/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.apimgt.gateway.cli.model.route;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class represents the endpoint configurations for the given environment. (in the routes configuration)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RouteEndpointConfig {

    private EndpointListRouteDTO prodEndpointList = null;
    private EndpointListRouteDTO sandboxEndpointList = null;
    private String functionIn;
    private String functionOut;

    @JsonProperty("prod")
    public EndpointListRouteDTO getProdEndpointList() {
        return prodEndpointList;
    }

    public void setProdEndpointList(EndpointListRouteDTO prodEndpointList) {
        this.prodEndpointList = prodEndpointList;
    }

    @JsonProperty("sandbox")
    public EndpointListRouteDTO getSandboxEndpointList() {
        return sandboxEndpointList;
    }

    public void setSandboxEndpointList(EndpointListRouteDTO sandboxEndpointList) {
        this.sandboxEndpointList = sandboxEndpointList;
    }

    @JsonProperty("functionIn")
    public String getFunctionIn() {
        return functionIn;
    }

    public void setFunctionIn(String functionIn) {
        this.functionIn = functionIn;
    }

    @JsonProperty("functionOut")
    public String getFunctionOut() {
        return functionOut;
    }

    public void setFunctionOut(String functionOut) {
        this.functionOut = functionOut;
    }
}
