package org.wso2.apimgt.gateway.cli.model.rest.route;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * This class represents the endpoint configurations for the given environment. (in the routes.yaml)
 */
public class EnvDTO {
    private EndpointListRouteDTO basicEndpoint = null;
    private List<ResourceRouteDTO> resourceRouteDTOList = null;

    @JsonProperty("basicURL")
    public EndpointListRouteDTO getBasicEndpoint() {
        return basicEndpoint;
    }

    public void setBasicEndpoint(EndpointListRouteDTO basicEndpoint) {
        this.basicEndpoint = basicEndpoint;
    }

    @JsonProperty("resources")
    public List<ResourceRouteDTO> getResourceRouteDTOList() {
        return resourceRouteDTOList;
    }

    public void setResourceRouteDTOList(List<ResourceRouteDTO> resourceRouteDTOList) {
        this.resourceRouteDTOList = resourceRouteDTOList;
    }
}
