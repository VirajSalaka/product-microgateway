// Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import ballerina/config;

string dialectURI = getConfigValue(JWT_GENERATOR_ID, JWT_GENERATOR_DIALECT, DEFAULT_JWT_GENERATOR_DIALECT);
string jwtGeneratorUsername = getConfigValue(JWT_GENERATOR_ID, JWT_GENERATOR_USERNAME, DEFAULT_JWT_GENERATOR_USERNAME);
string jwtGeneratorPassword = getConfigValue(JWT_GENERATOR_ID, JWT_GENERATOR_USERNAME, DEFAULT_JWT_GENERATOR_PASSWORD);

//todo: introduce a cache to avoid calling retrieve Claims if needed
function retrieveClaims (AuthenticationContext authContext) returns @tainted ClaimsListDTO ? {

    if (!config:contains(JWT_GENERATOR_ID + "." + JWT_GENERATOR_USER_INFO_ENDPOINT)) {
        printDebug(CLAIM_RETRIEVER, "Claims are not retrieved from the API Manager as the " + JWT_GENERATOR_ID + "." +
            JWT_GENERATOR_USER_INFO_ENDPOINT + " configuration is not provided.");
        return;
    }
    UserInfoDTO userInfoDTO = {};
    string? usernameWithTenant = authContext.username;
    if (usernameWithTenant is string) {
        string username = split(usernameWithTenant, "@")[0];
        userInfoDTO.username = username;
    } else {
        userInfoDTO.username = USER_NAME_UNKNOWN;
    }
    //todo: decide the required behavior
    //the user claims will be received only if dialect is matched.
    if (dialectURI.trim() != "") {
        userInfoDTO["dialect"] = dialectURI;
    }
    userInfoDTO.accessToken = authContext.apiKey;
    json | error userInfoDTOJson = json.constructFrom(userInfoDTO);

    http:Request claimRetrieveRequest = new;
    claimRetrieveRequest.setHeader(AUTHORIZATION_HEADER, getBasicAuthHeaderValue(jwtGeneratorUsername,
                                                                                jwtGeneratorPassword));
    if (userInfoDTOJson is json) {
        claimRetrieveRequest.setJsonPayload(userInfoDTOJson);
    }
    http:Response | error userInfoClaimsResponse = userInfoClaimsEndpoint->post("/",
        claimRetrieveRequest);

    if (userInfoClaimsResponse is http:Response) {
        if (userInfoClaimsResponse.statusCode == http:STATUS_OK) {
            json | error jsonPayload = userInfoClaimsResponse.getJsonPayload();
            if (jsonPayload is json) {
                ClaimsListDTO | error claimsListDTO = ClaimsListDTO.constructFrom(jsonPayload);
                if (claimsListDTO is ClaimsListDTO) {
                    printDebug(CLAIM_RETRIEVER, "user claims received for the user " + userInfoDTO.username + " : " +
                        ClaimsListDTO.toString());
                    return claimsListDTO;
                }  else {
                    printError(CLAIM_RETRIEVER, "Failed to map the json response to a valid ClaimList Object: " +
                        jsonPayload.toString(), claimsListDTO);
                }
            } else {
                printError(CLAIM_RETRIEVER, "Response does not contain JSON payload", jsonPayload);
            }
        } else {
            printError(CLAIM_RETRIEVER, "Response code from claim retrieve endpoint is " +
                userInfoClaimsResponse.statusCode.toString());
        }
    } else {
        printError(CLAIM_RETRIEVER, "Error Response received while receiving user claims", userInfoClaimsResponse);
    }
}
