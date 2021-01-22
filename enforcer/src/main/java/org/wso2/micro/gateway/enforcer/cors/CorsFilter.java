package org.wso2.micro.gateway.enforcer.cors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.micro.gateway.enforcer.Filter;
import org.wso2.micro.gateway.enforcer.api.RequestContext;
import org.wso2.micro.gateway.enforcer.api.config.APIConfig;
import org.wso2.micro.gateway.enforcer.api.config.ResourceConfig;

/**
 * Cors Filter for failed preflight requests.
 */
public class CorsFilter implements Filter {
    // private static final Log log = LogFactory.getLog(CorsFilter.class);
    @Override
    public void init(APIConfig apiConfig) {

    }

    @Override
    public boolean handleRequest(RequestContext requestContext) {
        System.out.println("Cors Filter applied. ");
        System.out.println(requestContext.getRequestMethod());
        System.out.println(requestContext.getHeaders().get("access-control-request-method"));
        // TODO: (VirajSalaka) Provide this for all the OPTION calls ?
        //  (Since this filter handles cors failures only)
        // Preflight request
        if (requestContext.getRequestMethod().contains("OPTIONS")
                && requestContext.getHeaders().get("access-control-request-method") != null) {
            requestContext.getProperties().put("code", 204);
            StringBuilder allowedMethodsBuilder = new StringBuilder("OPTIONS");
            for (ResourceConfig resourceConfig : requestContext.getMathedAPI().getAPIConfig().getResources()) {
                if (resourceConfig.getMethod() != ResourceConfig.HttpMethods.OPTIONS) {
                    allowedMethodsBuilder.append(", " + resourceConfig.getMethod().name());
                }
            }
            requestContext.addResponseHeaders("allow", allowedMethodsBuilder.toString());
            // The resource's OPTION call is not handled from here.
            System.out.println("Added Code");
            return false;
        }
        return true;
    }
}
