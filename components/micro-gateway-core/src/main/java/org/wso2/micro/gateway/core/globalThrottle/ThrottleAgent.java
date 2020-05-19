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

    public static void setTMBinaryAgentConfiguration(String trustStorePath, String trustStorePassword,
                                                     int queueSize, int batchSize, int corePoolSize,
                                                     int socketTimeoutMS, int maxPoolSize, int keepAliveTimeInPool,
                                                     int reconnectionInterval, int maxTransportPoolSize,
                                                     int maxIdleConnections, int evictionTimePeriod,
                                                     int minIdleTimeInPool, int secureMaxTransportPoolSize,
                                                     int secureMaxIdleConnections, int secureEvictionTimePeriod,
                                                     int secureMinIdleTimeInPool, String sslEnabledProtocols,
                                                     String ciphers) {
        //TrustStore path provided from the microgateway configuration needs to be preprocessed.
        String resolvedTrustStorePath = preProcessTrustStorePath(trustStorePath);

        AgentConfiguration.getInstance().setConfiguration(resolvedTrustStorePath, trustStorePassword, queueSize,
                batchSize, corePoolSize, socketTimeoutMS, maxPoolSize, keepAliveTimeInPool, reconnectionInterval,
                maxTransportPoolSize, maxIdleConnections, evictionTimePeriod, minIdleTimeInPool,
                secureMaxTransportPoolSize, secureMaxIdleConnections, secureEvictionTimePeriod,
                secureMinIdleTimeInPool, sslEnabledProtocols, ciphers);
    }

    public static void setTMBinaryPublisherConfiguration(String receiverURLGroup, String authURLGroup,
                                                         String userName, String password,
                                                         int maxIdleDataPublishingAgents,
                                                         int initIdleObjectDataPublishingAgents,
                                                         int publisherThreadPoolCoreSize,
                                                         int publisherThreadPoolMaximumSize,
                                                         int publisherThreadPoolKeepAliveTime) {
        PublisherConfiguration.getInstance().setConfiguration(receiverURLGroup, authURLGroup, userName, password,
                maxIdleDataPublishingAgents, initIdleObjectDataPublishingAgents, publisherThreadPoolCoreSize,
                publisherThreadPoolMaximumSize, publisherThreadPoolKeepAliveTime);
    }

    public static void startThrottlePublisherPool() {
        throttleDataPublisher = new ThrottleDataPublisher();
    }

    /**
     * The Truststore path provided from the ballerina implementation could be associated with a system property.
     * It needs to substituted with relevant system property.
     * e.g. ${mgw-runtime.home}/runtime/bre/security/ballerinaTruststore.p12
     *
     * @param mgwTrustStorePath trustStorePath as provided by the microgateway configuration
     * @return resolved trustStorePath
     */
    private static String preProcessTrustStorePath(String mgwTrustStorePath) {
        String placeHolderRegex = "\\$\\{.*\\}";
        Pattern placeHolderPattern = Pattern.compile(placeHolderRegex);
        Matcher placeHolderMatcher = placeHolderPattern.matcher(mgwTrustStorePath);
        if (placeHolderMatcher.find()) {
            String placeHolder = placeHolderMatcher.group(0);
            //to remove additional symbols
            String systemPropertyKey = placeHolder.substring(2, placeHolder.length() - 1);
            return placeHolderMatcher.replaceFirst(System.getProperty(systemPropertyKey));
        }
        return mgwTrustStorePath;
    }

    public static void publishNonThrottledEvent(BMap<String, String> throttleEvent) {
        throttleDataPublisher.publishNonThrottledEvent(throttleEvent);
    }
}
