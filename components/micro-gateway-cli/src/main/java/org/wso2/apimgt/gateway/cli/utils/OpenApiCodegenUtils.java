/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.apimgt.gateway.cli.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.ballerinalang.net.grpc.builder.components.EndPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.apimgt.gateway.cli.constants.GatewayCliConstants;
import org.wso2.apimgt.gateway.cli.constants.RESTServiceConstants;
import org.wso2.apimgt.gateway.cli.exception.CLIInternalException;
import org.wso2.apimgt.gateway.cli.exception.CLIRuntimeException;
import org.wso2.apimgt.gateway.cli.model.rest.Endpoint;
import org.wso2.apimgt.gateway.cli.model.rest.EndpointConfig;
import org.wso2.apimgt.gateway.cli.model.rest.EndpointList;
import org.wso2.apimgt.gateway.cli.model.rest.EndpointUrlTypeEnum;
import org.wso2.apimgt.gateway.cli.model.rest.ext.ExtendedAPI;
import org.wso2.apimgt.gateway.cli.model.rest.route.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Utilities used by ballerina code generator.
 */
public class OpenApiCodegenUtils {

    private static final Logger logger = LoggerFactory.getLogger(OpenApiCodegenUtils.class);

    public static String readApi(String filePath) {
        String responseStr;
        try {
            responseStr = new String(Files.readAllBytes(Paths.get(filePath)), GatewayCliConstants.CHARSET_UTF8);
        } catch (IOException e) {
            logger.error("Error while reading api definition.", e);
            throw new CLIInternalException("Error while reading api definition.");
        }
        return responseStr;
    }

    public static void setAdditionalConfigs(ExtendedAPI api) throws IOException {
        String endpointConfig = api.getEndpointConfig();
        api.setEndpointConfigRepresentation(getEndpointConfig(endpointConfig));
    }

    public static void setAdditionalConfigs(ExtendedAPI api, String projectName, String apiName, String apiVersion) {
        api.setEndpointConfigRepresentation(getEndpointConfig(projectName, apiName, apiVersion));
    }

    private static EndpointConfig getEndpointConfig(String endpointConfig) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode;
        EndpointConfig endpointConf = new EndpointConfig();
        rootNode = mapper.readTree(endpointConfig);
        String endpointType = rootNode.path(RESTServiceConstants.ENDPOINT_TYPE).asText();
        endpointConf.setEndpointType(endpointType);

        if (RESTServiceConstants.HTTP.equalsIgnoreCase(endpointType) || RESTServiceConstants.FAILOVER.
                equalsIgnoreCase(endpointType)) {
            JsonNode prodEndpointNode = rootNode.get(RESTServiceConstants.PRODUCTION_ENDPOINTS);
            if (prodEndpointNode != null) {
                Endpoint prod = new Endpoint();
                prod.setEndpointUrl(prodEndpointNode.get(RESTServiceConstants.URL).asText());
                endpointConf.addProdEndpoint(prod);
            }

            JsonNode sandEndpointNode = rootNode.get(RESTServiceConstants.SANDBOX_ENDPOINTS);
            if (sandEndpointNode != null) {
                Endpoint sandbox = new Endpoint();
                sandbox.setEndpointUrl(sandEndpointNode.get(RESTServiceConstants.URL).asText());
                endpointConf.addSandEndpoint(sandbox);
            }

            if (RESTServiceConstants.FAILOVER.equalsIgnoreCase(endpointType)) {
                //ballerina does not treat primary/failover endpoint separately. Hence, primary production/sandbox
                //  eps (if any) will be added into failover list.
                if (endpointConf.getProdEndpoints() != null
                        && endpointConf.getProdEndpoints().getEndpoints().size() > 0) {
                    endpointConf.addProdFailoverEndpoint(endpointConf.getProdEndpoints().getEndpoints().get(0));
                }
                if (endpointConf.getSandEndpoints() != null
                        && endpointConf.getSandEndpoints().getEndpoints().size() > 0) {
                    endpointConf.addSandFailoverEndpoint(endpointConf.getSandEndpoints().getEndpoints().get(0));
                }

                //Adding additional production/sandbox failover endpoints
                JsonNode prodFailoverEndpointNode = rootNode.withArray(RESTServiceConstants.PRODUCTION_FAILOVERS);
                if (prodFailoverEndpointNode != null) {
                    for (JsonNode node : prodFailoverEndpointNode) {
                        Endpoint endpoint = new Endpoint();
                        endpoint.setEndpointUrl(node.get(RESTServiceConstants.URL).asText());
                        endpointConf.addProdFailoverEndpoint(endpoint);
                    }
                }

                JsonNode sandFailoverEndpointNode = rootNode.withArray(RESTServiceConstants.SANDBOX_FAILOVERS);
                if (sandFailoverEndpointNode != null) {
                    for (JsonNode node : sandFailoverEndpointNode) {
                        Endpoint endpoint = new Endpoint();
                        endpoint.setEndpointUrl(node.get(RESTServiceConstants.URL).asText());
                        endpointConf.addSandFailoverEndpoint(endpoint);
                    }
                }
            }
        } else if (RESTServiceConstants.LOAD_BALANCE.equalsIgnoreCase(endpointType)) {
            JsonNode prodEndpoints = rootNode.withArray(RESTServiceConstants.PRODUCTION_ENDPOINTS);
            if (prodEndpoints != null) {
                for (JsonNode node : prodEndpoints) {
                    Endpoint endpoint = new Endpoint();
                    endpoint.setEndpointUrl(node.get(RESTServiceConstants.URL).asText());
                    endpointConf.addProdEndpoint(endpoint);
                }
            }

            JsonNode sandboxEndpoints = rootNode.withArray(RESTServiceConstants.SANDBOX_ENDPOINTS);
            if (sandboxEndpoints != null) {
                for (JsonNode node : sandboxEndpoints) {
                    Endpoint endpoint = new Endpoint();
                    endpoint.setEndpointUrl(node.get(RESTServiceConstants.URL).asText());
                    endpointConf.addSandEndpoint(endpoint);
                }
            }
        }
        return endpointConf;
    }

    private static EndpointConfig getEndpointConfig(String projectName, String apiName, String version){
        //todo: move this method to some other class
        String routesPath = GatewayCmdUtils.getProjectRoutesConfFilePath(projectName);
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        APIListRouteDTO apiListRouteDTO = null;
        try {
            apiListRouteDTO = objectMapper.readValue(new File(routesPath), APIListRouteDTO.class);
        } catch (IOException e) {
            new CLIInternalException("cannot parse routes.yaml");
        }

        APIRouteDTO api = apiListRouteDTO.findByAPIName(apiName);
        if(api == null){
            new CLIRuntimeException("found no api named "+ apiName + " in routes.yaml");
        }

        APIVersionRouteDTO apiVersionRouteDTO = api.findByAPIVersion(version);
        if(apiVersionRouteDTO == null){
            new CLIRuntimeException("found no version named " + apiVersionRouteDTO + " in routes.yaml");
        }
        EndpointConfig endpointConfig = new EndpointConfig();
        EndpointList[] endpointLists = generateEpListFromEnvDTO(apiVersionRouteDTO);

        endpointConfig.setProdEndpoints(restoreNullForEmptyEndpointList(endpointLists[0]));
        endpointConfig.setProdFailoverEndpoints(restoreNullForEmptyEndpointList(endpointLists[1]));
        endpointConfig.setSandEndpoints(restoreNullForEmptyEndpointList(endpointLists[2]));
        endpointConfig.setSandFailoverEndpoints(restoreNullForEmptyEndpointList(endpointLists[3]));

        return endpointConfig;
    }

    private static EndpointList restoreNullForEmptyEndpointList(EndpointList epList){
        if(epList.getEndpoints().size() == 0){
            return null;
        }
        return epList;
    }

    private static EndpointList[] generateEpListFromEnvDTO(APIVersionRouteDTO api){
        EndpointListRouteDTO basicProdEp = api.getProd().getBasicEndpoint();
        EndpointListRouteDTO basicSandEp = api.getSandbox().getBasicEndpoint();

        EndpointList prodEpList = new EndpointList(EndpointUrlTypeEnum.PROD);
        EndpointList prodFailoverEpList = new EndpointList(EndpointUrlTypeEnum.PROD);
        EndpointList sandboxEpList = new EndpointList(EndpointUrlTypeEnum.SAND);
        EndpointList sandboxFailoverEpList = new EndpointList(EndpointUrlTypeEnum.SAND);

        //todo: remove the redundant code
        if(basicProdEp != null){
            if(basicProdEp.getType().equals(EndpointType.DEFAULT)){
                prodEpList.addEndpoint(new Endpoint(((DefaultEndpointListDTO) basicProdEp).getEndpoint()));
            }else if(basicProdEp.getType().equals(EndpointType.LOAD_BALANCE)){
                List<String> epList = ((LoadBalanceEndpointListDTO) basicProdEp).getEndpointList();
                for(String epUrl : epList){
                    prodEpList.addEndpoint(new Endpoint(epUrl));
                }
            }else if(basicProdEp.getType().equals(EndpointType.FAILOVER)){
                prodEpList.addEndpoint(new Endpoint(((FailoverEndpointListDTO) basicProdEp).getDefaultEndpoint()));
                List<String> epList = ((FailoverEndpointListDTO) basicProdEp).getEndpointList();
                for(String epUrl : epList){
                    prodFailoverEpList.addEndpoint(new Endpoint(epUrl));
                }
            }
        }

        if(basicSandEp != null){
            if(basicSandEp.getType().equals(EndpointType.DEFAULT)){
                sandboxEpList.addEndpoint(new Endpoint(((DefaultEndpointListDTO) basicSandEp).getEndpoint()));
            }else if(basicSandEp.getType().equals(EndpointType.LOAD_BALANCE)){
                List<String> epList = ((LoadBalanceEndpointListDTO) basicSandEp).getEndpointList();
                for(String epUrl : epList){
                    sandboxEpList.addEndpoint(new Endpoint(epUrl));
                }
            }else if(basicSandEp.getType().equals(EndpointType.FAILOVER)){
                sandboxEpList.addEndpoint(new Endpoint(((FailoverEndpointListDTO) basicSandEp).getDefaultEndpoint()));
                List<String> epList = ((FailoverEndpointListDTO) basicSandEp).getEndpointList();
                for(String epUrl : epList){
                    sandboxFailoverEpList.addEndpoint(new Endpoint(epUrl));
                }
            }
        }

        return new EndpointList[]{prodEpList, prodFailoverEpList, sandboxEpList, sandboxFailoverEpList};
    }
}
