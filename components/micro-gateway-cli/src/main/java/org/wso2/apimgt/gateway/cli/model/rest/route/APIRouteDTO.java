package org.wso2.apimgt.gateway.cli.model.rest.route;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * This class holds the all apis in the microgateway (in routes.yaml)
 */
public class APIRouteDTO {
    private String API_name = null;
    private List<APIVersionRouteDTO> apiVersionList = null;

    @JsonProperty("API_name")
    public String getAPI_name() {
        return API_name;
    }

    public void setAPI_name(String API_name) {
        this.API_name = API_name;
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
}
