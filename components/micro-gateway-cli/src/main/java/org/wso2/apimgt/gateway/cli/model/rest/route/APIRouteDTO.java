package org.wso2.apimgt.gateway.cli.model.rest.route;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * This class holds the all apis in the microgateway (in routes.yaml)
 */
public class APIRouteDTO {
    private String apiName = null;
    private List<APIVersionRouteDTO> apiVersionList = null;

    @JsonProperty("API_name")
    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    @JsonProperty("versions")
    public List<APIVersionRouteDTO> getApiVersionList() {
        return apiVersionList;
    }

    public void setApiVersionList(List<APIVersionRouteDTO> apiVersionList) {
        this.apiVersionList = apiVersionList;
    }

    public void addAPIVersion(APIVersionRouteDTO apiVersionRouteDTO){
        if(apiVersionList == null){
            apiVersionList = new ArrayList<>();
        }
        //todo: handle if existing version is added
        apiVersionList.add(apiVersionRouteDTO);
    }

    public APIVersionRouteDTO findByAPIVersion(String apiVersion){
        if(apiVersionList == null){
            return null;
        }

        for(APIVersionRouteDTO api : apiVersionList){
            if(api.getVersion().equals(apiVersion)){
                return api;
            }
        }
        return null;
    }
}
