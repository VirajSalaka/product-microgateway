package org.wso2.micro.gateway.core.databridge.agent;


import org.ballerinalang.jvm.values.api.BMap;
import org.wso2.carbon.databridge.commons.utils.DataBridgeCommonsUtils;
import org.wso2.micro.gateway.core.databridge.agent.exception.DataEndpointAgentConfigurationException;
import org.wso2.micro.gateway.core.databridge.agent.util.DataAgentConstants;
import org.wso2.micro.gateway.core.databridge.agent.util.DataEndpointConstants;

public class AgentConfigurationHolder {

    //TODO: introduce constants
    //TODO: remove the field
    private String name = "Binary";
    //TODO: remove the field
    private String dataEndpointClass = "org.wso2.micro.gateway.core.databridge.agent.endpoint.binary" +
            ".BinaryDataEndpoint";
    private String publishingStrategy = "async";
    //TODO: change the location
    private String trustStorePath = "/Users/viraj/mgw_workspace/webinar-grpc/wso2am-micro-gw-macos-3.1.0/" +
            "runtime/bre/security/ballerinaTruststore.p12";
    private String trustStorePassword = "ballerina";
    private int queueSize = 32768;
    private int batchSize = 200;
    private int corePoolSize = 1;
    private int socketTimeoutMS = 30000;
    private int maxPoolSize = 1;
    private int keepAliveTimeInPool = 20;
    private int reconnectionInterval = 30;
    private int maxTransportPoolSize = 250;
    private int maxIdleConnections = 250;
    private int evictionTimePeriod = 5500;
    private int minIdleTimeInPool = 5000;
    private int secureMaxTransportPoolSize = 250;
    private int secureMaxIdleConnections = 250;
    private int secureEvictionTimePeriod = 5500;
    private int secureMinIdleTimeInPool = 5000;
    private String sslEnabledProtocols = "TLSv1,TLSv1.1,TLSv1.2";

    private AgentConfigurationHolder() {}

    public String getName() {
        return name;
    }

    public String getDataEndpointClass() {
        return dataEndpointClass;
    }

    public String getPublishingStrategy() {
        return publishingStrategy;
    }

    public String getTrustStorePath() {
        return trustStorePath;
    }

    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public int getSocketTimeoutMS() {
        return socketTimeoutMS;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public int getKeepAliveTimeInPool() {
        return keepAliveTimeInPool;
    }

    public int getReconnectionInterval() {
        return reconnectionInterval;
    }

    public int getMaxTransportPoolSize() {
        return maxTransportPoolSize;
    }

    public int getMaxIdleConnections() {
        return maxIdleConnections;
    }

    public int getEvictionTimePeriod() {
        return evictionTimePeriod;
    }

    public int getMinIdleTimeInPool() {
        return minIdleTimeInPool;
    }

    public int getSecureMaxTransportPoolSize() {
        return secureMaxTransportPoolSize;
    }

    public int getSecureMaxIdleConnections() {
        return secureMaxIdleConnections;
    }

    public int getSecureEvictionTimePeriod() {
        return secureEvictionTimePeriod;
    }

    public int getSecureMinIdleTimeInPool() {
        return secureMinIdleTimeInPool;
    }

    public String getSslEnabledProtocols() {
        return sslEnabledProtocols;
    }

    private static class InnerAgentConfigHolder {
        private static AgentConfigurationHolder instance = new AgentConfigurationHolder();
        //TODO: assign truststore from system variable here
    }

    public static AgentConfigurationHolder getInstance() {
        return InnerAgentConfigHolder.instance;
    }

    private boolean isPropertyAvailable(BMap<String,String> configMap, String key) {
        return configMap.containsKey(key) && !configMap.get(key).trim().isEmpty();
    }

    public void setConfigurationValues(BMap<String,String> configMap) {
        //TODO: remove if async is mandatory
        if(isPropertyAvailable(configMap, DataAgentConstants.PUBLISHING_STRATEGY)) {
            publishingStrategy = configMap.get(DataAgentConstants.PUBLISHING_STRATEGY);
        }
        trustStorePath = configMap.get(DataAgentConstants.TRUST_STORE_PATH);
        trustStorePassword = configMap.get(DataAgentConstants.TRUST_STORE_PASSWORD);
    }

    //TODO: Implement the body
    public void setProperties() {

    }

}
