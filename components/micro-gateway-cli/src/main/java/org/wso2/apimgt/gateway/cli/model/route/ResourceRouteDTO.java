package org.wso2.apimgt.gateway.cli.model.route;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class holds the routes for http methods of a given resource (routes.yaml)
 */
public abstract class ResourceRouteDTO {

    private EndpointListRouteDTO defaultUrl = null;
    private EndpointListRouteDTO getMethodEndpoint = null;
    private EndpointListRouteDTO putMethodEndpoint = null;
    private EndpointListRouteDTO deleteMethodEndpoint = null;
    private EndpointListRouteDTO postMethodEndpoint = null;
    private EndpointListRouteDTO patchMethodEndpoint = null;
    private EndpointListRouteDTO optionsMethodEndpoint = null;

    @JsonProperty("defaultUrl")
    public EndpointListRouteDTO getDefaultUrl() {
        return defaultUrl;
    }

    public void setDefaultUrl(EndpointListRouteDTO defaultUrl) {
        this.defaultUrl = defaultUrl;
    }

    @JsonProperty("GET")
    public EndpointListRouteDTO getGetMethodEndpoint() {
        return getMethodEndpoint;
    }

    public void setGetMethodEndpoint(EndpointListRouteDTO getMethodEndpoint) {
        this.getMethodEndpoint = getMethodEndpoint;
    }

    @JsonProperty("PUT")
    public EndpointListRouteDTO getPutMethodEndpoint() {
        return putMethodEndpoint;
    }

    public void setPutMethodEndpoint(EndpointListRouteDTO putMethodEndpoint) {
        this.putMethodEndpoint = putMethodEndpoint;
    }

    @JsonProperty("DELETE")
    public EndpointListRouteDTO getDeleteMethodEndpoint() {
        return deleteMethodEndpoint;
    }

    public void setDeleteMethodEndpoint(EndpointListRouteDTO deleteMethodEndpoint) {
        this.deleteMethodEndpoint = deleteMethodEndpoint;
    }

    @JsonProperty("POST")
    public EndpointListRouteDTO getPostMethodEndpoint() {
        return postMethodEndpoint;
    }

    public void setPostMethodEndpoint(EndpointListRouteDTO postMethodEndpoint) {
        this.postMethodEndpoint = postMethodEndpoint;
    }

    @JsonProperty("PATCH")
    public EndpointListRouteDTO getPatchMethodEndpoint() {
        return patchMethodEndpoint;
    }

    public void setPatchMethodEndpoint(EndpointListRouteDTO patchMethodEndpoint) {
        this.patchMethodEndpoint = patchMethodEndpoint;
    }

    @JsonProperty("OPTIONS")
    public EndpointListRouteDTO getOptionsMethodEndpoint() {
        return optionsMethodEndpoint;
    }

    public void setOptionsMethodEndpoint(EndpointListRouteDTO optionsMethodEndpoint) {
        this.optionsMethodEndpoint = optionsMethodEndpoint;
    }
}
