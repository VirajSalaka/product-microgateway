import ballerinax/java;

public function initGlobalThrottleDataPublisher() {
    jInitGlobalThrottleDataPublisher();
}
public function publishGlobalThrottleEventFromDto(RequestStreamDTO throttleEvent) {
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

public function jInitGlobalThrottleDataPublisher() = @java:Method {
    name: "startThrottlePublisherPool",
    class: "org.wso2.micro.gateway.core.globalThrottle.ThrottleAgent"
} external;

public function jPublishGlobalThrottleEvent(handle applicationLevelThrottleKey, handle applicationLevelTier,
        handle apiLevelThrottleKey, handle apiLevelTier, handle subscriptionLevelThrottleKey,
        handle subscriptionLevelTier,handle resourceLevelThrottleKey, handle resourceLevelTier,
        handle authorizedUser, handle apiContext, handle apiVersion, handle appTenant,handle apiTenant, handle appId,
        handle apiName, handle messageId) = @java:Method {
            name: "publishNonThrottledEvent",
            class: "org.wso2.micro.gateway.core.globalThrottle.ThrottleAgent"
            } external;
