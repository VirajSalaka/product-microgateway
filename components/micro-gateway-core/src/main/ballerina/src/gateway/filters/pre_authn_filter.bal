// Copyright (c)  WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file   except
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
import ballerina/runtime;

// Pre Authentication filter

public type PreAuthnFilter object {
    map<string> httpGrpcStatusCodeMap = {};
    map<string> httpGrpcErrorMsgMap = {};

    public function _init() returns error? {
        self.httpGrpcStatusCodeMap["401"] = "16";
        self.httpGrpcStatusCodeMap["403"] = "7";
        self.httpGrpcStatusCodeMap["404"] = "12";
        self.httpGrpcStatusCodeMap["429"] = "8";
        //todo: verify https://github.com/grpc/grpc/blob/master/doc/statuscodes.md
        self.httpGrpcStatusCodeMap["500"] = "2";

        self.httpGrpcErrorMsgMap["401"] = "Unauthenticated";
        self.httpGrpcErrorMsgMap["403"] = "Unauthorized";
        self.httpGrpcErrorMsgMap["404"] = "Service not found";
        self.httpGrpcErrorMsgMap["429"] = "Too many function calls";
        self.httpGrpcErrorMsgMap["500"] = "Internal server error";
    }

    public function filterRequest(http:Caller caller, http:Request request, @tainted http:FilterContext context)
                        returns boolean {
        //Setting UUID
        int startingTime = getCurrentTime();
        context.attributes[REQUEST_TIME] = startingTime;
        checkOrSetMessageID(context);
        setHostHeaderToFilterContext(request, context);
        setLatency(startingTime, context, SECURITY_LATENCY_AUTHN);
        addGrpcToFilterContext(context);
        printDebug(KEY_GRPC_FILTER, "Grpc filter is applied for request" + context.attributes[MESSAGE_ID].toString());
        return doAuthnFilterRequest(caller, request, <@untainted>context);
    }

    public function filterResponse(http:Response response, http:FilterContext context) returns boolean {
        printDebug(KEY_GRPC_FILTER, "Grpc filter is applied for response" + context.attributes[MESSAGE_ID].toString());
        if (!filterGrpcResponse(response, context)) {
            if(response.statusCode == 401) {
                sendErrorResponseFromInvocationContext(response);
            }
           return true;
        }
        string statusCode = response.statusCode.toString();
        printDebug(KEY_GRPC_FILTER, "http status code, " + statusCode + " " + context.attributes[MESSAGE_ID].toString());
        if (statusCode == "0") {
           printDebug(KEY_GRPC_FILTER, "Grpc message is status code 0 " + context.attributes[MESSAGE_ID].toString());
           return true;
        }
        string grpcStatus = self.httpGrpcStatusCodeMap[statusCode] ?: "";
        string grpcErrorMessage = self.httpGrpcErrorMsgMap[statusCode] ?: "";
        
        if(statusCode == "") {
           response.setHeader("grpc-status", "2");
           response.setHeader("grpc-message", "Response is not recognized by the gateway.");
           return true;
        }
        response.setHeader("grpc-status", grpcStatus);
        response.setHeader("grpc-message", grpcErrorMessage);
        response.setContentType("application/grpc");

        return true;
    }
};

function doAuthnFilterRequest(http:Caller caller, http:Request request, http:FilterContext context)
             returns boolean {
    boolean isOauth2Enabled = false;
    runtime:InvocationContext invocationContext = runtime:getInvocationContext();
    invocationContext.attributes[MESSAGE_ID] = <string>context.attributes[MESSAGE_ID];
    printDebug(KEY_AUTHN_FILTER, "Processing request via Pre Authentication filter test.");

    context.attributes[REMOTE_ADDRESS] = getClientIp(request, caller);
    context.attributes[FILTER_FAILED] = false;
    string serviceName = context.getServiceName();
    string resourceName = context.getResourceName();
    invocationContext.attributes[SERVICE_TYPE_ATTR] = context.getService();
    invocationContext.attributes[RESOURCE_NAME_ATTR] = resourceName;
    boolean isSecuredResource = isSecured(serviceName, resourceName);
    invocationContext.attributes[IS_SECURED] = isSecuredResource;

    boolean isCookie = false;
    string authHeader = "";
    string? authCookie = "";
    string|error extractedToken = "";
    string authHeaderName = getAuthorizationHeader(invocationContext);
    invocationContext.attributes[AUTH_HEADER] = authHeaderName;
    string[] authProvidersIds = getAuthProviders(context.getServiceName());


    if (request.hasHeader(authHeaderName)) {
        authHeader = request.getHeader(authHeaderName);
    } else if (request.hasHeader(COOKIE_HEADER)) {
        //Authentiction with HTTP cookies
        isCookie = config:contains(COOKIE_HEADER);
        if (isCookie) {
            authCookie = getAuthCookieIfPresent(request);
            if (authCookie is string) {
                authHeader = authCookie;
            }
        }
    }
    string providerId;
    if (!isCookie) {
        providerId = getAuthenticationProviderType(authHeader);
    } else {
        providerId = getAuthenticationProviderTypeWithCookie(authHeader);
    }
    printDebug(KEY_AUTHN_FILTER, "Provider Id for authentication handler : " + providerId);
    boolean canHandleAuthentication = false;
    foreach string provider in authProvidersIds {
        if (provider == providerId) {
            canHandleAuthentication = true;
        }
    }

    if (!canHandleAuthentication) {
        setErrorMessageToInvocationContext(API_AUTH_PROVIDER_INVALID);
        sendErrorResponse(caller, request, context);
        return false;
    }
    // if auth providers are there, use those to authenticate

        //TODO: Move this to post authentication handler
        //checkAndRemoveAuthHeaders(request, authHeaderName);
    return true;
}

function getAuthenticationProviderType(string authHeader) returns (string) {
    if (contains(authHeader, AUTH_SCHEME_BASIC)){
        return AUTHN_SCHEME_BASIC;
    } else if (contains(authHeader,AUTH_SCHEME_BEARER) && contains(authHeader,".")) {
        return AUTH_SCHEME_JWT;
    } else {
        return AUTH_SCHEME_OAUTH2;
    }
}


function getAuthenticationProviderTypeWithCookie(string authHeader) returns (string) {
    if (contains(authHeader,".")) {
        return AUTH_SCHEME_JWT;
    } else {
        return AUTH_SCHEME_OAUTH2;
    }
}

function checkAndRemoveAuthHeaders(http:Request request, string authHeaderName) {
    if (getConfigBooleanValue(AUTH_CONF_INSTANCE_ID, REMOVE_AUTH_HEADER_FROM_OUT_MESSAGE, true)) {
        request.removeHeader(authHeaderName);
        printDebug(KEY_AUTHN_FILTER, "Removed header : " + authHeaderName + " from the request");
    }
    if (request.hasHeader(TEMP_AUTH_HEADER)) {
        request.setHeader(AUTH_HEADER, request.getHeader(TEMP_AUTH_HEADER));
        printDebug(KEY_AUTHN_FILTER, "Setting the backed up auth header value to the header: " + AUTH_HEADER);
        request.removeHeader(TEMP_AUTH_HEADER);
        printDebug(KEY_AUTHN_FILTER, "Removed header : " + TEMP_AUTH_HEADER + " from the request");
    }
}

function getAuthCookieIfPresent(http:Request request) returns string? {
    //get required cookie as config value
    string? authCookie = ();
    if (request.hasHeader(COOKIE_HEADER)) {
        string requiredCookie = config:getAsString(COOKIE_HEADER, "");
        //extract cookies from the incoming request
        string authHead = request.getHeader(COOKIE_HEADER);
        string[] cookies = split(authHead.trim(), ";");
        foreach var cookie in cookies {
            string converted = replaceFirst(cookie, "=", "::");
            string[] splitedStrings = split(converted.trim(), "::");
            string sessionId = splitedStrings[1];
            if (sessionId == requiredCookie) {
                authCookie = sessionId;
            }
        }
    }
    return authCookie;
}

function addGrpcToFilterContext(http:FilterContext context){
    //todo: check if ballerina map support boolean
    context.attributes["isGrpc"] = true;
    printDebug(KEY_GRPC_FILTER, "\"isGrpc\" key is added to the request " + context.attributes[MESSAGE_ID].toString());
}

function filterGrpcResponse(http:Response response, http:FilterContext context) returns boolean {
    //todo: check if needs to check the content type as well.
    if(response.hasHeader("grpc-status")){
        string grpcStatus = response.getHeader("grpc-status").toUpperAscii();
            if(grpcStatus != "UNIMPLEMENTED") {
                return false;
            }
        }
    any isGrpcAttr = context.attributes["isGrpc"];

    if(isGrpcAttr is boolean){
        return true;
    }
    return false;
}
