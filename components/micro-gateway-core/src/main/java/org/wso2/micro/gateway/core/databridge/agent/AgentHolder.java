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
package org.wso2.micro.gateway.core.databridge.agent;

import org.apache.log4j.Logger;
import org.wso2.micro.gateway.core.databridge.agent.conf.AgentConfiguration;
import org.wso2.micro.gateway.core.databridge.agent.exception.DataEndpointException;

/**
 * The holder for all Agents created and this is singleton class.
 * The Agents will be loaded by reading a configuration file data.agent.config.yaml default.
 */

public class AgentHolder {

    private static final Logger log = Logger.getLogger(AgentHolder.class);
    private static AgentHolder instance;
    //TODO: manupulate this properly
    private DataEndpointAgent agent;

    private AgentHolder() {
        addAgentConfiguration(generateAgentConfiguration());
    }

    public static synchronized AgentHolder getInstance() {
        if (instance == null) {
            instance = new AgentHolder();
        }
        return instance;
    }

    public static synchronized void shutdown() throws DataEndpointException {
        if (instance != null) {
            instance.agent.shutDown();
            instance = null;
        }
    }

    //TODO: Remove default agent concept
    private void addAgentConfiguration(AgentConfiguration agentConfiguration) {
        agent = new DataEndpointAgent(agentConfiguration);
    }

    /**
     * Returns the default agent,and the first element in the data.agent.config.yaml
     * is taken as default data publisher type.
     *
     * @return DataEndpointAgent for the default endpoint name.
     */
    public DataEndpointAgent getDataEndpointAgent() {
        return agent;
    }

    //todo: under default configuration
    //TODO: Remove this
    public AgentConfiguration generateAgentConfiguration() {
        return new AgentConfiguration();
    }
}
