/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.choreo.connect.tests.util;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;

import java.io.IOException;
import java.util.HashMap;

public class AnalyticsUtils {
    public static String analyticsURL = "http://localhost:2399/analytics";

    /**
     * Clear the event array.
     */
    public static void clearEventArray() {
        try {
            HttpResponse analyticsResponse =
                    HttpClientRequest.doGet(analyticsURL + "/clear", new HashMap<>());
            Assert.assertNotNull(analyticsResponse);
            Assert.assertEquals(analyticsResponse.getResponseCode(), 200,
                    "Analytics event array is not cleared");
        } catch (IOException e) {
            e.printStackTrace();
            Assert.assertNull(e, "Error while clearing the analytics event array.");
        }
    }

    public static String validatePublishedEvent(String apiId, String apiType, String apiName, String apiVersion,
                                                 String apiCreator, String apiCreatorTenantDomain, String apiMethod,
                                                 String apiResourceTemplate, String targetResponseCode,
                                                 String destination, String keyType, String applicationId,
                                                 String applicationName, String applicationOwner,
                                                 String regionId) {
        try {
            HttpResponse analyticsResponse =
                    HttpClientRequest.doGet(analyticsURL + "/get", new HashMap<>());
            Assert.assertEquals(analyticsResponse.getResponseCode(), 200,
                    "Analytics event array is not returned");
            Assert.assertNotNull(analyticsResponse);
            JSONArray jsonArray = new JSONArray(analyticsResponse.getData());
            String arrayElement = jsonArray.get(0).toString();

            Assert.assertTrue(arrayElement.contains(apiId));
            Assert.assertTrue(arrayElement.contains(apiType));
            Assert.assertTrue(array)
            Assert.assertTrue(analyticsResponse.getData().contains("SubscriptionValidationTestAPI"),
                    analyticsResponse.getData());
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            Assert.assertNull(e, "Error while clearing the analytics event array.");
        }
        return "";
    }
}
