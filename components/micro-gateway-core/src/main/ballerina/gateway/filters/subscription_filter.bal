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
import ballerina/internal;
import ballerina/log;
import ballerina/io;

// Subscription filter to validate the subscriptions which is available in the  jwt token
// This filter should only be engaged when jwt token is is used for authentication. For oauth2
// OAuthnFilter will handle the subscription validation as well.
public type SubscriptionFilter object {

    public function filterRequest(http:Caller caller, http:Request request, http:FilterContext filterContext)
                        returns boolean {
        int startingTime = getCurrentTime();
        checkOrSetMessageID(filterContext);
        boolean result = doFilterRequest(caller, request, filterContext);
        setLatency(startingTime, filterContext, SECURITY_LATENCY_SUBS);
        return result;
    }

    public function doFilterRequest(http:Caller caller, http:Request request, http:FilterContext filterContext)
                        returns boolean {
        string authScheme = runtime:getInvocationContext().authContext.scheme;
        printDebug(KEY_SUBSCRIPTION_FILTER, "Auth scheme: " + authScheme);
        if (authScheme == AUTH_SCHEME_JWT){
            string jwtToken = runtime:getInvocationContext().authContext.authToken;
            string currentAPIContext = getContext(filterContext);
            AuthenticationContext authenticationContext = new ;
            var jwtPayload = getEncodedJWTPayload(jwtToken);
            if(jwtPayload is string) {
                printTrace(KEY_SUBSCRIPTION_FILTER, "Encoded JWT payload: " + jwtPayload);
                var decodedPayload = getDecodedJWTPayload(jwtPayload);
                if(decodedPayload is json) {
                    printTrace(KEY_SUBSCRIPTION_FILTER, "Decoded JWT payload: " + decodedPayload.toString());
                    json subscribedAPIList = decodedPayload.subscribedAPIs;
                    APIConfiguration apiConfig = getAPIDetailsFromServiceAnnotation(reflect:
                        getServiceAnnotations(filterContext.serviceType));
                    if (subscribedAPIList != null){
                        int l = subscribedAPIList.length();
                        if (l == 0){
                            authenticationContext.authenticated = true;
                            authenticationContext.tier = "Unauthenticated";
                            authenticationContext.apiKey = jwtToken;
                            authenticationContext.username = decodedPayload.sub.toString();
                            if (decodedPayload.application.id != null) {
                                authenticationContext.applicationId = decodedPayload.application.id.toString();
                            } else {
                                authenticationContext.applicationId = "__unknown__";
                            }
                            if (decodedPayload.application.name != null) {
                                authenticationContext.applicationName = decodedPayload.application.name.toString
                                ();
                            } else {
                                authenticationContext.applicationName = "__unknown__";
                            }
                            if (decodedPayload.application.tier != null) {
                                authenticationContext.applicationTier = decodedPayload.application.tier.toString
                                ();
                            } else {
                                authenticationContext.applicationTier = "Unlimited";
                            }
                            authenticationContext.subscriber = decodedPayload.application.owner.toString();
                            authenticationContext.consumerKey = decodedPayload.consumerKey.toString();
                            authenticationContext.apiTier = "Unlimited";
                            authenticationContext.apiPublisher = "__unknown__";
                            authenticationContext.subscriberTenantDomain = "__unknown__";
                            authenticationContext.keyType = decodedPayload.keytype.toString();
                            runtime:getInvocationContext().attributes[KEY_TYPE_ATTR] = authenticationContext.
                            keyType;
                            filterContext.attributes[AUTHENTICATION_CONTEXT] = authenticationContext;
                            return true;
                        }
                        foreach var subscription in subscribedAPIList {
                            if (subscription.name.toString() == apiConfig.name &&
                                subscription["version"].toString() == apiConfig.apiVersion) {
                                printDebug(KEY_SUBSCRIPTION_FILTER, "Found a matching subscription with name:" +
                                        subscription.name.toString() + " version:" + subscription["version"].
                                        toString());
                                authenticationContext.authenticated = true;
                                authenticationContext.tier = subscription.subscriptionTier.toString();
                                authenticationContext.apiKey = jwtToken;
                                authenticationContext.username = decodedPayload.sub.toString();
                                authenticationContext.callerToken = jwtToken;
                                authenticationContext.applicationId = decodedPayload.application.id.toString();
                                authenticationContext.applicationName = decodedPayload.application.name.toString
                                ();
                                authenticationContext.applicationTier = decodedPayload.application.tier.toString
                                ();
                                authenticationContext.subscriber = decodedPayload.application.owner.toString();
                                authenticationContext.consumerKey = decodedPayload.consumerKey.toString();
                                authenticationContext.apiTier = subscription.subscriptionTier.toString();
                                authenticationContext.apiPublisher = subscription.publisher.toString();
                                authenticationContext.subscriberTenantDomain = subscription
                                .subscriberTenantDomain.toString();
                                authenticationContext.keyType = decodedPayload.keytype.toString();
                                // setting keytype to invocationContext
                                printDebug(KEY_SUBSCRIPTION_FILTER, "Setting key type as " +
                                        authenticationContext.keyType);
                                runtime:getInvocationContext().attributes[KEY_TYPE_ATTR] = authenticationContext
                                .keyType;
                                filterContext.attributes[AUTHENTICATION_CONTEXT] = authenticationContext;
                                printDebug(KEY_SUBSCRIPTION_FILTER, "Subscription validation success.");
                                return true;
                            }
                        }
                    }
                    else
                    {
                        authenticationContext.authenticated = true;
                        authenticationContext.tier = "Unauthenticated";
                        authenticationContext.apiKey = jwtToken;
                        authenticationContext.username = decodedPayload.sub.toString();
                        authenticationContext.applicationId = "__unknown__";
                        authenticationContext.applicationName = "__unknown__";
                        authenticationContext.applicationTier = "Unlimited";
                        authenticationContext.subscriber = "__unknown__";
                        authenticationContext.consumerKey = "__unknown__";
                        authenticationContext.apiTier = "Unlimited";
                        authenticationContext.apiPublisher = "__unknown__";
                        authenticationContext.subscriberTenantDomain = "__unknown__";
                        authenticationContext.keyType = "__unknown__";
                        runtime:getInvocationContext().attributes[KEY_TYPE_ATTR] = authenticationContext.keyType
                        ;
                        filterContext.attributes[AUTHENTICATION_CONTEXT] = authenticationContext;
                        return true;
                    }
                    setErrorMessageToFilterContext(filterContext, API_AUTH_FORBIDDEN);
                    sendErrorResponse(caller, request, filterContext);
                    return false;
                    } else {
                        log:printError("Error occurred while decoding the JWT token with the payload : " +
                                jwtPayload, err = decodedPayload);
                        setErrorMessageToFilterContext(filterContext, API_AUTH_GENERAL_ERROR);
                        sendErrorResponse(caller, request, filterContext);
                        return false;
                    }

            } else {
                log:printError(jwtPayload.message, err = jwtPayload);
                setErrorMessageToFilterContext(filterContext, API_AUTH_GENERAL_ERROR);
                sendErrorResponse(caller, request, filterContext);
                return false;
            }
        } else {
            printDebug(KEY_SUBSCRIPTION_FILTER, "Skipping since auth scheme != jwt.");
        }
        return true;
    }


    public function filterResponse(http:Response response, http:FilterContext context) returns boolean {
        return true;
    }

};
