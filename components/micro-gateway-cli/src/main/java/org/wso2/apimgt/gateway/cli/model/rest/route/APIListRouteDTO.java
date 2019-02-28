package org.wso2.apimgt.gateway.cli.model.rest.route;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class APIListRouteDTO {
    private List<APIRouteDTO> apiRouteList;

    @JsonProperty("apis")
    public List<APIRouteDTO> getApiRouteList() {
        return apiRouteList;
    }

    public void setApiRouteList(List<APIRouteDTO> apiRouteList) {
        this.apiRouteList = apiRouteList;
    }
}
