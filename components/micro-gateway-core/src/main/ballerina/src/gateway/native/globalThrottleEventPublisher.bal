// Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

import ballerinax/java;
import ballerina/config;

# Initialize the Binary Throttle Data Publisher
public function initBinaryThrottleDataPublisher() {
    loadTMBinaryAgentConfiguration();
    loadTMBinaryPublisherConfiguration();
    jinitBinaryThrottleDataPublisher();
}

# Publish the throttleEvent
# +throttleEvent - throttle Event
public function publishBinaryGlobalThrottleEvent(RequestStreamDTO throttleEvent) {
    string messageId = throttleEvent.messageID;
    string applicationLevelThrottleKey = throttleEvent.appKey;
    string apiLevelThrottleKey = throttleEvent.apiKey;
    string applicationLevelTier = throttleEvent.appTier;
    string apiLevelTier = throttleEvent.apiTier;
    string subscriptionLevelThrottleKey = throttleEvent.subscriptionKey;
    string subscriptionLevelTier = throttleEvent.subscriptionTier;
    string resourceLevelThrottleKey = throttleEvent.resourceKey;
    string resourceLevelTier = throttleEvent.resourceTier;
    string authorizedUser = throttleEvent.userId;
    string apiContext = throttleEvent.apiContext;
    string apiVersion = throttleEvent.apiVersion;
    string appTenant = throttleEvent.appTenant;
    string apiTenant = throttleEvent.apiTenant;
    string appId = throttleEvent.appId;
    string apiName = throttleEvent.apiName;
    string properties = throttleEvent.properties;

    jPublishGlobalThrottleEvent(throttleEvent);
}

# set configurations related to binary publisher
function loadTMBinaryPublisherConfiguration() {
    //todo: input validation from ballerina layer
    string receiverURLGroup;
    string authURLGroup;
    [receiverURLGroup, authURLGroup] = processTMPublisherURLGroup();
    string username = getConfigValue(BINARY_PUBLISHER_THROTTLE_CONF_INSTANCE_ID, TM_USERNAME, DEFAULT_TM_USERNAME);
    string password = getConfigValue(BINARY_PUBLISHER_THROTTLE_CONF_INSTANCE_ID, TM_PASSWORD, DEFAULT_TM_PASSWORD);
    int publisherPoolMaxIdle = getConfigIntValue(BINARY_PUBLISHER_POOL_THROTTLE_CONF_INSTANCE_ID,
        TM_PUBLISHER_POOL_MAX_IDLE, DEFAULT_TM_PUBLISHER_POOL_MAX_IDLE);
    int publisherPoolInitIdleCapacity = getConfigIntValue(BINARY_PUBLISHER_POOL_THROTTLE_CONF_INSTANCE_ID,
        TM_PUBLISHER_POOL_INIT_IDLE_CAPACITY, DEFAULT_TM_PUBLISHER_POOL_INIT_IDLE_CAPACITY);
    int publisherThreadPoolCoreSize = getConfigIntValue(BINARY_PUBLISHER_THREAD_POOL_THROTTLE_CONF_INSTANCE_ID,
        TM_PUBLISHER_THREAD_POOL_CORE_SIZE, DEFAULT_TM_PUBLISHER_THREAD_POOL_CORE_SIZE);
    int publisherThreadPoolMaximumSize = getConfigIntValue(BINARY_PUBLISHER_THREAD_POOL_THROTTLE_CONF_INSTANCE_ID,
        TM_PUBLISHER_THREAD_POOL_MAXIMUM_SIZE, DEFAULT_TM_PUBLISHER_THREAD_POOL_MAXIMUM_SIZE);
    int publisherThreadPoolKeepAliveTime = getConfigIntValue(BINARY_PUBLISHER_THREAD_POOL_THROTTLE_CONF_INSTANCE_ID,
        TM_PUBLISHER_THREAD_POOL_KEEP_ALIVE_TIME, DEFAULT_TM_PUBLISHER_THREAD_POOL_KEEP_ALIVE_TIME);

    jSetTMBinaryPublisherConfiguration(java:fromString(receiverURLGroup), java:fromString(authURLGroup),
        java:fromString(username), java:fromString(password), publisherPoolMaxIdle, publisherPoolInitIdleCapacity,
        publisherThreadPoolCoreSize, publisherThreadPoolMaximumSize, publisherThreadPoolKeepAliveTime);
}

# set configurations related to binary agent
function loadTMBinaryAgentConfiguration() {
    //todo: add debug logs to see the configurations passed
    //todo: validate if the value is a power of 2
    int queueSize = getConfigIntValue(BINARY_AGENT_THROTTLE_CONF_INSTANCE_ID, TM_AGENT_QUEUE_SIZE,
        DEFAULT_TM_AGENT_QUEUE_SIZE);
    int batchSize = getConfigIntValue(BINARY_AGENT_THROTTLE_CONF_INSTANCE_ID, TM_AGENT_BATCH_SIZE,
        DEFAULT_TM_AGENT_BATCH_SIZE);
    int corePoolSize = getConfigIntValue(BINARY_AGENT_THROTTLE_CONF_INSTANCE_ID, TM_AGENT_THREAD_POOL_CORE_SIZE,
        DEFAULT_TM_AGENT_THREAD_POOL_CORE_SIZE);
    int socketTimeoutMS = getConfigIntValue(BINARY_AGENT_THROTTLE_CONF_INSTANCE_ID,  TM_AGENT_SOCKET_TIMEOUT_MS,
        DEFAULT_TM_AGENT_SOCKET_TIMEOUT_MS);
    int maxPoolSize = getConfigIntValue(BINARY_AGENT_THROTTLE_CONF_INSTANCE_ID, TM_AGENT_THREAD_POOL_MAXIMUM_SIZE,
        DEFAULT_TM_AGENT_THREAD_POOL_MAXIMUM_SIZE);
    int keepAliveTimeInPool = getConfigIntValue(BINARY_AGENT_THROTTLE_CONF_INSTANCE_ID,
        TM_AGENT_THREAD_POOL_KEEP_ALIVE_TIME, DEFAULT_TM_AGENT_THREAD_POOL_KEEP_ALIVE_TIME);
    int reconnectionInterval = getConfigIntValue(BINARY_AGENT_THROTTLE_CONF_INSTANCE_ID,
        TM_AGENT_RECONNECTION_INTERVAL, DEFAULT_TM_AGENT_RECONNECTION_INTERVAL);
    int maxTransportPoolSize = getConfigIntValue(BINARY_AGENT_THROTTLE_CONF_INSTANCE_ID,
        TM_AGENT_MAX_TRANSPORT_POOL_SIZE, DEFAULT_TM_AGENT_MAX_TRANSPORT_POOL_SIZE);
    int maxIdleConnections = getConfigIntValue(BINARY_AGENT_THROTTLE_CONF_INSTANCE_ID,
        TM_AGENT_EVICTION_TIME_PERIOD, DEFAULT_TM_AGENT_MAX_IDLE_CONNECTIONS);
    int evictionTimePeriod = getConfigIntValue(BINARY_AGENT_THROTTLE_CONF_INSTANCE_ID,
        TM_AGENT_EVICTION_TIME_PERIOD, DEFAULT_TM_AGENT_EVICTION_TIME_PERIOD);
    int minIdleTimeInPool = getConfigIntValue(BINARY_AGENT_THROTTLE_CONF_INSTANCE_ID,
        TM_AGENT_MIN_IDLE_TIME_IN_POOL, DEFAULT_TM_AGENT_MIN_IDLE_TIME_IN_POOL);
    int secureMaxTransportPoolSize = getConfigIntValue(BINARY_AGENT_THROTTLE_CONF_INSTANCE_ID,
        TM_AGENT_SECURE_MAX_TRANSPORT_POOL_SIZE, DEFAULT_TM_AGENT_SECURE_MAX_TRANSPORT_POOL_SIZE);
    int secureMaxIdleConnections = getConfigIntValue(BINARY_AGENT_THROTTLE_CONF_INSTANCE_ID,
        TM_AGENT_SECURE_MAX_IDLE_CONNECTIONS, DEFAULT_TM_AGENT_SECURE_MAX_IDLE_CONNECTIONS);
    int secureEvictionTimePeriod = getConfigIntValue(BINARY_AGENT_THROTTLE_CONF_INSTANCE_ID,
        TM_AGENT_SECURE_EVICTION_TIME_PERIOD, DEFAULT_TM_AGENT_SECURE_EVICTION_TIME_PERIOD);
    int secureMinIdleTimeInPool = getConfigIntValue(BINARY_AGENT_THROTTLE_CONF_INSTANCE_ID,
        TM_AGENT_SECURE_MIN_IDLE_TIME_IN_POOL, DEFAULT_TM_AGENT_SECURE_MIN_IDLE_TIME_IN_POOL);

    //todo: check if additional config is required
    //the placeholder replacement is handled via the java implementation
    string trustStorePath = getConfigValue(LISTENER_CONF_INSTANCE_ID, TRUST_STORE_PATH, DEFAULT_TRUST_STORE_PATH);
    string trustStorePassword = getConfigValue(LISTENER_CONF_INSTANCE_ID, TRUST_STORE_PASSWORD,
        DEFAULT_TRUST_STORE_PASSWORD);
    string sslEnabledProtoccols = getConfigValue(MTSL_CONF_INSTANCE_ID, MTSL_CONF_PROTOCOL_VERSIONS,
        DEFAULT_PROTOCOL_VERSIONS);
    string ciphers = getConfigValue(MTSL_CONF_INSTANCE_ID, MTSL_CONF_CIPHERS, DEFAULT_CIPHERS);

    jSetTMBinaryAgentConfiguration(java:fromString(trustStorePath), java:fromString(trustStorePassword), queueSize,
        batchSize, corePoolSize, socketTimeoutMS, maxPoolSize, keepAliveTimeInPool, reconnectionInterval,
        maxTransportPoolSize, maxIdleConnections, evictionTimePeriod, minIdleTimeInPool, secureMaxTransportPoolSize,
        secureMaxIdleConnections, secureEvictionTimePeriod, secureMinIdleTimeInPool,
        java:fromString(sslEnabledProtoccols), java:fromString(ciphers));
}

# The receiverURLGroup and the authURLGroup is preprocessed such that to make them compatible with the binary agent.
# + return - [receiverURLGroup , authURLGroup]
function processTMPublisherURLGroup () returns [string, string] {
    string restructuredReceiverURL = "";
    string restructuredAuthURL = "";
    map<anydata>[] | error urlGroups = map<anydata>[].constructFrom(config:getAsArray(TM_BINARY_URL_GROUP));

    if (urlGroups is map<anydata>[] && urlGroups.length() > 0) {
        //todo: remove this as it has no effect
        if (urlGroups.length() == 1) {
            map<anydata> urlGroup = urlGroups[0];
            if ((urlGroup[TM_BINARY_RECEIVER_URL] is string) && (urlGroup[TM_BINARY_AUTH_URL] is string)) {
                return [<string>urlGroup[TM_BINARY_RECEIVER_URL], <string>urlGroup[TM_BINARY_AUTH_URL]];
            } else {
                printDebug(KEY_GLOBAL_THROTTLE_EVENT_PUBLISHER, "Both/One of " + TM_BINARY_RECEIVER_URL +
                    "and/or " + TM_BINARY_AUTH_URL + " properties are not provided under " + TM_BINARY_URL_GROUP);
            }
        } else {
            foreach map<anydata> urlGroup in urlGroups {
                string receiverUrl = "";
                string authUrl = "";

                if (urlGroup[TM_BINARY_RECEIVER_URL] is string) {
                    receiverUrl = <string>urlGroup[TM_BINARY_RECEIVER_URL];
                } else {
                    printError(KEY_GLOBAL_THROTTLE_EVENT_PUBLISHER, TM_BINARY_URL_GROUP + " element is " +
                        "skipped as " + TM_BINARY_RECEIVER_URL + " property is not provided under "
                        + TM_BINARY_URL_GROUP);
                }

                if (urlGroup[TM_BINARY_AUTH_URL] is string) {
                    authUrl = <string>urlGroup[TM_BINARY_AUTH_URL];
                } else {
                    printError(KEY_GLOBAL_THROTTLE_EVENT_PUBLISHER, TM_BINARY_URL_GROUP + " element is " +
                        "skipped as " + TM_BINARY_AUTH_URL + " property is not provided under "
                        + TM_BINARY_URL_GROUP);
                }

                //the urlGroup is added only if both URLs are provided.
                if (receiverUrl != "" && authUrl != "") {
                    restructuredReceiverURL += "{ " + receiverUrl + " },";
                    restructuredAuthURL += "{ " + authUrl + " },";
                }
            }
        }
        //to remove the final ',' in the URLs
        if(restructuredReceiverURL != "" && restructuredAuthURL != "") {
            restructuredReceiverURL = restructuredReceiverURL.substring(0, restructuredReceiverURL.length() - 1);
            restructuredAuthURL = restructuredAuthURL.substring(0, restructuredAuthURL.length() - 1);
            return [restructuredReceiverURL, restructuredAuthURL];
        }
    } else {
        printDebug(KEY_GLOBAL_THROTTLE_EVENT_PUBLISHER, TM_BINARY_AUTH_URL + " property is not identified " +
            "in the configuration file");
    }
    //If receiverURLGroup and AuthURLGroup is not set, task will proceed with the default configurations.
    printDebug(KEY_GLOBAL_THROTTLE_EVENT_PUBLISHER, "Proceeding with the default parameters for " +
        TM_BINARY_URL_GROUP);
    return [DEFAULT_TM_RECEIVER_URL_GROUP,DEFAULT_TM_AUTH_URL_GROUP];
}

function jinitBinaryThrottleDataPublisher() = @java:Method {
    name: "startThrottlePublisherPool",
    class: "org.wso2.micro.gateway.core.globalThrottle.ThrottleAgent"
} external;

function jPublishGlobalThrottleEvent(RequestStreamDTO throttleEvent) = @java:Method {
        name: "publishNonThrottledEvent",
        class: "org.wso2.micro.gateway.core.globalThrottle.ThrottleAgent"
} external;

function jSetTMBinaryAgentConfiguration(handle trustStorePath, handle trustStorePassword, int queueSize,
    int batchSize, int corePoolSize, int socketTimeoutMS, int maxPoolSize, int keepAliveTimeInPool,
    int reconnectionInterval,int maxTransportPoolSize, int maxIdleConnections, int evictionTimePeriod,
    int minIdleTimeInPool, int secureMaxTransportPoolSize, int secureMaxIdleConnections, int secureEvictionTimePeriod,
    int secureMinIdleTimeInPool, handle sslEnabledProtocols, handle ciphers) = @java:Method {
        name: "setTMBinaryAgentConfiguration",
        class: "org.wso2.micro.gateway.core.globalThrottle.ThrottleAgent"
} external;

function jSetTMBinaryPublisherConfiguration(handle receiverURLGroup, handle authURLGroup,
    handle userName, handle password, int maxIdleDataPublishingAgents, int initIdleObjectDataPublishingAgents,
    int publisherThreadPoolCoreSize, int publisherThreadPoolMaximumSize, int publisherThreadPoolKeepAliveTime)
    = @java:Method {
        name: "setTMBinaryPublisherConfiguration",
        class: "org.wso2.micro.gateway.core.globalThrottle.ThrottleAgent"
} external;
