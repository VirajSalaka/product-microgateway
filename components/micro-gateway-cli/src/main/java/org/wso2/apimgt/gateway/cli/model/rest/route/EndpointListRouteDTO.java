package org.wso2.apimgt.gateway.cli.model.rest.route;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class hold the available endpoints, transport_type and config details (in the routes.yaml)
 */
public abstract class EndpointListRouteDTO {

    private EndpointConfigRouteDTO config = null;
    /*
    This field's purpose is to identify the type of Endpoint as we need to cast while reading the yaml
    //todo: but this will create a redundant type feature as "type" and "@type" as the jackson library is used
    //todo: solution: have all the methods exposed in this abstract class
     */
    private EndpointType type = null;

    @JsonProperty("config")
    public EndpointConfigRouteDTO getConfig() {
        return config;
    }

    public void setConfig(EndpointConfigRouteDTO config) {
        this.config = config;
    }

    public abstract void addEndpoint(String endpoint);

    @JsonProperty("type")
    public EndpointType getType() {
        return type;
    }

    public void setType(EndpointType type) {
        this.type = type;
    }
}
