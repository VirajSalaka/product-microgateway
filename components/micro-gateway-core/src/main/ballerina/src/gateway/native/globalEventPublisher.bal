import ballerinax/java;

public function initBinaryThrottleDataPublisher() {
    loadTMBinaryAgentConfiguration();
    loadTMBinaryPublisherConfiguration();
    jinitBinaryThrottleDataPublisher();
}
public function publishBinaryGlobalThrottleEvent(RequestStreamDTO throttleEvent) {
        //            messageID: throttleEvent.messageID,
        //            appKey: throttleEvent.appKey,
        //            appTier: throttleEvent.appTier,
        //            apiKey: throttleEvent.apiKey,
        //            apiTier: throttleEvent.apiTier,
        //            subscriptionKey: throttleEvent.subscriptionKey,
        //            subscriptionTier: throttleEvent.subscriptionTier,
        //            resourceKey: throttleEvent.resourceKey,
        //            resourceTier: throttleEvent.resourceTier,
        //            userId: throttleEvent.userId,
        //            apiContext: throttleEvent.apiContext,
        //            apiVersion: throttleEvent.apiVersion,
        //            appTenant: throttleEvent.appTenant,
        //            apiTenant: throttleEvent.apiTenant,
        //            appId: throttleEvent.appId,
        //            apiName: throttleEvent.apiName,
        //            properties: throttleEvent.properties
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
        string messageId = throttleEvent.messageID;

        publishGlobalThrottleEvent(applicationLevelThrottleKey, applicationLevelTier,
                                                                                    apiLevelThrottleKey, apiLevelTier,
                                                                                    subscriptionLevelThrottleKey, subscriptionLevelTier,
                                                                                    resourceLevelThrottleKey, resourceLevelTier,
                                                                                    authorizedUser, apiContext, apiVersion, appTenant,
                                                                                    apiTenant, appId, apiName, messageId);
}

public function publishGlobalThrottleEvent(string applicationLevelThrottleKey, string applicationLevelTier,
                                                                            string apiLevelThrottleKey, string apiLevelTier,
                                                                            string subscriptionLevelThrottleKey, string subscriptionLevelTier,
                                                                            string resourceLevelThrottleKey, string resourceLevelTier,
                                                                            string authorizedUser, string apiContext, string apiVersion, string appTenant,
                                                                            string apiTenant, string appId, string apiName, string messageId) {
    jPublishGlobalThrottleEvent(java:fromString(applicationLevelThrottleKey), java:fromString(applicationLevelTier),
                                                                 java:fromString(apiLevelThrottleKey), java:fromString(apiLevelTier),
                                                                 java:fromString(subscriptionLevelThrottleKey), java:fromString(subscriptionLevelTier),
                                                                 java:fromString(resourceLevelThrottleKey), java:fromString(resourceLevelTier),
                                                                 java:fromString(authorizedUser), java:fromString(apiContext), java:fromString(apiVersion), java:fromString(appTenant),
                                                                 java:fromString(apiTenant), java:fromString(appId), java:fromString(apiName), java:fromString(messageId));
}

function loadTMBinaryPublisherConfiguration() {
    //todo: input validation from ballerina layer
    string receiverURLGroup = getConfigValue(BINARY_PUBLISHER_THROTTLE_CONF_INSTANCE_ID, TM_RECEIVER_URL_GROUP, DEFAULT_TM_RECEIVER_URL_GROUP);
    string authURLGroup = getConfigValue(BINARY_PUBLISHER_THROTTLE_CONF_INSTANCE_ID, TM_AUTH_URL_GROUP, DEFAULT_TM_AUTH_URL_GROUP);
    string username = getConfigValue(BINARY_PUBLISHER_THROTTLE_CONF_INSTANCE_ID, TM_USERNAME, DEFAULT_TM_USERNAME);
    string password = getConfigValue(BINARY_PUBLISHER_THROTTLE_CONF_INSTANCE_ID, TM_PASSWORD, DEFAULT_TM_PASSWORD);
    int publisherPoolMaxIdle = getConfigIntValue(BINARY_PUBLISHER_POOL_THROTTLE_CONF_INSTANCE_ID, TM_PUBLISHER_POOL_MAX_IDLE, DEFAULT_TM_PUBLISHER_POOL_MAX_IDLE);
    int publisherPoolInitIdleCapacity = getConfigIntValue(BINARY_PUBLISHER_POOL_THROTTLE_CONF_INSTANCE_ID, TM_PUBLISHER_POOL_INIT_IDLE_CAPACITY, DEFAULT_TM_PUBLISHER_POOL_INIT_IDLE_CAPACITY);
    int publisherThreadPoolCoreSize = getConfigIntValue(BINARY_PUBLISHER_THREAD_POOL_THROTTLE_CONF_INSTANCE_ID, TM_PUBLISHER_THREAD_POOL_CORE_SIZE, DEFAULT_TM_PUBLISHER_THREAD_POOL_CORE_SIZE);
    int publisherThreadPoolMaximumSize = getConfigIntValue(BINARY_PUBLISHER_THREAD_POOL_THROTTLE_CONF_INSTANCE_ID, TM_PUBLISHER_THREAD_POOL_MAXIMUM_SIZE, DEFAULT_TM_PUBLISHER_THREAD_POOL_MAXIMUM_SIZE);
    int publisherThreadPoolKeepAliveTime = getConfigIntValue(BINARY_PUBLISHER_THREAD_POOL_THROTTLE_CONF_INSTANCE_ID, TM_PUBLISHER_THREAD_POOL_KEEP_ALIVE_TIME, DEFAULT_TM_PUBLISHER_THREAD_POOL_KEEP_ALIVE_TIME);

    jSetTMBinaryPublisherConfiguration(java:fromString(receiverURLGroup), java:fromString(authURLGroup),
        java:fromString(username), java:fromString(password), publisherPoolMaxIdle, publisherPoolInitIdleCapacity,
        publisherThreadPoolCoreSize, publisherThreadPoolMaximumSize, publisherThreadPoolKeepAliveTime);
}

function loadTMBinaryAgentConfiguration() {
    //todo: validate if the value is a power of 2
    int queueSize = getConfigIntValue(BINARY_AGENT_THROTTLE_CONF_INSTANCE_ID, TM_AGENT_QUEUE_SIZE, DEFAULT_TM_AGENT_QUEUE_SIZE);
    int batchSize = getConfigIntValue(BINARY_AGENT_THROTTLE_CONF_INSTANCE_ID, TM_AGENT_BATCH_SIZE, DEFAULT_TM_AGENT_BATCH_SIZE);
    int corePoolSize = getConfigIntValue(BINARY_AGENT_THROTTLE_CONF_INSTANCE_ID, TM_AGENT_THREAD_POOL_CORE_SIZE, DEFAULT_TM_AGENT_THREAD_POOL_CORE_SIZE);
    int socketTimeoutMS = getConfigIntValue(BINARY_AGENT_THROTTLE_CONF_INSTANCE_ID,  TM_AGENT_SOCKET_TIMEOUT_MS, DEFAULT_TM_AGENT_SOCKET_TIMEOUT_MS);
    int maxPoolSize = getConfigIntValue(BINARY_AGENT_THROTTLE_CONF_INSTANCE_ID, TM_AGENT_THREAD_POOL_MAXIMUM_SIZE, DEFAULT_TM_AGENT_THREAD_POOL_MAXIMUM_SIZE);
    int keepAliveTimeInPool = getConfigIntValue(BINARY_AGENT_THROTTLE_CONF_INSTANCE_ID, TM_AGENT_THREAD_POOL_KEEP_ALIVE_TIME, DEFAULT_TM_AGENT_THREAD_POOL_KEEP_ALIVE_TIME);
    int reconnectionInterval = getConfigIntValue(BINARY_AGENT_THROTTLE_CONF_INSTANCE_ID, TM_AGENT_RECONNECTION_INTERVAL, DEFAULT_TM_AGENT_RECONNECTION_INTERVAL);
    int maxTransportPoolSize = getConfigIntValue(BINARY_AGENT_THROTTLE_CONF_INSTANCE_ID, TM_AGENT_MAX_TRANSPORT_POOL_SIZE, DEFAULT_TM_AGENT_MAX_TRANSPORT_POOL_SIZE);
    int maxIdleConnections = getConfigIntValue(BINARY_AGENT_THROTTLE_CONF_INSTANCE_ID, TM_AGENT_EVICTION_TIME_PERIOD, DEFAULT_TM_AGENT_MAX_IDLE_CONNECTIONS);
    int evictionTimePeriod = getConfigIntValue(BINARY_AGENT_THROTTLE_CONF_INSTANCE_ID, TM_AGENT_EVICTION_TIME_PERIOD, DEFAULT_TM_AGENT_EVICTION_TIME_PERIOD);
    int minIdleTimeInPool = getConfigIntValue(BINARY_AGENT_THROTTLE_CONF_INSTANCE_ID, TM_AGENT_MIN_IDLE_TIME_IN_POOL, DEFAULT_TM_AGENT_MIN_IDLE_TIME_IN_POOL);
    int secureMaxTransportPoolSize = getConfigIntValue(BINARY_AGENT_THROTTLE_CONF_INSTANCE_ID, TM_AGENT_SECURE_MAX_TRANSPORT_POOL_SIZE, DEFAULT_TM_AGENT_SECURE_MAX_TRANSPORT_POOL_SIZE);
    int secureMaxIdleConnections = getConfigIntValue(BINARY_AGENT_THROTTLE_CONF_INSTANCE_ID, TM_AGENT_SECURE_MAX_IDLE_CONNECTIONS, DEFAULT_TM_AGENT_SECURE_MAX_IDLE_CONNECTIONS);
    int secureEvictionTimePeriod = getConfigIntValue(BINARY_AGENT_THROTTLE_CONF_INSTANCE_ID, TM_AGENT_SECURE_EVICTION_TIME_PERIOD, DEFAULT_TM_AGENT_SECURE_EVICTION_TIME_PERIOD);
    int secureMinIdleTimeInPool = getConfigIntValue(BINARY_AGENT_THROTTLE_CONF_INSTANCE_ID, TM_AGENT_SECURE_MIN_IDLE_TIME_IN_POOL, DEFAULT_TM_AGENT_SECURE_MIN_IDLE_TIME_IN_POOL);

    //todo: check if additional config is required
    //todo: check if we need to replace the place holder
    string trustStorePath = getConfigValue(LISTENER_CONF_INSTANCE_ID, TRUST_STORE_PATH, DEFAULT_TRUST_STORE_PATH);
    string trustStorePassword = getConfigValue(LISTENER_CONF_INSTANCE_ID, TRUST_STORE_PASSWORD, DEFAULT_TRUST_STORE_PASSWORD);
    string sslEnabledProtoccols = getConfigValue(MTSL_CONF_INSTANCE_ID, MTSL_CONF_PROTOCOL_VERSIONS, DEFAULT_PROTOCOL_VERSIONS);
    string ciphers = getConfigValue(MTSL_CONF_INSTANCE_ID, MTSL_CONF_CIPHERS, DEFAULT_CIPHERS);

    jSetTMBinaryAgentConfiguration(java:fromString(trustStorePath), java:fromString(trustStorePassword), queueSize,
        batchSize, corePoolSize, socketTimeoutMS, maxPoolSize, keepAliveTimeInPool, reconnectionInterval,
        maxTransportPoolSize, maxIdleConnections, evictionTimePeriod, minIdleTimeInPool, secureMaxTransportPoolSize,
        secureMaxIdleConnections, secureEvictionTimePeriod, secureMinIdleTimeInPool,
        java:fromString(sslEnabledProtoccols), java:fromString(ciphers));
}

function jinitBinaryThrottleDataPublisher() = @java:Method {
    name: "startThrottlePublisherPool",
    class: "org.wso2.micro.gateway.core.globalThrottle.ThrottleAgent"
} external;

function jPublishGlobalThrottleEvent(handle applicationLevelThrottleKey, handle applicationLevelTier,
        handle apiLevelThrottleKey, handle apiLevelTier, handle subscriptionLevelThrottleKey,
        handle subscriptionLevelTier,handle resourceLevelThrottleKey, handle resourceLevelTier,
        handle authorizedUser, handle apiContext, handle apiVersion, handle appTenant,handle apiTenant, handle appId,
        handle apiName, handle messageId) = @java:Method {
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
