package org.wso2.apimgt.gateway.cli.model.rest.route;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * This class holds the route details for a single version of the API (in the routes.yaml)
 */
public class APIVersionRouteDTO {
    private String version = null;
    private EnvDTO prod = null;
    private EnvDTO sandbox = null;

    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @JsonProperty("production") //todo: provide a constant
    public EnvDTO getProd() {
        return prod;
    }

    public void setProd(EnvDTO prod) {
        this.prod = prod;
    }

    @JsonProperty("sandbox") //todo: provide a constant
    public EnvDTO getSandbox() {
        return sandbox;
    }

    public void setSandbox(EnvDTO sandbox) {
        this.sandbox = sandbox;
    }
}
