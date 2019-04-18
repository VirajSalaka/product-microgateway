/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.apimgt.gateway.cli.model.mgwcodegen;

import org.wso2.apimgt.gateway.cli.model.rest.APIEndpointSecurityDTO;
import org.wso2.apimgt.gateway.cli.model.rest.EndpointUrlTypeEnum;
import org.wso2.apimgt.gateway.cli.model.route.EndpointType;

import java.util.List;

/**
 * This class represents the endpoint configuration of a given environment (production or sandbox) required for
 * ballerina code generation process (in mustache templates).
 */
public class MgwEndpointListDTO {

    private APIEndpointSecurityDTO securityConfig = null;
    private EndpointType type = null;
    private List<MgwEndpointDTO> endpoints = null;
    private EndpointUrlTypeEnum endpointUrlType = null;

    public APIEndpointSecurityDTO getSecurityConfig() {
        return securityConfig;
    }

    public void setSecurityConfig(APIEndpointSecurityDTO securityConfig) {
        this.securityConfig = securityConfig;
    }

    public EndpointType getType() {
        return type;
    }

    public void setType(EndpointType type) {
        this.type = type;
    }

    public List<MgwEndpointDTO> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<MgwEndpointDTO> endpoints) {
        this.endpoints = endpoints;
    }

    public EndpointUrlTypeEnum getEndpointUrlType() {
        return endpointUrlType;
    }

    public void setEndpointUrlType(EndpointUrlTypeEnum endpointUrlType) {
        this.endpointUrlType = endpointUrlType;
    }
}
