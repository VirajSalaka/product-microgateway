package org.wso2.apimgt.gateway.cli.model.rest.route;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * This class hold the available endpoints, transport_type and config details (in the routes.yaml)
 */
public class EndpointListRouteDTO {
    private List<String> endpointList = null;
    private List<TransportTypeEnum> transport = null;
    private EndpointConfigRouteDTO config = null;

    @JsonProperty("endpoints")
    public List<String> getEndpointList() {
        return endpointList;
    }

    public void setEndpointList(List<String> endpointList) {
        this.endpointList = endpointList;
    }

    @JsonProperty("transport")
    public List<TransportTypeEnum> getTransport() {
        return transport;
    }

    public void setTransport(List<TransportTypeEnum> transportTypeEnumList) {
        this.transport = transportTypeEnumList;
    }

    @JsonProperty("config")
    public EndpointConfigRouteDTO getConfig() {
        return config;
    }

    public void setConfig(EndpointConfigRouteDTO config) {
        this.config = config;
    }

    public void addTransportType(TransportTypeEnum transportTypeEnum){
        if(transport == null){
            transport = new ArrayList<>();
        }
        if(!transport.contains(transportTypeEnum)){
            transport.add(transportTypeEnum);
        }
    }

    public void addEndpoint(String endpoint){
        if(endpointList == null){
            endpointList = new ArrayList<>();
        }
        //todo: validate if there are any duplicates
        endpointList.add(endpoint);
    }
}
