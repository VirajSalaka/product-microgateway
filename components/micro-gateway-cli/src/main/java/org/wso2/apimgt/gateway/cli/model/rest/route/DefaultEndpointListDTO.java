package org.wso2.apimgt.gateway.cli.model.rest.route;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DefaultEndpointListDTO extends EndpointListRouteDTO {
    private String endpoint = null;

    @JsonProperty("endpoint")
    public String setEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void addEndpoint(String endpoint){
        setEndpoint(endpoint);
    }
}
