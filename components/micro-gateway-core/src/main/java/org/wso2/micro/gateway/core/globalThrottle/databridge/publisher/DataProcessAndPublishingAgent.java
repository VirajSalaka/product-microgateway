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

package org.wso2.micro.gateway.core.globalThrottle.databridge.publisher;

import org.apache.log4j.Logger;
import org.ballerinalang.jvm.values.api.BMap;
import org.wso2.micro.gateway.core.globalThrottle.databridge.agent.DataPublisher;
import org.wso2.micro.gateway.core.globalThrottle.databridge.agent.util.ThrottleEventConstants;

/**
 * This class is responsible for executing data publishing logic. This class implements runnable interface and
 * need to execute using thread pool executor. Primary task of this class it is accept message context as parameter
 * and perform time consuming data extraction and publish event to data publisher. Having data extraction and
 * transformation logic in this class will help to reduce overhead added to main message flow.
 */
public class DataProcessAndPublishingAgent implements Runnable {
    private static final Logger log = Logger.getLogger(DataProcessAndPublishingAgent.class);

    private static String streamID = "org.wso2.throttle.request.stream:1.0.0";
    private DataPublisher dataPublisher;

    String messageId;
    String applicationLevelThrottleKey;
    String applicationLevelTier;
    String apiLevelThrottleKey;
    String apiLevelTier;
    String subscriptionLevelThrottleKey;
    String subscriptionLevelTier;
    String resourceLevelThrottleKey;
    String authorizedUser;
    String resourceLevelTier;
    String apiContext;
    String apiVersion;
    String appTenant;
    String apiTenant;
    String apiName;
    String appId;
    String properties;

    public DataProcessAndPublishingAgent() {
        dataPublisher = getDataPublisher();
    }

    /**
     * This method will clean data references. This method should call whenever we return data process and publish
     * agent back to pool. Every time when we add new property we need to implement cleaning logic as well.
     */
    public void clearDataReference() {
        this.messageId = null;
        this.applicationLevelThrottleKey = null;
        this.applicationLevelTier = null;
        this.apiLevelThrottleKey = null;
        this.apiLevelTier = null;
        this.subscriptionLevelThrottleKey = null;
        this.subscriptionLevelTier = null;
        this.resourceLevelThrottleKey = null;
        this.resourceLevelTier = null;
        this.authorizedUser = null;
        this.apiContext = null;
        this.apiVersion = null;
        this.appTenant = null;
        this.apiTenant = null;
        this.appId = null;
        this.apiName = null;
    }

    /**
     * This method will use to set message context.
     */
    public void setDataReference(BMap<String, String> throttleEvent) {
        this.messageId = throttleEvent.get(ThrottleEventConstants.messageID);
        this.applicationLevelThrottleKey = throttleEvent.get(ThrottleEventConstants.appKey);
        this.applicationLevelTier = throttleEvent.get(ThrottleEventConstants.appTier);
        this.apiLevelThrottleKey = throttleEvent.get(ThrottleEventConstants.apiKey);
        this.apiLevelTier = throttleEvent.get(ThrottleEventConstants.apiTier);
        this.subscriptionLevelThrottleKey = throttleEvent.get(ThrottleEventConstants.subscriptionKey);
        this.subscriptionLevelTier = throttleEvent.get(ThrottleEventConstants.subscriptionTier);
        this.resourceLevelThrottleKey = throttleEvent.get(ThrottleEventConstants.resourceKey);
        this.resourceLevelTier = throttleEvent.get(ThrottleEventConstants.resourceTier);
        this.authorizedUser = throttleEvent.get(ThrottleEventConstants.userId);
        this.apiContext = throttleEvent.get(ThrottleEventConstants.apiContext);
        this.apiVersion = throttleEvent.get(ThrottleEventConstants.apiVersion);
        this.appTenant = throttleEvent.get(ThrottleEventConstants.appTenant);
        this.apiTenant = throttleEvent.get(ThrottleEventConstants.apiTenant);
        this.appId = throttleEvent.get(ThrottleEventConstants.appId);
        this.apiName = throttleEvent.get(ThrottleEventConstants.apiName);
        this.properties = throttleEvent.get(ThrottleEventConstants.properties);
    }

    public void run() {

        Object[] objects = new Object[]{messageId,
                this.applicationLevelThrottleKey, this.applicationLevelTier,
                this.apiLevelThrottleKey, this.apiLevelTier,
                this.subscriptionLevelThrottleKey, this.subscriptionLevelTier,
                this.resourceLevelThrottleKey, this.resourceLevelTier,
                this.authorizedUser, this.apiContext, this.apiVersion,
                this.appTenant, this.apiTenant, this.appId, this.apiName, properties};
        org.wso2.carbon.databridge.commons.Event event = new org.wso2.carbon.databridge.commons.Event(streamID,
                System.currentTimeMillis(), null, null, objects);
        dataPublisher.tryPublish(event);
    }

    protected DataPublisher getDataPublisher() {
        return ThrottleDataPublisher.getDataPublisher();
    }
}
