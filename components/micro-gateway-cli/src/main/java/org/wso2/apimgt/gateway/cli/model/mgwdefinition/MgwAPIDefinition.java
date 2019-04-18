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
package org.wso2.apimgt.gateway.cli.model.mgwdefinition;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.wso2.apimgt.gateway.cli.model.rest.APICorsConfigurationDTO;
import org.wso2.apimgt.gateway.cli.model.route.EndpointListRouteDTO;

/**
 * This class represents the DTO for Single API in Microgateway Definition.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MgwAPIDefinition {
    private String title;
    private String version;
    private EndpointListRouteDTO prodEpReference;
    private EndpointListRouteDTO sandEpReference;
    //todo: add interceptor
    private MgwPathsDefinition pathsDefinition;
    private String security; //todo: bring enum
    private APICorsConfigurationDTO corsConfReference;

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @JsonProperty("prod-Endpoint")
    public EndpointListRouteDTO getProdEpReference() {
        return prodEpReference;
    }

    public void setProdEpReference(EndpointListRouteDTO prodEpReference) {
        this.prodEpReference = prodEpReference;
    }

    @JsonProperty("sand-Endpoint")
    public EndpointListRouteDTO getSandEpReference() {
        return sandEpReference;
    }

    public void setSandEpReference(EndpointListRouteDTO sandEpReference) {
        this.sandEpReference = sandEpReference;
    }

    @JsonProperty("security")
    public String getSecurity() {
        return security;
    }

    public void setSecurity(String security) {
        this.security = security;
    }

    @JsonProperty("cors-configuration")
    public APICorsConfigurationDTO getCorsConfReference() {
        return corsConfReference;
    }

    public void setCorsConfReference(APICorsConfigurationDTO corsConfReference) {
        this.corsConfReference = corsConfReference;
    }

    @JsonProperty("Resources")
    public MgwPathsDefinition getPathsDefinition() {
        return pathsDefinition;
    }

    public void setPathsDefinition(MgwPathsDefinition pathsDefinition) {
        this.pathsDefinition = pathsDefinition;
    }
}
