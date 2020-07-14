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
import ballerina/jwt;
import ballerina/http;

# Refactoring method for setting JWT header
#
# + payload - The payload of the authentication token
# + req - The `Request` instance.
# + cacheKey - key for the jwt generator cache
# + enabledCaching - jwt generator caching enabled
# + apiDetails - extracted api details for the current api
# + return - Returns `true` if the token generation and setting the header completed successfully
# or the `AuthenticationError` in case of an error.
public function setJWTHeader(jwt:JwtPayload payload,
                                http:Request req,
                                string cacheKey,
                                boolean enabledCaching,
                                map<string> apiDetails)
                                returns @tainted boolean {
    AuthenticationContext authContext = 
        <AuthenticationContext> runtime:getInvocationContext().attributes[AUTHENTICATION_CONTEXT];
    (handle|error) generatedToken = generateBackendTokenForJWT(authContext, payload, apiDetails);
    return setGeneratedTokenAsHeader(req, cacheKey, enabledCaching, generatedToken);
}

# Setting backend JWT header when there is no JWT Token is present.
#
# + req - The `Request` instance.
# + authContext - Authentication Context
# + cacheKey - key for the jwt generator cache
# + enabledCaching - jwt generator caching enabled
# + apiDetails - extracted api details for the current api
# + return - Returns `true` if the token generation and setting the header completed successfully
# or the `AuthenticationError` in case of an error.
public function setJWTHeaderForOauth2(http:Request req,
                                AuthenticationContext authContext,
                                string cacheKey,
                                boolean enabledCaching,
                                map<string> apiDetails)
                                returns @tainted boolean {

    (handle|error) generatedToken = generateBackendJWTTokenForOauth(authContext, apiDetails);
    if (generatedToken is error) {
        return false;
    } else {
        return setGeneratedTokenAsHeader(req, cacheKey, enabledCaching, generatedToken);
    }
}

# Setting backend JWT header when there is no JWT Token is present.
#
# + authContext - Authentication Context
# + payload - The payload of the authentication token
# + apiDetails - extracted api details for the current api
# + return - JWT Token
# or the `AuthenticationError` in case of an error.
function generateBackendTokenForJWT(AuthenticationContext authContext, jwt:JwtPayload payload, map<string> apiDetails)  
        returns handle | error {
    (handle|error) generatedToken;
    if (isSelfContainedToken(payload)) {
        generatedToken = generateJWTToken(payload, apiDetails);
    } else {
        ClaimsMapDTO claimsMapDTO = createMapFromClaimsListDTO(authContext, payload);
        generatedToken = generateJWTTokenFromUserClaimsMap(claimsMapDTO, apiDetails);
    }
    return generatedToken;
}

# Setting backend JWT header when there is no JWT Token is present.
#
# + apiDetails - extracted api details for the current api
# + return - JWT Token
# or the `AuthenticationError` in case of an error.
function generateBackendJWTTokenForOauth(AuthenticationContext authContext, map<string> apiDetails) returns handle | error {
    (handle|error) generatedToken;
    ClaimsMapDTO claimsMapDTO = createMapFromClaimsListDTO(authContext);
    generatedToken = generateJWTTokenFromUserClaimsMap(claimsMapDTO, apiDetails);
    return generatedToken;
}

# Setting backend JWT header when there is no JWT Token is present.
#
# + req - The `Request` instance.
# + cacheKey - key for the jwt generator cache
# + enabledCaching - jwt generator caching enabled
# + generatedToken - generated Backend JWT
# + return - Returns `true` if the token generation and setting the header completed successfully
function setGeneratedTokenAsHeader(http:Request req,
                                string cacheKey,
                                boolean enabledCaching,
                                handle | error generatedToken)
                                returns @tainted boolean {

    if (generatedToken is error) {
        printError(KEY_JWT_AUTH_PROVIDER, "Token not generated due to error", generatedToken);
        return false;
    }
    printDebug(KEY_JWT_AUTH_PROVIDER, "Generated jwt token");
    printDebug(KEY_JWT_AUTH_PROVIDER, "Token: " + generatedToken.toString());

    //todo: add to cache if cache enabled
    if (enabledCaching) {
        error? err = jwtGeneratorCache.put(<@untainted>cacheKey, <@untainted>generatedToken.toString());
        if (err is error) {
            printError(KEY_JWT_AUTH_PROVIDER, "Error while adding entry to jwt generator cache", err);
        }
        printDebug(KEY_JWT_AUTH_PROVIDER, "Added to jwt generator token cache.");
    }
    req.setHeader(jwtheaderName, generatedToken.toString());
    return true;
}

function isSelfContainedToken(jwt:JwtPayload payload) returns boolean {
    if (payload.hasKey(APPLICATION)) {
        return true;
    }
    return false;
}

function createMapFromClaimsListDTO(AuthenticationContext authContext, jwt:JwtPayload? payload = ()) 
        returns @tainted ClaimsMapDTO {
    ClaimsMapDTO claimsMapDTO = {};
    CustomClaimsMapDTO customClaimsMapDTO = {};
    ClaimsListDTO ? claimsListDTO = retrieveClaims(authContext);
    if (claimsListDTO is ClaimsListDTO) {
       ClaimDTO[] claimList = claimsListDTO.list;
       foreach ClaimDTO claim in claimList {
           customClaimsMapDTO[claim.uri] = claim.value;
       }
    }
    //todo: ideally authentication context should have the scope variable
    //todo: therefore the scopes wont be there in the oauth2 scenario
    if (!(payload is ())) {
        map<json>? customClaims = payload["customClaims"];
        if (customClaims is map<json>) {
            //todo: decide whether we put dialect here
            foreach var [key, value] in customClaims.entries() {
                string | error claimValue = trap <string> value;
                if (claimValue is string) {
                    customClaimsMapDTO[key] = claimValue;
                }
            }
        }
    }
    ApplicationClaimsMapDTO applicationClaimsMapDTO = {};
    applicationClaimsMapDTO.id = emptyStringIfUnknownValue(authContext.applicationId);
    applicationClaimsMapDTO.owner = emptyStringIfUnknownValue(authContext.username);
    applicationClaimsMapDTO.name = emptyStringIfUnknownValue(authContext.applicationName);
    applicationClaimsMapDTO.tier = emptyStringIfUnknownValue(authContext.applicationTier);

    customClaimsMapDTO.application = applicationClaimsMapDTO;
    customClaimsMapDTO.sub = emptyStringIfUnknownValue(authContext.username);
    claimsMapDTO.customClaims = customClaimsMapDTO;
    return claimsMapDTO;
}

//todo: check if there are performance related issues
function createAPIDetailsMap (runtime:InvocationContext invocationContext) returns map<string> {
    map<string> apiDetails = {};
    AuthenticationContext authenticationContext = <AuthenticationContext>invocationContext.attributes[AUTHENTICATION_CONTEXT];
    APIConfiguration? apiConfig = apiConfigAnnotationMap[<string>invocationContext.attributes[http:SERVICE_NAME]];
    if (apiConfig is APIConfiguration) {
        apiDetails["apiName"] = apiConfig.name;
        apiDetails["apiVersion"] = apiConfig.apiVersion;
        apiDetails["apiTier"] = apiConfig.apiTier;
        apiDetails["apiContext"] = <string> invocationContext.attributes[API_CONTEXT];
        apiDetails["apiPublisher"] = apiConfig.publisher;
        apiDetails["subscriberTenantDomain"] = authenticationContext.subscriberTenantDomain;
    }
    return apiDetails;
}

function emptyStringIfUnknownValue (string value) returns string {
    return value != UNKNOWN_VALUE ? value : "";
}
