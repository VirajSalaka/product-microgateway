package org.wso2.apimgt.gateway.cli.model.rest.route;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * This class hold the available endpoints, transport_type and config details (in the routes.yaml)
 */
public class EndpointListRouteDTO {
    private List<String> endpointList = null;
    private TransportTypeEnum transport = null;
    private EndpointConfigRouteDTO config = null;

    @JsonProperty("endpoints")
    public List<String> getEndpointList() {
        return endpointList;
    }

    public void setEndpointList(List<String> endpointList) {
        this.endpointList = endpointList;
    }

    @JsonProperty("transport")
    public TransportTypeEnum getTransport() {
        return transport;
    }

    public void setTransport(TransportTypeEnum type) {
        this.transport = type;
    }

    @JsonProperty("config")
    public EndpointConfigRouteDTO getConfig() {
        return config;
    }

    public void setConfig(EndpointConfigRouteDTO config) {
        this.config = config;
    }
}
