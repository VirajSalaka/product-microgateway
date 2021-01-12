package org.wso2.micro.gateway.enforcer.cors;

import org.wso2.micro.gateway.enforcer.Filter;
import org.wso2.micro.gateway.enforcer.api.RequestContext;
import org.wso2.micro.gateway.enforcer.api.config.APIConfig;

public class CorsFilter implements Filter {
    @Override
    public void init(APIConfig apiConfig) {

    }

    @Override
    public boolean handleRequest(RequestContext requestContext) {
        // the enforcer will only handle the preflight requests.
        // Simple requests should be handled from the cors filter within router.
        if (requestContext.getRequestMethod().equals("OPTIONS")) {
            if (requestContext.getHeaders().get("allow") != null) {
                requestContext.getResponseHeaders().put("allow", requestContext.getHeaders().get("allow"));
            }
            CorsHeaderGenerator.process(requestContext, false);
            requestContext.getProperties().put("code", 200);
        }
        return false;
    }
}
