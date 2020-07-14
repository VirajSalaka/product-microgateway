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

import ballerina/runtime;

boolean claimRetrieveEnabled = getConfigBooleanValue(KM_CONF_CLAIM_RETRIEVAL_INSTANCE_ID, KM_CONF_CLAIM_RETRIEVAL_ENABLED,
    DEFAULT_KM_CONF_CLAIM_RETRIEVAL_ENABLED);

function retrieveClaims (AuthenticationContext authContext) returns @tainted ClaimsListDTO ? {

    if (!claimRetrieveEnabled) {
        return;
    }
    printInfo("CLAIM RETRIEVER XX", "sfsdfsadfasdfasdf");

    runtime:InvocationContext invocationContext = runtime:getInvocationContext();
    runtime:Principal? principal = invocationContext?.principal;

    if (principal is runtime:Principal) {
        OpaqueTokenInfoDTO userInfo = generateOpaqueTokenInfo(authContext, principal);
        ClaimsListDTO? | error claimListDTO = trap retrieveClaimsFromImpl(userInfo);
        if (claimListDTO is ClaimsListDTO ){
            return claimListDTO;
        } else {
            printError("CLAIM RETRIEVER XX" , "Null returned");
        }
    } else {
        printDebug(CLAIM_RETRIEVER, "Claim retrieval is not executed due to the unavailability " +
            "of the principal component");
    }
    

    //UserInfoDTO userInfoDTO = {};
    //string? usernameWithTenant = authContext.username;
    //if (usernameWithTenant is string) {
    //    string username = split(usernameWithTenant, "@")[0];
    //    userInfoDTO.username = username;
    //} else {
    //    userInfoDTO.username = USER_NAME_UNKNOWN;
    //}
    ////todo: decide the required behavior
    ////the user claims will be received only if dialect is matched.
    //if (dialectURI.trim() != "") {
    //    userInfoDTO["dialect"] = dialectURI;
    //}
    //userInfoDTO.accessToken = authContext.apiKey;
    //json | error userInfoDTOJson = json.constructFrom(userInfoDTO);
    //
    //http:Request claimRetrieveRequest = new;
    //claimRetrieveRequest.setHeader(AUTHORIZATION_HEADER, getBasicAuthHeaderValue(jwtGeneratorUsername,
    //                                                                            jwtGeneratorPassword));
    //if (userInfoDTOJson is json) {
    //    claimRetrieveRequest.setJsonPayload(userInfoDTOJson);
    //}
    //http:Response | error userInfoClaimsResponse = userInfoClaimsEndpoint->post("/",
    //    claimRetrieveRequest);
    //
    //if (userInfoClaimsResponse is http:Response) {
    //    if (userInfoClaimsResponse.statusCode == http:STATUS_OK) {
    //        json | error jsonPayload = userInfoClaimsResponse.getJsonPayload();
    //        if (jsonPayload is json) {
    //            ClaimsListDTO | error claimsListDTO = ClaimsListDTO.constructFrom(jsonPayload);
    //            if (claimsListDTO is ClaimsListDTO) {
    //                printDebug(CLAIM_RETRIEVER, "user claims received for the user " + userInfoDTO.username + " : " +
    //                    ClaimsListDTO.toString());
    //                return claimsListDTO;
    //            }  else {
    //                printError(CLAIM_RETRIEVER, "Failed to map the json response to a valid ClaimList Object: " +
    //                    jsonPayload.toString(), claimsListDTO);
    //            }
    //        } else {
    //            printError(CLAIM_RETRIEVER, "Response does not contain JSON payload", jsonPayload);
    //        }
    //    } else {
    //        printError(CLAIM_RETRIEVER, "Response code from claim retrieve endpoint is " +
    //            userInfoClaimsResponse.statusCode.toString());
    //    }
    //} else {
    //    printError(CLAIM_RETRIEVER, "Error Response received while receiving user claims", userInfoClaimsResponse);
    //}
}

public function loadClaimRetrieverImpl() {
    if (!claimRetrieveEnabled) {
        printDebug(CLAIM_RETRIEVER, "Claim Retrieval is disabled.");
        return;
    }
    printDebug(CLAIM_RETRIEVER, "Claim Retrieval is Enabled.");
    string claimRetrieverImplClassName = getConfigValue(KM_CONF_CLAIM_RETRIEVAL_INSTANCE_ID, KM_CONF_CLAIM_RETRIEVAL_IMPLEMENTATION,
        DEFAULT_KM_CONF_CLAIM_RETRIEVAL_IMPLEMENTATION);
    map<any> claimRetrieverConfig = {};
    boolean configurationProvided = isConfigAvailable(KM_CONF_CLAIM_RETRIEVAL_CONFIGURATION);
    if (configurationProvided) {
        claimRetrieverConfig = getConfigMapValue(KM_CONF_CLAIM_RETRIEVAL_CONFIGURATION);
    } else {
        string username = getConfigValue(APIM_CREDENTIALS_INSTANCE_ID, APIM_CREDENTIALS_USERNAME, DEFAULT_APIM_CREDENTIALS_USERNAME);
        string password = getConfigValue(APIM_CREDENTIALS_INSTANCE_ID, APIM_CREDENTIALS_PASSWORD, DEFAULT_APIM_CREDENTIALS_PASSWORD);
        string keyManagerURL = getConfigValue(KM_CONF_INSTANCE_ID, KM_SERVER_URL, DEFAULT_KM_SERVER_URL);
        claimRetrieverConfig["Username"] = username;
        claimRetrieverConfig["password"] = password;
        claimRetrieverConfig["KeyManagerUrl"] = keyManagerURL;
    }

    //todo: read this property from configuration
    //todo: read property map from the configuration
    //todo: if the class is present and configuration is not there, populate it from the newly introduced apim credentials property
    boolean claimRetrieveClassLoaded =
                        loadClaimRetrieverClass(claimRetrieverImplClassName, claimRetrieverConfig);
    if (claimRetrieveClassLoaded) {
        printDebug(CLAIM_RETRIEVER, "JWT Claim Retriever Classloading is successful.");
    } else {
        printError(CLAIM_RETRIEVER, "Claim Retriever classloading is failed.");
        //If the classloading is failed, the configuration is set to disabled.
        claimRetrieveEnabled = false;
    }
}

public function generateOpaqueTokenInfo (AuthenticationContext authContext, runtime:Principal principal) returns OpaqueTokenInfoDTO {
    
    OpaqueTokenInfoDTO tokenInfoDTO = {};
    tokenInfoDTO.username = principal?.username ?: UNKNOWN_VALUE;
    tokenInfoDTO.scopes = principal?.scopes.toString();
    map<any>? claims = principal?.claims;
    tokenInfoDTO.client_id = convertAnyToString(claims[CLIENT_ID]);
    tokenInfoDTO.token =  authContext.apiKey;
    return tokenInfoDTO;
}

function convertAnyToString(any variable) returns string{
    if (variable is string) {
        return variable;
    } 
    return UNKNOWN_VALUE;
}
