/*
 *  Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wso2.micro.gateway.jwt.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Class to retrieve user claims from Key Manager component of the API Manager.
 */
public class DefaultMGWClaimRetriever extends AbstractMGWClaimRetriever {
    private static final Logger logger = LogManager.getLogger(AbstractMGWJWTGenerator.class);

    public DefaultMGWClaimRetriever(Map<String, String> configurationMap) {
        super(configurationMap);
    }

    @Override
    public List<ClaimDTO> retrieveClaims(Map<String, Object> authContext) {
        System.setProperty("javax.net.ssl.trustStore", "/Users/viraj/Documents/APIM-2/resolve_merge/" +
                "viraj_4/product-microgateway/distribution/target/wso2am-micro-gw-macos-3.2.0-alpha2-SNAPSHOT/" +
                "runtime/bre/security/ballerinaTruststore.p12");
        System.setProperty("javax.net.ssl.trustStorePassword", "ballerina");
        try {
            HttpsURLConnection urlConn = null;
            String userInfoEndpoint = "https://localhost:9443/user-info/claims/generate";
            URL url = new URL(userInfoEndpoint);
            urlConn = (HttpsURLConnection) url.openConnection();
            urlConn.setHostnameVerifier((s, sslSession) -> true);
            urlConn.setDoOutput(true);
            urlConn.setRequestMethod("POST");
            urlConn.setRequestProperty("Authorization", "Basic YWRtaW46YWRtaW4=");
            urlConn.setRequestProperty("Content-Type", "application/json");
            String jsonInputString = "{\"username\": \"admin\"}";
            try (OutputStream os = urlConn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            int responseCode = urlConn.getResponseCode();
            if (responseCode == 200) {
                String responseStr = getResponseString(urlConn.getInputStream());
                ObjectMapper mapper = new ObjectMapper();
                return (List<ClaimDTO>) mapper.readValue(responseStr, Map.class).get("list");
            }
            logger.error("Claim Retrieval request is failed with the response code : " + responseCode);
        } catch (IOException e) {
            logger.error("Error while retrieving user claims from remote endpoint", e);
        }
        return null;
    }


    /**
     * Get inputStream string as string.
     *
     * @param input input stream
     * @return inout stream content as string
     * @throws IOException if read went wrong
     */
    public static String getResponseString(InputStream input) throws IOException {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            StringBuilder content = new StringBuilder();
            String str;
            while ((str = buffer.readLine()) != null) {
                content.append(str);
            }
            return content.toString();
        }
    }
}
