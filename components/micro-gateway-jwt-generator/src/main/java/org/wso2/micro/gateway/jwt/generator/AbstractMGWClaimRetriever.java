/*
 *  Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wso2.micro.gateway.jwt.generator;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public abstract class AbstractMGWClaimRetriever {
    private Map<String, String> configurationMap;

    public AbstractMGWClaimRetriever(Map<String, String> configurationMap) {
        this.configurationMap = configurationMap;
    }

    public abstract List<ClaimDTO> retrieveClaims(Map<String, Object> authContext) throws IOException;
//    public abstract Map<String, Object> retrieveClaims(Map<String, Object> authContext, Map<String, Object> jwtPayload) throws IOException;

    public Map<String, String> getConfigurationMap() {
        return configurationMap;
    }
}
