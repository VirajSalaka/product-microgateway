package org.wso2.apimgt.gateway.cli.model.rest.route;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class LoadBalanceEndpointListDTO extends EndpointListRouteDTO{
    private List<String> endpointList = null;

    @JsonProperty("endpoints")
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
}
