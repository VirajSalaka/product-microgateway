package org.wso2.apimgt.gateway.cli.model.rest.route;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class APIListRouteDTO {
    private List<APIRouteDTO> apiRouteList = null;

    @JsonProperty("apis")
    public List<APIRouteDTO> getApiRouteList() {
        return apiRouteList;
    }

    public void setApiRouteList(List<APIRouteDTO> apiRouteList) {
        this.apiRouteList = apiRouteList;
    }

    public void addAPIDTO(APIRouteDTO apiRouteDTO){
        if(apiRouteList == null){
            apiRouteList = new ArrayList<>();
        }
        //todo: handle adding an existing apiName
        apiRouteList.add(apiRouteDTO);
    }

    public APIRouteDTO findByAPIName(String apiName){
        if(apiRouteList == null){
            return null;
        }

        for(APIRouteDTO api : apiRouteList){
            if(api.getApiName().equals(apiName)){
                return api;
            }
        }
        return null;
    }
}
