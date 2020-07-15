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

import ballerina/jwt;
import ballerina/runtime;

boolean claimRetrieveEnabled = getConfigBooleanValue(KM_CONF_CLAIM_RETRIEVAL_INSTANCE_ID, KM_CONF_CLAIM_RETRIEVAL_ENABLED,
    DEFAULT_KM_CONF_CLAIM_RETRIEVAL_ENABLED);

# To retrieve claims via the user specific claim retrieve implementation.
# 
# + authContext - Authentication Context
# + payload - jwt payload if it is handled via jwt auth provider
# + return - ClaimListDTO if there are any claims added from the user specific implementation
function retrieveClaims (AuthenticationContext authContext, jwt:JwtPayload? payload = ()) returns @tainted RetrievedUserClaimsListDTO ? {
    //if claim retrieve variable is disabled, there is no need to run through the method.
    if (!claimRetrieveEnabled) {
        return;
    }
    UserAuthContextDTO userInfo = {};
    if(payload is jwt:JwtPayload) {
        userInfo = generateAuthContextInfoFromJWT(authContext, payload);
    } else {
        runtime:InvocationContext invocationContext = runtime:getInvocationContext();
        runtime:Principal? principal = invocationContext?.principal;
        if (principal is runtime:Principal) {
            userInfo = generateAuthContextInfoFromPrincipal(authContext, principal);
        } else {
            printDebug(CLAIM_RETRIEVER, "Claim retrieval implementation is not executed due to the unavailability " +
                "of the principal component");
            return;
        }
    }
    printDebug (CLAIM_RETRIEVER, "User Auth Context information provided to the claim retrieval implementation : " +
            userInfo.toString());
    RetrievedUserClaimsListDTO? | error claimListDTO = trap retrieveClaimsFromImpl(userInfo);
    if (claimListDTO is RetrievedUserClaimsListDTO ) {
        printDebug (CLAIM_RETRIEVER, "Claims List received from the claim retrieval implementation : " +
                    claimListDTO.toString());
        return claimListDTO;
    } else if (claimListDTO is ()) {
        printDebug(CLAIM_RETRIEVER , "No user claims are received from the claim retrieval implementation");
    } else {
        printError(CLAIM_RETRIEVER , "Error while retrieving user claims from the claim retrieval implementation",
            claimListDTO);
    }
}

# To do the class loading operation for the user specific claim retriever implementation.
public function loadClaimRetrieverImpl() {
    if (!claimRetrieveEnabled) {
        printDebug(CLAIM_RETRIEVER, "Claim Retrieval is disabled from configuration.");
        return;
    }
    printDebug(CLAIM_RETRIEVER, "Claim Retrieval is Enabled from configuration.");
    string claimRetrieverImplClassName = getConfigValue(KM_CONF_CLAIM_RETRIEVAL_INSTANCE_ID,
                                                        KM_CONF_CLAIM_RETRIEVAL_IMPLEMENTATION,
                                                        DEFAULT_KM_CONF_CLAIM_RETRIEVAL_IMPLEMENTATION);
    map<any> claimRetrieverConfig = {};
    //todo: config available not works for instance_id, hence change the logic
    boolean configurationProvided = isConfigAvailable(KM_CONF_CLAIM_RETRIEVAL_CONFIGURATION);
    if (configurationProvided) {
        claimRetrieverConfig = getConfigMapValue(KM_CONF_CLAIM_RETRIEVAL_CONFIGURATION);
    } else {
        string username = getConfigValue(APIM_CREDENTIALS_INSTANCE_ID, APIM_CREDENTIALS_USERNAME,
                            DEFAULT_APIM_CREDENTIALS_USERNAME);
        string password = getConfigValue(APIM_CREDENTIALS_INSTANCE_ID, APIM_CREDENTIALS_PASSWORD,
                            DEFAULT_APIM_CREDENTIALS_PASSWORD);
        string keyManagerURL = getConfigValue(KM_CONF_INSTANCE_ID, KM_SERVER_URL, DEFAULT_KM_SERVER_URL);
        //todo: populate the properties properly
        claimRetrieverConfig["Username"] = username;
        claimRetrieverConfig["password"] = password;
        claimRetrieverConfig["KeyManagerUrl"] = keyManagerURL;
    }

    boolean claimRetrieveClassLoaded =
                        loadClaimRetrieverClass(claimRetrieverImplClassName, claimRetrieverConfig);
    if (claimRetrieveClassLoaded) {
        printDebug(CLAIM_RETRIEVER, "JWT Claim Retriever Classloading is successful.");
    } else {
        printError(CLAIM_RETRIEVER, "Claim Retriever classloading is failed. Hence claim retrieval process is disabled");
        //If the classloading is failed, the configuration is set to disabled.
        claimRetrieveEnabled = false;
    }
}

# Populate the DTO required for the claim retrieval implementation from authContext and principal component.
# 
# + authContext - Authentication Context
# + principal - Principal component
# + return - populated UserAuthContextDTO
function generateAuthContextInfoFromPrincipal(AuthenticationContext authContext, runtime:Principal principal)
        returns UserAuthContextDTO {
    UserAuthContextDTO userAuthContextDTO = {};
    userAuthContextDTO.username = principal?.username ?: UNKNOWN_VALUE;
    userAuthContextDTO.token_type = "oauth2";
    userAuthContextDTO.issuer = "https://localhost:9443/oauth2/token";
    userAuthContextDTO.token =  authContext.apiKey;
    userAuthContextDTO.client_id = authContext.consumerKey;
    map<any>? claims = principal?.claims;
    if (claims is map<any> ) {
        userAuthContextDTO.customClaims = claims;
    }
    return userAuthContextDTO;
}

# Populate the DTO required for the claim retrieval implementation from authContext and principal component.
# 
# + authContext - Authentication Context
# + payload - JWT payload
# + return - populated UserAuthContextDTO
function generateAuthContextInfoFromJWT(AuthenticationContext authContext, jwt:JwtPayload payload)
        returns UserAuthContextDTO {
    UserAuthContextDTO userAuthContextDTO = {};
    userAuthContextDTO.username = authContext.username;
    userAuthContextDTO.token_type = "jwt";
    userAuthContextDTO.issuer = payload?.iss ?: UNKNOWN_VALUE;
    userAuthContextDTO.client_id = authContext.consumerKey;
    map<any>? claims = payload?.customClaims;
    userAuthContextDTO.token =  authContext.apiKey;
    if (claims is map<any> ) {
        userAuthContextDTO.customClaims = claims;
    }
    return userAuthContextDTO;
}


function convertAnyToString(any variable) returns string{
    if (variable is string) {
        return variable;
    } 
    return UNKNOWN_VALUE;
}
