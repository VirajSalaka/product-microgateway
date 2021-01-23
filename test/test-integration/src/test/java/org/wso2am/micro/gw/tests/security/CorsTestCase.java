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

package org.wso2am.micro.gw.tests.security;

import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.netty.handler.codec.http.HttpHeaderNames;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2am.micro.gw.tests.common.BaseTestCase;
import org.wso2am.micro.gw.tests.common.model.API;
import org.wso2am.micro.gw.tests.common.model.ApplicationDTO;
import org.wso2am.micro.gw.tests.util.ApiDeployment;
import org.wso2am.micro.gw.tests.util.ApiProjectGenerator;
import org.wso2am.micro.gw.tests.util.HttpResponse;
import org.wso2am.micro.gw.tests.util.HttpsClientRequest;
import org.wso2am.micro.gw.tests.util.TestConstant;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CorsTestCase extends BaseTestCase {
    protected String jwtTokenProd;
    private String allowedOrigin = "http://test.com";
    private String allowedMethods = "GET,POST";
    private String allowedHeaders = "Authorization,Content-Type,SOAPAction";

    @BeforeClass(description = "initialise the setup")
    void start() throws Exception {
        super.startMGW(null, true);

        //deploy the api
        //api yaml file should put to the resources/apis/openApis folder
        String apiZipfile = ApiProjectGenerator.createApictlProjZip("backendtls/openapi.yaml",
                "backendtls/backend.crt");
        ApiDeployment.deployAPI(apiZipfile);

        //TODO: (VirajSalaka) change the token
        //generate JWT token from APIM
        API api = new API();
        api.setName("PetStoreAPI");
        api.setContext("petstore/v1");
        api.setProdEndpoint(getMockServiceURLHttp("/echo/prod"));
        api.setVersion("1.0.0");
        api.setProvider("admin");

        //Define application info
        ApplicationDTO application = new ApplicationDTO();
        application.setName("jwtApp");
        application.setTier("Unlimited");
        application.setId((int) (Math.random() * 1000));

        jwtTokenProd = getJWT(api, application, "Unlimited", TestConstant.KEY_TYPE_PRODUCTION, 3600);
    }

    @Test(description = "Success Scenario, with allow credentials is set to true.")
    public void CheckCORSHeadersInPreFlightResponse() throws Exception {
        // AccessControlAllowCredentials set to true
        // TODO: (VirajSalaka) MaxAge check
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaderNames.ORIGIN.toString(), "http://test1.com");
        headers.put(HttpHeaderNames.ACCESS_CONTROL_REQUEST_METHOD.toString(), "POST");
        HttpResponse response = HttpsClientRequest.doOption(getServiceURLHttps(
                "/corsTest/pet/2") , headers);

        Assert.assertNotNull(response);
        Assert.assertEquals(response.getResponseCode(), HttpStatus.SC_OK,"Response code mismatched");
        Assert.assertNotNull(response.getHeaders());
        Assert.assertNotNull(response.getHeaders().get(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN.toString()),
                HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN.toString() + " header is unavailable");
        Assert.assertEquals(response.getHeaders().get(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN.toString()),
                allowedOrigin);
        Assert.assertNotNull(response.getHeaders().get(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS.toString()),
                HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS.toString() + " header is unavailable");
        Assert.assertEquals(response.getHeaders().get(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS.toString())
                .replaceAll(" ", ""), allowedMethods);
        Assert.assertNotNull(response.getHeaders().get(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS.toString()),
                HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS.toString() + " header is unavailable");
        Assert.assertEquals(response.getHeaders().get(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS.toString()),
                allowedHeaders);
        Assert.assertNotNull(response.getHeaders().get(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS.toString()),
                HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS.toString() + " header is unavailable");
        Assert.assertTrue(Boolean.parseBoolean(response.getHeaders()
                .get(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS.toString())));
    }

    @Test(description = "Success Scenario, with allow credentials is set to true.")
    public void CheckCORSHeadersInSimpleResponse() throws IOException {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaderNames.ORIGIN.toString(), "http://test2.com");
        headers.put(HttpHeaderNames.AUTHORIZATION.toString(), "Bearer " + jwtTokenProd);
        HttpResponse response = HttpsClientRequest.doGet("/corsTest/pet/2", headers);

        Assert.assertNotNull(response);
        Assert.assertEquals(response.getResponseCode(), HttpStatus.SC_OK,"Response code mismatched");
        Assert.assertNotNull(response.getHeaders());
        Assert.assertNotNull(response.getHeaders().get(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN.toString()),
                HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN.toString() + " header is unavailable");
        Assert.assertEquals(response.getHeaders().get(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN.toString()),
                allowedOrigin);


    }
}
