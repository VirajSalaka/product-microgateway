package org.wso2.micro.gateway.core.globalThrottle;

import org.wso2.micro.gateway.core.databridge.agent.AgentHolder;
import org.wso2.micro.gateway.core.databridge.throttling.publisher.ThrottleDataPublisher;

public class ThrottleAgent {

    private static ThrottleDataPublisher throttleDataPublisher = null;

    public static void startThrottlePublisherPool() {
        //todo:provided hard coded config path
        //AgentHolder.setConfigPath("/Users/viraj/Desktop/data-agent-config.yaml");
        System.setProperty("javax.net.ssl.trustStore", "/Users/viraj/mgw_workspace/webinar-grpc/wso2am-micro-gw-macos-3.1.0/runtime/bre/security/ballerinaTruststore.p12");
        System.setProperty("javax.net.ssl.trustStorePassword", "ballerina");

        throttleDataPublisher = new ThrottleDataPublisher();
    }

    public static void publishNonThrottledEvent(
            String applicationLevelThrottleKey, String applicationLevelTier,
            String apiLevelThrottleKey, String apiLevelTier,
            String subscriptionLevelThrottleKey, String subscriptionLevelTier,
            String resourceLevelThrottleKey, String resourceLevelTier,
            String authorizedUser, String apiContext, String apiVersion, String appTenant, String apiTenant,
            String appId, String apiName, String messageId) {
        throttleDataPublisher.publishNonThrottledEvent( applicationLevelThrottleKey,  applicationLevelTier,
                 apiLevelThrottleKey,  apiLevelTier,
                 subscriptionLevelThrottleKey,  subscriptionLevelTier,
                 resourceLevelThrottleKey,  resourceLevelTier,
                 authorizedUser,  apiContext,  apiVersion,  appTenant,  apiTenant,
                 appId,  apiName,  messageId);
    }
}