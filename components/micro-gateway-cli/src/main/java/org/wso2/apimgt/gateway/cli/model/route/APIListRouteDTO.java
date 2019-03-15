package org.wso2.apimgt.gateway.cli.model.route;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.wso2.apimgt.gateway.cli.exception.CLIInternalException;
import org.wso2.apimgt.gateway.cli.exception.CLIRuntimeException;

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

        //todo: test the function
        //check if the there is an api under the provided name
        if(!apiRouteList.stream().anyMatch(api -> api.getApiName().equals(apiRouteDTO.getApiName()))){
            apiRouteList.add(apiRouteDTO);
        }
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
