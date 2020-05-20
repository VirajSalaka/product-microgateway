/*
 *  Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.micro.gateway.core.globalThrottle.databridge.agent.conf;

import org.ballerinalang.jvm.values.api.BMap;
import org.wso2.micro.gateway.core.globalThrottle.databridge.agent.util.DataAgentConstants;
import org.wso2.micro.gateway.core.globalThrottle.databridge.agent.util.DataEndpointConstants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Data agent configuration.
 */
public class AgentConfiguration {

    private AgentConfiguration() {
    }

    private String publishingStrategy = DataEndpointConstants.ASYNC_STRATEGY;
    private String trustStorePath =
            preProcessTrustStorePath("${mgw-runtime.home}/runtime/bre/security/ballerinaTruststore.p12");
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
    private String ciphers = "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256,TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256," +
            "TLS_DHE_RSA_WITH_AES_128_CBC_SHA256,TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA," +
            "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA,TLS_DHE_RSA_WITH_AES_128_CBC_SHA," +
            "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256," +
            "TLS_DHE_RSA_WITH_AES_128_GCM_SHA256";

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

    public String getCiphers() {
        return ciphers;
    }

    public String getPublishingStrategy() {
        return publishingStrategy;
    }

    public void setTrustStorePath(String trustStorePath) {
        this.trustStorePath = trustStorePath;
    }

    public void setTrustStorePassword(String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
    }

    public void setPublishingStrategy(String publishingStrategy) {
        this.publishingStrategy = publishingStrategy;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public void setSocketTimeoutMS(int socketTimeoutMS) {
        this.socketTimeoutMS = socketTimeoutMS;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public void setKeepAliveTimeInPool(int keepAliveTimeInPool) {
        this.keepAliveTimeInPool = keepAliveTimeInPool;
    }

    public void setReconnectionInterval(int reconnectionInterval) {
        this.reconnectionInterval = reconnectionInterval;
    }

    public void setMaxTransportPoolSize(int maxTransportPoolSize) {
        this.maxTransportPoolSize = maxTransportPoolSize;
    }

    public void setMaxIdleConnections(int maxIdleConnections) {
        this.maxIdleConnections = maxIdleConnections;
    }

    public void setEvictionTimePeriod(int evictionTimePeriod) {
        this.evictionTimePeriod = evictionTimePeriod;
    }

    public void setMinIdleTimeInPool(int minIdleTimeInPool) {
        this.minIdleTimeInPool = minIdleTimeInPool;
    }

    public void setSecureMaxTransportPoolSize(int secureMaxTransportPoolSize) {
        this.secureMaxTransportPoolSize = secureMaxTransportPoolSize;
    }

    public void setSecureMaxIdleConnections(int secureMaxIdleConnections) {
        this.secureMaxIdleConnections = secureMaxIdleConnections;
    }

    public void setSecureEvictionTimePeriod(int secureEvictionTimePeriod) {
        this.secureEvictionTimePeriod = secureEvictionTimePeriod;
    }

    public void setSecureMinIdleTimeInPool(int secureMinIdleTimeInPool) {
        this.secureMinIdleTimeInPool = secureMinIdleTimeInPool;
    }

    public void setSslEnabledProtocols(String sslEnabledProtocols) {
        this.sslEnabledProtocols = sslEnabledProtocols;
    }

    public void setCiphers(String ciphers) {
        this.ciphers = ciphers;
    }

    @Override
    public String toString() {
        return ", PublishingStrategy : " + publishingStrategy +
                "TrustSorePath" + trustStorePath +
                "TrustSorePassword" + trustStorePassword +
                "QueueSize" + queueSize +
                "BatchSize" + batchSize +
                "CorePoolSize" + corePoolSize +
                "SocketTimeoutMS" + socketTimeoutMS +
                "MaxPoolSize" + maxPoolSize +
                "KeepAliveTimeInPool" + keepAliveTimeInPool +
                "ReconnectionInterval" + reconnectionInterval +
                "MaxTransportPoolSize" + maxTransportPoolSize +
                "MaxIdleConnections" + maxIdleConnections +
                "EvictionTimePeriod" + evictionTimePeriod +
                "MinIdleTimeInPool" + minIdleTimeInPool +
                "SecureMaxTransportPoolSize" + secureMaxTransportPoolSize +
                "SecureMaxIdleConnections" + secureMaxIdleConnections +
                "SecureEvictionTimePeriod" + secureEvictionTimePeriod +
                "SecureMinIdleTimeInPool" + secureMinIdleTimeInPool +
                "SSLEnabledProtocols" + sslEnabledProtocols +
                "Ciphers" + ciphers;
    }

    private static class InnerAgentConfiguration {
        private static final AgentConfiguration instance = new AgentConfiguration();
    }

    public static AgentConfiguration getInstance() {
        return InnerAgentConfiguration.instance;
    }

    public void setConfiguration(BMap<String, Object> configuration) {
        String trustStorePath = String.valueOf(configuration.get(DataAgentConstants.TRUST_STORE_PATH));
        //TrustStore path provided from the microgateway configuration needs to be preprocessed.
        String resolvedTrustStorePath = preProcessTrustStorePath(trustStorePath);

        this.trustStorePath = resolvedTrustStorePath;
        this.trustStorePassword = String.valueOf(configuration.get(DataAgentConstants.TRUST_STORE_PASSWORD));
        this.sslEnabledProtocols = String.valueOf(configuration.get(DataAgentConstants.SSL_ENABLED_PROTOCOLS));
        this.ciphers = String.valueOf(configuration.get(DataAgentConstants.CIPHERS));

        try {
            this.queueSize = Math.toIntExact((long) configuration.get(DataAgentConstants.QUEUE_SIZE));
            this.batchSize = Math.toIntExact((long) configuration.get(DataAgentConstants.BATCH_SIZE));
            this.corePoolSize = Math.toIntExact((long) configuration.get(DataAgentConstants.CORE_POOL_SIZE));
            this.socketTimeoutMS = Math.toIntExact((long) configuration.get(DataAgentConstants.SOCKET_TIMEOUT_MS));
            this.maxPoolSize = Math.toIntExact((long) configuration.get(DataAgentConstants.MAX_POOL_SIZE));
            this.keepAliveTimeInPool = Math.toIntExact((long) configuration
                    .get(DataAgentConstants.KEEP_ALIVE_TIME_INTERVAL_IN_POOL));
            this.reconnectionInterval = Math.toIntExact((long) configuration
                    .get(DataAgentConstants.RECONNECTION_INTERVAL));
            this.maxTransportPoolSize = Math.toIntExact((long) configuration.get(DataAgentConstants.MAX_TRANSPORT_POOL_SIZE));
            this.maxIdleConnections = Math.toIntExact((long) configuration
                    .get(DataAgentConstants.MAX_IDLE_CONNECTIONS));
            this.evictionTimePeriod = Math.toIntExact((long) configuration
                    .get(DataAgentConstants.EVICTION_TIME_PERIOD));
            this.minIdleTimeInPool = Math.toIntExact((long) configuration
                    .get(DataAgentConstants.MIN_IDLE_TIME_IN_POOL));
            this.secureMaxTransportPoolSize = Math.toIntExact((long) configuration
                    .get(DataAgentConstants.SECURE_MAX_TRANSPORT_POOL_SIZE));
            this.secureMaxIdleConnections = Math.toIntExact((long) configuration
                    .get(DataAgentConstants.SECURE_MAX_IDLE_CONNECTIONS));
            this.secureEvictionTimePeriod = Math.toIntExact((long) configuration
                    .get(DataAgentConstants.SECURE_EVICTION_TIME_PERIOD));
            this.secureMinIdleTimeInPool = Math.toIntExact((long) configuration
                    .get(DataAgentConstants.SECURE_MIN_IDLE_TIME_IN_POOL));
        } catch (ArithmeticException e) {
            //todo: handle the error
        }

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
}
