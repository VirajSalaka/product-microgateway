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
import ballerina/log;

string throttleEndpointUrl = getConfigValue(THROTTLE_CONF_INSTANCE_ID, THROTTLE_ENDPOINT_URL, DEFAULT_THROTTLE_ENDPOINT_URL);
string throttleEndpointbase64Header = getConfigValue(THROTTLE_CONF_INSTANCE_ID, THROTTLE_ENDPOINT_BASE64_HEADER,
DEFAULT_THROTTLE_ENDPOINT_BASE64_HEADER);

http:Client httpThrottleEndpoint = new (throttleEndpointUrl,
{
    cache: {enabled: false},
    secureSocket: {
        trustStore: {
            path: getConfigValue(LISTENER_CONF_INSTANCE_ID, TRUST_STORE_PATH, DEFAULT_TRUST_STORE_PATH),
            password: getConfigValue(LISTENER_CONF_INSTANCE_ID, TRUST_STORE_PASSWORD, DEFAULT_TRUST_STORE_PASSWORD)
        },
        verifyHostname: getConfigBooleanValue(HTTP_CLIENTS_INSTANCE_ID, ENABLE_HOSTNAME_VERIFICATION, true)
    }
});

//todo: avoid initializing local throttling if the global throttling is enabled
public function initGlobalThrottleDataPublisher() {
    if(!isHttpPublisherEnabled()) {
        initBinaryThrottleDataPublisher();
    }
}

public function publishThrottleEventToTrafficManager(RequestStreamDTO throttleEvent) {

    //Event will be published via http, if and only if the throttle_endpoint_url is available and binary_endpoint
    //configurations are not provided.
    if (isHttpPublisherEnabled()) {
        publishHttpGlobalThrottleEvent(throttleEvent);
    } else {
        //todo: improve debug logs for the process inside the java impl
        publishBinaryGlobalThrottleEvent(throttleEvent);
        printDebug(KEY_THROTTLE_UTIL, "ThrottleMessage is added to the event queue");
    }
}

function publishHttpGlobalThrottleEvent(RequestStreamDTO throttleEvent) {
    json sendEvent = {
        event: {
            metaData: {},
            correlationData: {},
            payloadData: {
                messageID: throttleEvent.messageID,
                appKey: throttleEvent.appKey,
                appTier: throttleEvent.appTier,
                apiKey: throttleEvent.apiKey,
                apiTier: throttleEvent.apiTier,
                subscriptionKey: throttleEvent.subscriptionKey,
                subscriptionTier: throttleEvent.subscriptionTier,
                resourceKey: throttleEvent.resourceKey,
                resourceTier: throttleEvent.resourceTier,
                userId: throttleEvent.userId,
                apiContext: throttleEvent.apiContext,
                apiVersion: throttleEvent.apiVersion,
                appTenant: throttleEvent.appTenant,
                apiTenant: throttleEvent.apiTenant,
                appId: throttleEvent.appId,
                apiName: throttleEvent.apiName,
                properties: throttleEvent.properties
            }
        }
    };

    http:Request clientRequest = new;
    string encodedBasicAuthHeader = throttleEndpointbase64Header.toBytes().toBase64();
    clientRequest.setHeader(AUTHORIZATION_HEADER, BASIC_PREFIX_WITH_SPACE + encodedBasicAuthHeader);
    clientRequest.setPayload(sendEvent);

    var response = httpThrottleEndpoint->post("/throttleEventReceiver", clientRequest);
    if (response is http:Response) {
        printDebug(KEY_THROTTLE_UTIL, "\nStatus Code: " + response.statusCode.toString());
    } else {
        log:printError(response.reason(), err = response);
    }
}

function isHttpPublisherEnabled() returns boolean {
    return containsConfigKey(THROTTLE_CONF_INSTANCE_ID, THROTTLE_ENDPOINT_URL) &&
                //todo: fix this properly by providing enabled tag
                !containsConfigKey(BINARY_PUBLISHER_THROTTLE_CONF_INSTANCE_ID, TM_USERNAME);
}
