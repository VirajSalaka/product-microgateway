package org.wso2.apimgt.gateway.cli.model.rest.route;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

//todo: add constants
/**
 * This class hold the available endpoints, transport_type and securityConfig details (in the routes.yaml)
 */
@JsonTypeInfo(use = NAME, include = PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value=LoadBalanceEndpointListDTO.class, name = "load_balance"),
        @JsonSubTypes.Type(value=FailoverEndpointListDTO.class, name = "failover"),
        @JsonSubTypes.Type(value=DefaultEndpointListDTO.class, name = "default")
})
public abstract class EndpointListRouteDTO {

    private EndpointSecurityRouteDTO securityConfig = null;
    /*
    This field's purpose is to identify the type of Endpoint as we need to cast while reading the yaml
    //todo: but this will create a redundant type feature as "type" and "@type" as the jackson library is used
    //todo: solution: have all the methods exposed in this abstract class
     */
    private EndpointType type = null;

    @JsonProperty("securityConfig")
    public EndpointSecurityRouteDTO getSecurityConfig() {
        return securityConfig;
    }

    public void setSecurityConfig(EndpointSecurityRouteDTO securityConfig) {
        this.securityConfig = securityConfig;

    }

    public abstract void addEndpoint(String endpoint);

    @JsonProperty("type")
    public EndpointType getType() {
        return type;
    }

    public void setType(EndpointType type) {
        this.type = type;
    }


}
