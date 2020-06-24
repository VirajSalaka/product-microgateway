// Copyright (c)  WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
import ballerina/http;

# to retrieve revoked tokens from traffic manager during the startup.
public function retrieveRevokedTokenFromTM() {

    boolean enabledPersistentMessagePilot = getConfigBooleanValue(PERSISTENT_MESSAGE_PILOT_INSTANCE_ID,
            PERSISTENT_MESSAGE_PILOT_ENABLED, DEFAULT_PERSISTENT_MESSAGE_PILOT_ENABLED);
    if (!enabledPersistentMessagePilot) {
        return;
    }
    printDebug(REVOKED_TOKEN_RETRIEVE_UTIL, "Revoked Token retrieval from the Pilot node is enabled.");

    string username = getConfigValue(PERSISTENT_MESSAGE_PILOT_INSTANCE_ID, PERSISTENT_MESSAGE_PILOT_USERNAME,
        DEFAULT_PERSISTENT_MESSAGE_PILOT_USERNAME);
    string password = getConfigValue(PERSISTENT_MESSAGE_PILOT_INSTANCE_ID, PERSISTENT_MESSAGE_PILOT_PASSWORD,
        DEFAULT_PERSISTENT_MESSAGE_PILOT_PASSWORD);
    string credentials = username + ":" + password;
    string encodedCredentials = credentials.toBytes().toBase64();

    http:Request req = new;
    req.addHeader(DEFAULT_AUTH_HEADER_NAME, BASIC_PREFIX_WITH_SPACE + encodedCredentials);

    var response = revokeTokenRetrieveEndpoint->get("/", req);
    if (response is http:Response) {
        //todo: update the revoketokenmap, once the confusion over using signature or jti
        printDebug(REVOKED_TOKEN_RETRIEVE_UTIL,response.getTextPayload().toString());
    } else {
        printError(REVOKED_TOKEN_RETRIEVE_UTIL, "Error response is received from the revoked token retrieval endpoint."
            , response);
    }
}
