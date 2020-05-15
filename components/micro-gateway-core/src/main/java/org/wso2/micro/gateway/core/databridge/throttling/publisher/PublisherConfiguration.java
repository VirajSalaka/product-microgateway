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

package org.wso2.micro.gateway.core.databridge.throttling.publisher;

public class PublisherConfiguration {
    private int maxIdleDataPublishingAgents = 250;
    private int initIdleObjectDataPublishingAgents = 250;

    private int publisherThreadPoolCoreSize = 200;
    private int publisherThreadPoolMaximumSize = 1000;
    private int publisherThreadPoolKeepAliveTime = 20;

    private String receiverUrlGroup = "tcp://localhost:9611";
    private String authUrlGroup = "ssl://localhost:9711";
    private String userName = "admin";
    private String password = "admin";

    private PublisherConfiguration() {
    }

    public int getMaxIdleDataPublishingAgents() {
        return maxIdleDataPublishingAgents;
    }

    public int getInitIdleObjectDataPublishingAgents() {
        return initIdleObjectDataPublishingAgents;
    }

    public int getPublisherThreadPoolCoreSize() {
        return publisherThreadPoolCoreSize;
    }

    public int getPublisherThreadPoolMaximumSize() {
        return publisherThreadPoolMaximumSize;
    }

    public int getPublisherThreadPoolKeepAliveTime() {
        return publisherThreadPoolKeepAliveTime;
    }

    public String getReceiverUrlGroup() {
        return receiverUrlGroup;
    }

    public String getAuthUrlGroup() {
        return authUrlGroup;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    private static class InnerPublisherConfiguration {
        private static final PublisherConfiguration instance = new PublisherConfiguration();
    }

    public static PublisherConfiguration getInstance() {
        return InnerPublisherConfiguration.instance;
    }

    public void setConfiguration(String receiverUrlGroup, String authUrlGroup, String userName, String password,
                                 int maxIdleDataPublishingAgents, int initIdleObjectDataPublishingAgents,
                                 int publisherThreadPoolCoreSize, int publisherThreadPoolMaximumSize,
                                 int publisherThreadPoolKeepAliveTime) {
        this.maxIdleDataPublishingAgents = maxIdleDataPublishingAgents;
        this.initIdleObjectDataPublishingAgents = initIdleObjectDataPublishingAgents;
        this.publisherThreadPoolCoreSize = publisherThreadPoolCoreSize;
        this.publisherThreadPoolMaximumSize = publisherThreadPoolMaximumSize;
        this.publisherThreadPoolKeepAliveTime = publisherThreadPoolKeepAliveTime;
        this.receiverUrlGroup = receiverUrlGroup;
        this.authUrlGroup = authUrlGroup;
        this.userName = userName;
        this.password = password;
    }
}
