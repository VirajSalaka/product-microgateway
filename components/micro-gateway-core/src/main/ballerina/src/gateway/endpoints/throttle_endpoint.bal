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

string throttleEndpointUrl = getConfigValue(THROTTLE_CONF_INSTANCE_ID, THROTTLE_ENDPOINT_URL, DEFAULT_THROTTLE_ENDPOINT_URL);
string throttleEndpointbase64Header = getConfigValue(THROTTLE_CONF_INSTANCE_ID, THROTTLE_ENDPOINT_BASE64_HEADER,
DEFAULT_THROTTLE_ENDPOINT_BASE64_HEADER);
string encodedBasicAuthHeader = throttleEndpointbase64Header.toBytes().toBase64();

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

public function initGlobalThrottleDataPublisher() {
    if(enabledGlobalTMEventPublishing && isBinaryPublisherEnabled()) {
        printDebug(KEY_THROTTLE_UTIL, "ThrottleEvents will be published via binary endpoint.");
        initBinaryThrottleDataPublisher();
    } else {
        printDebug(KEY_THROTTLE_UTIL, "ThrottleEvents will be published via HTTPS endpoint.");
    }
}

public function publishThrottleEventToTrafficManager(RequestStreamDTO throttleEvent) {

    //Event will be published via http, if and only if the throttle_endpoint_url is available and binary_endpoint
    //configurations are not provided.
    if (isBinaryPublisherEnabled()) {
        publishBinaryGlobalThrottleEvent(throttleEvent);
        printDebug(KEY_THROTTLE_UTIL, "ThrottleMessage is added to the event queue");
    } else {
        publishHttpGlobalThrottleEvent(throttleEvent);
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
    clientRequest.setHeader(AUTHORIZATION_HEADER, BASIC_PREFIX_WITH_SPACE + encodedBasicAuthHeader);
    clientRequest.setPayload(sendEvent);

    var response = httpThrottleEndpoint->post("/throttleEventReceiver", clientRequest);
    if (response is http:Response) {
        int responseCode = response.statusCode;
        printDebug(KEY_THROTTLE_UTIL, "\nStatus Code: " + responseCode.toString());
        if(responseCode != 200) {
            printError(KEY_THROTTLE_UTIL, "Error while publishing to traffic manager. Returned status code : " +
            responseCode.toString());
        }
    } else {
        printError(KEY_THROTTLE_UTIL, "Throttle event publishing is failed due to: " + response.reason(), response);
    }
}

function isBinaryPublisherEnabled() returns boolean {
    return  getConfigBooleanValue(BINARY_PUBLISHER_THROTTLE_CONF_INSTANCE_ID, ENABLED,
    DEFAULT_TM_BINARY_PUBLISHER_ENABLED);
}
