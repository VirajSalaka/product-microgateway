package org.wso2.apimgt.gateway.cli.model.rest.route;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class holds the route details for a single version of the API (in the routes.yaml)
 */
public class APIVersionRouteDTO {
    private String version = null;
    private EnvDTO prodLoadBalance = null;
    private EnvDTO prodFailover = null;
    private EnvDTO sandboxLoadBalance = null;
    private EnvDTO sandboxFailover = null;

    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @JsonProperty("prod_load_balance")
    public EnvDTO getProdLoadBalance() {
        return prodLoadBalance;
    }

    public void setProdLoadBalance(EnvDTO prodLoadBalance) {
        this.prodLoadBalance = prodLoadBalance;
    }

    @JsonProperty("prod_failover")
    public EnvDTO getProdFailover() {
        return prodFailover;
    }

    public void setProdFailover(EnvDTO prodFailover) {
        this.prodFailover = prodFailover;
    }

    @JsonProperty("sandbox_load_balance")
    public EnvDTO getSandboxLoadBalance() {
        return sandboxLoadBalance;
    }

    public void setSandboxLoadBalance(EnvDTO sandboxLoadBalance) {
        this.sandboxLoadBalance = sandboxLoadBalance;
    }

    @JsonProperty("sandbox_failover")
    public EnvDTO getSandboxFailover() {
        return sandboxFailover;
    }

    public void setSandboxFailover(EnvDTO sandboxFailover) {
        this.sandboxFailover = sandboxFailover;
    }
}
