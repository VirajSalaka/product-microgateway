package org.wso2.apimgt.gateway.cli.model.route;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class represents the endpoint configurations for the given environment. (in the routes.yaml)
 */
public abstract class EndpointConfig {

    private EndpointListRouteDTO ProdEndpointList = null;
    private EndpointListRouteDTO sandboxEndpointList = null;

    @JsonProperty("Production")
    public EndpointListRouteDTO getProdEndpointList() {
        return ProdEndpointList;
    }

    public void setProdEndpointList(EndpointListRouteDTO prodEndpointList) {
        this.ProdEndpointList = prodEndpointList;
    }

    @JsonProperty("Sandbox")
    public EndpointListRouteDTO getSandboxEndpointList() {
        return sandboxEndpointList;
    }

    public void setSandboxEndpointList(EndpointListRouteDTO sandboxEndpointList) {
        this.sandboxEndpointList = sandboxEndpointList;
    }
}
