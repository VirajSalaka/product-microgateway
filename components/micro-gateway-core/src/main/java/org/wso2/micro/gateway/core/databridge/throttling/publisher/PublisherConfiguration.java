package org.wso2.micro.gateway.core.databridge.throttling.publisher;

public class PublisherConfiguration {
    private int maxIdleDataPublishingAgents = 250;
    private int initIdleObjectDataPublishingAgents = 250;

    private int processThreadPoolCoreSize = 200;
    private int processThreadPoolMaximumSize = 1000;
    private int processThreadPoolKeepAliveTime = 20;
    private int throttleFrequency = 3600;

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

    public int getProcessThreadPoolCoreSize() {
        return processThreadPoolCoreSize;
    }

    public int getProcessThreadPoolMaximumSize() {
        return processThreadPoolMaximumSize;
    }

    public int getProcessThreadPoolKeepAliveTime() {
        return processThreadPoolKeepAliveTime;
    }

    public int getThrottleFrequency() {
        return throttleFrequency;
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

    public void setConfigValues(int maxIdleDataPublishingAgents, int initIdleObjectDataPublishingAgents,
                                int processThreadPoolCoreSize, int processThreadPoolMaximumSize,
                                int processThreadPoolKeepAliveTime, int throttleFrequency, String receiverUrlGroup,
                                String authUrlGroup, String userName, String password) {
        this.maxIdleDataPublishingAgents = maxIdleDataPublishingAgents;
        this.initIdleObjectDataPublishingAgents = initIdleObjectDataPublishingAgents;
        this.processThreadPoolCoreSize = processThreadPoolCoreSize;
        this.processThreadPoolMaximumSize = processThreadPoolMaximumSize;
        this.processThreadPoolKeepAliveTime = processThreadPoolKeepAliveTime;
        this.throttleFrequency = throttleFrequency;
        this.receiverUrlGroup = receiverUrlGroup;
        this.authUrlGroup = authUrlGroup;
        this.userName = userName;
        this.password = password;
    }


}
