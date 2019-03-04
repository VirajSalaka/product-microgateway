package org.wso2.apimgt.gateway.cli.model.rest.route;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class FailoverEndpointListDTO extends EndpointListRouteDTO{
    private String defaultEndpoint = null;

    private List<String> endpointList = null;

    @JsonProperty("failover_endpoints")
    public List<String> getEndpointList() {
        return endpointList;
    }

    public void setEndpointList(List<String> endpointList) {
        this.endpointList = endpointList;
    }

    public void addEndpoint(String endpoint){
        if(endpointList == null){
            endpointList = new ArrayList<>();
        }
        //todo: validate if there are any duplicates
        endpointList.add(endpoint);
    }

    @JsonProperty("default_endpoint")
    public String getDefaultEndpoint() {
        return defaultEndpoint;
    }

    public void setDefaultEndpoint(String defaultEndpoint) {
        this.defaultEndpoint = defaultEndpoint;
    }
}
