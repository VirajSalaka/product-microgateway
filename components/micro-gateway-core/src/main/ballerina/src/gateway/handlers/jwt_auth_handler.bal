// Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import ballerina/jwt;
import ballerina/runtime;

# Representation of the jwt self validating handler
#
# + jwtAuthProvider - The reference to the jwt auth provider instance
public type JWTAuthHandler object {

    *http:InboundAuthHandler;

    public JwtAuthProvider jwtAuthProvider;

    private boolean enabledJWTGenerator = false;
    private boolean classLoaded = false;
    private string generatorClass = "";
    private string dialectURI = "";
    private string signatureAlgorithm = "";
    private string certificateAlias = "";
    private string privateKeyAlias = "";
    private int tokenExpiry = 0;
    private any[] restrictedClaims = [];
    private string keyStoreLocationUnresolved = "";
    private string keyStorePassword = "";
    private string tokenIssuer = "";
    private any[] tokenAudience = [];
    private int skewTime = 0;
    private boolean enabledCaching = false;
    private int cacheExpiry = 0;

    public function __init(JwtAuthProvider jwtAuthProvider) {
        self.jwtAuthProvider = jwtAuthProvider;
        // initiating generator class if enabled
        self.enabledJWTGenerator = getConfigBooleanValue(JWT_GENERATOR_ID,
                                                            JWT_GENERATOR_ENABLED,
                                                            DEFAULT_JWT_GENERATOR_ENABLED);
        if (self.enabledJWTGenerator) {
            self.generatorClass = getConfigValue(JWT_GENERATOR_ID,
                                                    JWT_GENERATOR_IMPLEMENTATION,
                                                    DEFAULT_JWT_GENERATOR_IMPLEMENTATION);
            self.dialectURI = getConfigValue(JWT_GENERATOR_ID,
                                                JWT_GENERATOR_DIALECT,
                                                DEFAULT_JWT_GENERATOR_DIALECT);
            self.signatureAlgorithm = getConfigValue(JWT_GENERATOR_ID,
                                                        JWT_GENERATOR_SIGN_ALGO,
                                                        DEFAULT_JWT_GENERATOR_SIGN_ALGO);
            self.certificateAlias = getConfigValue(JWT_GENERATOR_ID,
                                                        JWT_GENERATOR_CERTIFICATE_ALIAS,
                                                        DEFAULT_JWT_GENERATOR_CERTIFICATE_ALIAS);
            self.privateKeyAlias = getConfigValue(JWT_GENERATOR_ID,
                                                    JWT_GENERATOR_PRIVATE_KEY_ALIAS,
                                                    DEFAULT_JWT_GENERATOR_PRIVATE_KEY_ALIAS);
            self.tokenExpiry = getConfigIntValue(JWT_GENERATOR_ID,
                                                    JWT_GENERATOR_TOKEN_EXPIRY,
                                                    DEFAULT_JWT_GENERATOR_TOKEN_EXPIRY);
            self.restrictedClaims = getConfigArrayValue(JWT_GENERATOR_ID,
                                                        JWT_GENERATOR_RESTRICTED_CLAIMS);
            self.keyStoreLocationUnresolved = getConfigValue(LISTENER_CONF_INSTANCE_ID,
                                                                KEY_STORE_PATH,
                                                                DEFAULT_KEY_STORE_PATH);
            self.keyStorePassword = getConfigValue(LISTENER_CONF_INSTANCE_ID,
                                                                    KEY_STORE_PASSWORD,
                                                                    DEFAULT_KEY_STORE_PASSWORD);
            self.tokenIssuer = getConfigValue(JWT_GENERATOR_ID,
                                                JWT_GENERATOR_TOKEN_ISSUER,
                                                DEFAULT_JWT_GENERATOR_TOKEN_ISSUER);
            self.tokenAudience = getConfigArrayValue(JWT_GENERATOR_ID,
                                                        JWT_GENERATOR_TOKEN_AUDIENCE);
            // provide backward compatibility for skew time
            self.skewTime = getConfigIntValue(SERVER_CONF_ID, 
                                                SERVER_TIMESTAMP_SKEW, 
                                                DEFAULT_SERVER_TIMESTAMP_SKEW);
            if (self.skewTime == DEFAULT_SERVER_TIMESTAMP_SKEW) {
                self.skewTime = getConfigIntValue(KM_CONF_INSTANCE_ID, 
                                                    TIMESTAMP_SKEW, 
                                                    DEFAULT_TIMESTAMP_SKEW);
            }
            self.enabledCaching = getConfigBooleanValue(JWT_GENERATOR_CACHING_ID,
                                                            JWT_GENERATOR_TOKEN_CACHE_ENABLED,
                                                            DEFAULT_JWT_GENERATOR_TOKEN_CACHE_ENABLED);
            self.cacheExpiry = getConfigIntValue(JWT_GENERATOR_CACHING_ID,
                                                    JWT_GENERATOR_TOKEN_CACHE_EXPIRY,
                                                    DEFAULT_TOKEN_CACHE_EXPIRY);

            self.classLoaded = loadJWTGeneratorClass(self.generatorClass,
                                                        self.dialectURI,
                                                        self.signatureAlgorithm,
                                                        self.keyStoreLocationUnresolved,
                                                        self.keyStorePassword,
                                                        self.certificateAlias,
                                                        self.privateKeyAlias,
                                                        self.tokenExpiry,
                                                        self.restrictedClaims,
                                                        self.enabledCaching,
                                                        self.cacheExpiry,
                                                        self.tokenIssuer,
                                                        self.tokenAudience);
        }
    }

    # Checks if the request can be authenticated with the Bearer Auth header.
    #
    # + req - The `Request` instance.
    # + return - Returns `true` if can be authenticated. Else, returns `false`.
    public function canProcess(http:Request req) returns @tainted boolean {
        string authHeader = runtime:getInvocationContext().attributes[AUTH_HEADER].toString();
        if (req.hasHeader(authHeader)) {
            string headerValue = req.getHeader(authHeader).toLowerAscii();
            if (headerValue.startsWith(AUTH_SCHEME_BEARER_LOWERCASE)) {
                string credential = headerValue.substring(6, headerValue.length()).trim();
                string[] splitContent = split(credential, "\\.");
                if (splitContent.length() == 3) {
                    printDebug(KEY_AUTHN_FILTER, "Request will authenticated via jwt handler");
                    return true;
                }
            }
        }
        return false;
    }

    # Authenticates the incoming request with the use of credentials passed as the Bearer Auth header.
    #
    # + req - The `Request` instance.
    # + return - Returns `true` if authenticated successfully. Else, returns `false`
    # or the `AuthenticationError` in case of an error.
    public function process(http:Request req) returns @tainted boolean | http:AuthenticationError {
        string authHeader = runtime:getInvocationContext().attributes[AUTH_HEADER].toString();
        string headerValue = req.getHeader(authHeader);
        string credential = headerValue.substring(6, headerValue.length()).trim();
        var authenticationResult = self.jwtAuthProvider.authenticate(credential);
        if (authenticationResult is boolean) {
            string? iss = self.jwtAuthProvider.jwtValidatorConfig?.issuer;
            boolean remoteUserClaimRetrievalEnabled = self.jwtAuthProvider.remoteUserClaimRetrievalEnabled;
            boolean backendJWTfromClaim = setBackendJwtHeader(credential, req, iss);
            if (!backendJWTfromClaim) {
                boolean generationStatus = generateAndSetBackendJwtHeader(credential,
                                                                            req,
                                                                            self.enabledJWTGenerator,
                                                                            self.classLoaded,
                                                                            self.skewTime,
                                                                            self.enabledCaching,
                                                                            iss,
                                                                            remoteUserClaimRetrievalEnabled);
                if (!generationStatus) {
                    printError(KEY_JWT_AUTH_PROVIDER, "JWT Generation failed");
                }
                return authenticationResult;
            } else {
                printDebug(KEY_JWT_AUTH_PROVIDER, "JWT is set from the payload claim");
                return true;
            }
        } else {
            return prepareAuthenticationError("Failed to authenticate with jwt bearer auth handler.", authenticationResult);
        }
    }

};

# Check whether backendJwt claim is in the payload and set the header if avaialable.
#
# + credential - Credential
# + req - The `Request` instance.
# + issuer - The jwt issuer who issued the token and comes in the iss claim.
# + return - Returns boolean based on backend jwt setting.
public function setBackendJwtHeader(string credential, http:Request req, string? issuer) returns @tainted boolean {
    (jwt:JwtPayload | error) payload = getDecodedJWTPayload(credential, issuer);
    if (payload is jwt:JwtPayload) {
        map<json>? customClaims = payload?.customClaims;
        // validate backend jwt claim and set it to jwt header
        if (customClaims is map<json> && customClaims.hasKey(BACKEND_JWT)) {
            printDebug(KEY_JWT_AUTH_PROVIDER, "Set backend jwt header from payload claim.");
            req.setHeader(jwtheaderName, customClaims.get(BACKEND_JWT).toString());
            return true;
        }
    }
    return false;
}

// TODO: Try to merge with the subscription validation method
# Identify the api details from the subscribed apis in the authentication token.
#
# + payload - The payload of the authentication token
# + apiName - name of the current API
# + apiVersion - version of the current API
# + return - Returns map<string> with the extracted details.
public function getAPIDetails(jwt:JwtPayload payload, string apiName, string apiVersion) returns map<string> {
    if (!isSelfContainedToken(payload)) {
        return createAPIDetailsMap(runtime:getInvocationContext());
    }
    map<string> apiDetails = {
        apiName: "",
        apiContext: "",
        apiVersion: "",
        apiTier: "",
        apiPublisher: "",
        subscriberTenantDomain: ""
    };
    json subscribedAPIList = [];
    map<json>? customClaims = payload?.customClaims;
    //get allowed apis
    if (customClaims is map<json> && customClaims.hasKey(SUBSCRIBED_APIS)) {
        printDebug(KEY_JWT_AUTH_PROVIDER, "subscribedAPIs claim found in the jwt.");
        subscribedAPIList = customClaims.get(SUBSCRIBED_APIS);
    }
    if (subscribedAPIList is json[]) {
        if (!(apiName == "" && apiVersion == "")) {
            printDebug(KEY_JWT_AUTH_PROVIDER, "Current API name: " + apiName + ", current version: " + apiVersion);
            int l = subscribedAPIList.length();
            int index = 0;
            while (index < l) {
                var subscription = subscribedAPIList[index];
                if (subscription.name.toString() == apiName && subscription.'version.toString() == apiVersion) {
                    // API is found in the subscribed APIs
                    if (isDebugEnabled) {
                        printDebug(KEY_JWT_AUTH_PROVIDER, "Found the API in subscribed APIs:" + subscription.name.toString()
                            + " version:" + subscription.'version.toString());
                    }
                    if (subscription.name is json) {
                        apiDetails["apiName"] = subscription.name.toString();
                    }
                    if (subscription.'version is json) {
                        apiDetails["apiVersion"] = subscription.'version.toString();
                    }
                    if (subscription.context is json) {
                        apiDetails["apiContext"] = subscription.context.toString();
                    }
                    if (subscription.subscriptionTier is json) {
                        apiDetails["apiTier"] = subscription.subscriptionTier.toString();
                    }
                    if (subscription.publisher is json) {
                        apiDetails["apiPublisher"] = subscription.publisher.toString();
                    }
                    if (subscription.subscriberTenantDomain is json) {
                        apiDetails["subscriberTenantDomain"] = subscription.subscriberTenantDomain.toString();
                    }
                }
                index += 1;
            }
        }
    }
    return apiDetails;
}

# Generate the backend JWT token and set to the header of the outgoing request.
#
# + credential - Credential
# + req - The `Request` instance.
# + enabledJWTGenerator - state of jwt generator
# + classLoaded - whether the class is loaded successfully
# + enabledCaching - jwt generator caching enabled
# + skewTime - skew time to backend
# + issuer - The jwt issuer who issued the token and comes in the iss claim.
# + return - Returns `true` if the token generation and setting the header completed successfully
# or the `AuthenticationError` in case of an error.
public function generateAndSetBackendJwtHeader(string credential,
                                                http:Request req,
                                                boolean enabledJWTGenerator,
                                                boolean classLoaded,
                                                int skewTime,
                                                boolean enabledCaching,
                                                string? issuer,
                                                boolean remoteUserClaimRetrievalEnabled) returns @tainted boolean {
    if (enabledJWTGenerator) {
        if (classLoaded) {
            boolean status = false;
            string apiName = "";
            string apiVersion = "";
            APIConfiguration? apiConfig = apiConfigAnnotationMap[runtime:getInvocationContext().attributes[http:SERVICE_NAME].toString()];
            if (apiConfig is APIConfiguration) {
                apiName = apiConfig.name;
                apiVersion = apiConfig.apiVersion;
            }
            string cacheKey = credential + apiName + apiVersion;
            (jwt:JwtPayload | error) payload = getDecodedJWTPayload(credential, issuer);
            if (payload is jwt:JwtPayload) {
                printDebug(KEY_JWT_AUTH_PROVIDER, "decoded token credential");
                // get the subscribedAPI details
                map<string> apiDetails = getAPIDetails(payload, apiName, apiVersion);
                // checking if cache is enabled
                if (enabledCaching) {
                    var cachedToken = jwtGeneratorCache.get(cacheKey);
                    printDebug(KEY_JWT_AUTH_PROVIDER, "Key: " + cacheKey);
                    if (cachedToken is string) {
                        printDebug(KEY_JWT_AUTH_PROVIDER, "Found in jwt generator cache");
                        printDebug(KEY_JWT_AUTH_PROVIDER, "Token: " + cachedToken);
                        (jwt:JwtPayload | error) cachedPayload = getDecodedJWTPayload(cachedToken, issuer);
                        if (cachedPayload is jwt:JwtPayload) {
                            int currentTime = getCurrentTime();
                            int? cachedTokenExpiry = cachedPayload?.exp;
                            if (cachedTokenExpiry is int) {
                                cachedTokenExpiry = cachedTokenExpiry * 1000;
                                int difference = (cachedTokenExpiry - currentTime);
                                if (difference < skewTime) {
                                    printDebug(KEY_JWT_AUTH_PROVIDER, "JWT regenerated because of the skew time");
                                    status = setJWTHeader(<@untainted>payload, req, cacheKey, enabledCaching, apiDetails, 
                                                            remoteUserClaimRetrievalEnabled);
                                } else {
                                    req.setHeader(jwtheaderName, cachedToken);
                                    status = true;
                                }
                            } else {
                                printDebug(KEY_JWT_AUTH_PROVIDER, "Failed to read exp from cached token");
                                return false;
                            }
                        }
                    } else {
                        printDebug(KEY_JWT_AUTH_PROVIDER, "Could not find in the jwt generator cache");
                        status = setJWTHeader(<@untainted>payload, req, cacheKey, enabledCaching, apiDetails, 
                                            remoteUserClaimRetrievalEnabled);
                    }
                } else {
                    printDebug(KEY_JWT_AUTH_PROVIDER, "JWT generator caching is disabled");
                    status = setJWTHeader(<@untainted>payload, req, cacheKey, enabledCaching, apiDetails, 
                                            remoteUserClaimRetrievalEnabled);
                }
            } else {
                printDebug(KEY_JWT_AUTH_PROVIDER, "Failed to read JWT token");
                return false;
            }
            return status;
        } else {
            printDebug(KEY_JWT_AUTH_PROVIDER, "Class loading failed");
            return false;
        }
    } else {
        printDebug(KEY_JWT_AUTH_PROVIDER, "JWT Generator is disabled");
        return true;
    }
}
