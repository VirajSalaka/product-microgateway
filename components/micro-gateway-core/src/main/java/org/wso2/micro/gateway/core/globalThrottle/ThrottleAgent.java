package org.wso2.micro.gateway.core.globalThrottle;

import org.ballerinalang.jvm.values.api.BMap;
import org.wso2.micro.gateway.core.globalThrottle.databridge.agent.conf.AgentConfiguration;
import org.wso2.micro.gateway.core.globalThrottle.databridge.publisher.PublisherConfiguration;
import org.wso2.micro.gateway.core.globalThrottle.databridge.publisher.ThrottleDataPublisher;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is used for ballerina interop invocations related to Global Throttle Event Publishing
 * via binary communication.
 */
public class ThrottleAgent {

    private static ThrottleDataPublisher throttleDataPublisher = null;

    public static void setTMBinaryAgentConfiguration(BMap<String, Object> publisherConfiguration) {
        AgentConfiguration.getInstance().setConfiguration(publisherConfiguration);
    }

    public static void setTMBinaryPublisherConfiguration(BMap<String, Object> publisherConfiguration) {
        PublisherConfiguration.getInstance().setConfiguration(publisherConfiguration);
    }

    public static void startThrottlePublisherPool() {
        throttleDataPublisher = new ThrottleDataPublisher();
    }

    public static void publishNonThrottledEvent(BMap<String, String> throttleEvent) {
        throttleDataPublisher.publishNonThrottledEvent(throttleEvent);
    }
}
