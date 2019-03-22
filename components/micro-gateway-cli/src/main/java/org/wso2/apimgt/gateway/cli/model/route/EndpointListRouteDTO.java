package org.wso2.apimgt.gateway.cli.model.route;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.wso2.apimgt.gateway.cli.model.rest.APIEndpointSecurityDTO;
import java.util.List;

//todo: add constants
/**
 * This class hold the available endpoints, transport_type and securityConfig details (in the routes.yaml)
 */
public class EndpointListRouteDTO {

    private APIEndpointSecurityDTO securityConfig = null;
    private EndpointType type = null;
    private List<String> endpointList = null;

    @JsonProperty("securityConfig")
    public APIEndpointSecurityDTO getSecurityConfig() {
        return securityConfig;
    }

    public void setSecurityConfig(APIEndpointSecurityDTO securityConfig) {
        this.securityConfig = securityConfig;
    }

    @JsonProperty("type")
    public EndpointType getType() {
        return type;
    }

    public void setType(EndpointType type) {
        this.type = type;
    }

    @JsonProperty("endpoints")
    public List<String> getEndpointList() {
        return endpointList;
    }

    public void setEndpointList(List<String> endpointList) {
        this.endpointList = endpointList;
    }

    //todo: add "add endpoint" method
}
