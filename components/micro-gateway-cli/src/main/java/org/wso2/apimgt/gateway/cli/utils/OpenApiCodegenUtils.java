/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.apimgt.gateway.cli.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.apimgt.gateway.cli.constants.GatewayCliConstants;
import org.wso2.apimgt.gateway.cli.exception.CLIInternalException;
import org.wso2.apimgt.gateway.cli.model.rest.ext.ExtendedAPI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Utilities used by ballerina code generator.
 */
public class OpenApiCodegenUtils {

    private static final Logger logger = LoggerFactory.getLogger(OpenApiCodegenUtils.class);

    public static String readApi(String filePath) {
        String responseStr;
        try {
            responseStr = new String(Files.readAllBytes(Paths.get(filePath)), GatewayCliConstants.CHARSET_UTF8);
        } catch (IOException e) {
            logger.error("Error while reading api definition.", e);
            throw new CLIInternalException("Error while reading api definition.");
        }
        return responseStr;
    }

    public static void setAdditionalConfigs(String projectName, ExtendedAPI api) {
        api.setEndpointConfigRepresentation(RouteUtils.getGlobalEpConfig( api.getName(), api.getVersion(),
                GatewayCmdUtils.getProjectRoutesConfFilePath(projectName)));
    }

}
