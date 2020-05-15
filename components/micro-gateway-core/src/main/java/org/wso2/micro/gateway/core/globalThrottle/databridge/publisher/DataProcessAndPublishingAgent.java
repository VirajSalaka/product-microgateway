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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.micro.gateway.core.globalThrottle.databridge.agent.DataPublisher;

/**
 * This class is responsible for executing data publishing logic. This class implements runnable interface and
 * need to execute using thread pool executor. Primary task of this class it is accept message context as parameter
 * and perform time consuming data extraction and publish event to data publisher. Having data extraction and
 * transformation logic in this class will help to reduce overhead added to main message flow.
 */
public class DataProcessAndPublishingAgent implements Runnable {
    private static final Log log = LogFactory.getLog(DataProcessAndPublishingAgent.class);

    private static String streamID = "org.wso2.throttle.request.stream:1.0.0";
    private DataPublisher dataPublisher;

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
    String messageId;

    private long messageSizeInBytes;

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
        this.applicationLevelTier = null;
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
        this.messageSizeInBytes = 0;
    }

    /**
     * This method will use to set message context.
     */
    //TODO: Introduce mapvalue
    //TODO: add properties parameter
    public void setDataReference(String applicationLevelThrottleKey, String applicationLevelTier,
                                 String apiLevelThrottleKey, String apiLevelTier,
                                 String subscriptionLevelThrottleKey, String subscriptionLevelTier,
                                 String resourceLevelThrottleKey, String resourceLevelTier,
                                 String authorizedUser, String apiContext, String apiVersion, String appTenant,
                                 String apiTenant, String appId, String apiName, String messageId) {
        if (!StringUtils.isEmpty(apiLevelTier)) {
            resourceLevelTier = apiLevelTier;
            resourceLevelThrottleKey = apiLevelThrottleKey;
        }
        this.messageId = messageId;
        this.applicationLevelThrottleKey = applicationLevelThrottleKey;
        this.applicationLevelTier = applicationLevelTier;
        this.apiLevelThrottleKey = apiLevelThrottleKey;
        this.subscriptionLevelThrottleKey = subscriptionLevelThrottleKey;
        this.subscriptionLevelTier = subscriptionLevelTier;
        this.resourceLevelThrottleKey = resourceLevelThrottleKey;
        this.resourceLevelTier = resourceLevelTier;
        this.authorizedUser = authorizedUser;
        this.apiContext = apiContext;
        this.apiVersion = apiVersion;
        this.appTenant = appTenant;
        this.apiTenant = apiTenant;
        this.appId = appId;
        this.apiName = apiName;
        this.messageSizeInBytes = 0;
    }

    public void run() {

        Object[] objects = new Object[]{messageId,
                this.applicationLevelThrottleKey, this.applicationLevelTier,
                this.apiLevelThrottleKey, this.apiLevelTier,
                this.subscriptionLevelThrottleKey, this.subscriptionLevelTier,
                this.resourceLevelThrottleKey, this.resourceLevelTier,
                this.authorizedUser, this.apiContext, this.apiVersion,
                //todo: change the hardcoded empty properties value
                this.appTenant, this.apiTenant, this.appId, this.apiName, "{}"};
        org.wso2.carbon.databridge.commons.Event event = new org.wso2.carbon.databridge.commons.Event(streamID,
                System.currentTimeMillis(), null, null, objects);
        dataPublisher.tryPublish(event);
    }

    protected DataPublisher getDataPublisher() {
        return ThrottleDataPublisher.getDataPublisher();
    }
}
