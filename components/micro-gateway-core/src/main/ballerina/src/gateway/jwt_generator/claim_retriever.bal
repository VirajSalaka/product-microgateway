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

# To retrieve claims via the user specific claim retrieve implementation.
# + authContext - Authentication Context
# + return - ClaimListDTO if there are any claims added from the user specific implementation
function retrieveClaims (AuthenticationContext authContext) returns @tainted ClaimsListDTO ? {
    //if claim retrieve variable is disabled, there is no need to run through the method.
    if (!claimRetrieveEnabled) {
        return;
    }
    runtime:InvocationContext invocationContext = runtime:getInvocationContext();
    runtime:Principal? principal = invocationContext?.principal;

    if (principal is runtime:Principal) {
        OpaqueTokenInfoDTO userInfo = generateOpaqueTokenInfo(authContext, principal);
        printDebug (CLAIM_RETRIEVER, "Opaque token information passed down to the claim retrieval implementation : " +
            userInfo.toString());
        ClaimsListDTO? | error claimListDTO = trap retrieveClaimsFromImpl(userInfo);
        if (claimListDTO is ClaimsListDTO ) {
            printDebug (CLAIM_RETRIEVER, "Claims List received from the claim retrieval implementation : " +
                        claimListDTO.toString());
            return claimListDTO;
        } else if (claimListDTO is ()) {
            printDebug(CLAIM_RETRIEVER , "No user claims are received from the claim retrieval implementation");
        } else {
            printError(CLAIM_RETRIEVER , "Error while retrieving user claims from the claim retrieval implementation",
                claimListDTO);
        }
    } else {
        printDebug(CLAIM_RETRIEVER, "Claim retrieval implementation is not executed due to the unavailability " +
            "of the principal component");
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
# + authContext - Authentication Context
# + principal - Principal component
# + return - populated OpaqueTokenInfoDTO
function generateOpaqueTokenInfo (AuthenticationContext authContext, runtime:Principal principal)
        returns OpaqueTokenInfoDTO {
    OpaqueTokenInfoDTO tokenInfoDTO = {};
    tokenInfoDTO.username = principal?.username ?: UNKNOWN_VALUE;
    tokenInfoDTO.scope = principal?.scopes.toString();
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
