/*
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.micro.gateway.core.globalThrottle.databridge.agent.util;

/**
 * This class holds the constants required to fetch the properties from the
 * ThrottleEvent {@link org.ballerinalang.jvm.values.api.BMap}.
 */
public class ThrottleEventConstants {

    private ThrottleEventConstants() {
    }

    public static final String messageID = "messageID";
    public static final String appKey = "appKey";
    public static final String appTier = "appTier";
    public static final String apiKey = "apiKey";
    public static final String apiTier = "apiTier";
    public static final String subscriptionKey = "subscriptionKey";
    public static final String subscriptionTier = "subscriptionTier";
    public static final String resourceKey = "resourceKey";
    public static final String resourceTier = "resourceTier";
    public static final String userId = "userId";
    public static final String apiContext = "apiContext";
    public static final String apiVersion = "apiVersion";
    public static final String appTenant = "appTenant";
    public static final String apiTenant = "apiTenant";
    public static final String appId = "appId";
    public static final String apiName = "apiName";
    public static final String properties = "properties";
}
