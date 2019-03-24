package org.wso2.apimgt.gateway.cli.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.wso2.apimgt.gateway.cli.exception.CLIInternalException;
import org.wso2.apimgt.gateway.cli.exception.CLIRuntimeException;
import org.wso2.apimgt.gateway.cli.hashing.HashUtils;
import org.wso2.apimgt.gateway.cli.model.rest.APIEndpointSecurityDTO;
import org.wso2.apimgt.gateway.cli.model.rest.Endpoint;
import org.wso2.apimgt.gateway.cli.model.rest.EndpointConfig;
import org.wso2.apimgt.gateway.cli.model.rest.ext.ExtendedAPI;
import org.wso2.apimgt.gateway.cli.model.route.APIRouteEndpointConfig;
import org.wso2.apimgt.gateway.cli.model.route.EndpointListRouteDTO;
import org.wso2.apimgt.gateway.cli.model.route.EndpointType;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class RouteUtils {
    //todo: rename variable name
    private static final ObjectMapper OBJECT_MAPPER_YAML = new ObjectMapper(new YAMLFactory());
    //todo: set routesConfigPath as class variable
    private static final ObjectMapper OBJECT_MAPPER_JSON = new ObjectMapper();

    public static void saveGlobalEpAndBasepath(String apiName, String apiVersion, String routesConfigPath, String basePath,
                                        String endpointConfigJson){
        String apiId = HashUtils.generateAPIId(apiName,apiVersion);
        JsonNode routesConfig = getRoutesConfig(routesConfigPath);
        addBasePath(routesConfig, apiId, basePath);
        addGlobalEndpoint(routesConfig, apiName, apiVersion, apiId, endpointConfigJson);
        writeRoutesConfig(routesConfig, routesConfigPath);

    }

    public static void saveGlobalEpAndBasepath(List<ExtendedAPI> apiList, String routesConfigPath) throws IOException {
        JsonNode routesConfig = getRoutesConfig(routesConfigPath);
        for(ExtendedAPI api : apiList){
            APIRouteEndpointConfig apiEpConfig = new APIRouteEndpointConfig();
            apiEpConfig.setApiName(api.getName());
            apiEpConfig.setApiVersion(api.getVersion());

            //todo: implement its own method
            EndpointListRouteDTO[] prodSandEndpointList = convertEndpointConfig(OpenApiCodegenUtils
                    .getEndpointConfig(api.getEndpointConfig()), api.getEndpointSecurity());
            apiEpConfig.setProdEndpointList(prodSandEndpointList[0]);
            apiEpConfig.setSandboxEndpointList(prodSandEndpointList[1]);

            String apiId = HashUtils.generateAPIId(api.getName(), api.getVersion());
            //todo: constant for "/"
            addBasePath(routesConfig, apiId, api.getContext() + "/" + api.getVersion());
            addAPIRouteEndpointConfigAsGlobalEp(routesConfig, apiId, apiEpConfig);
        }
        writeRoutesConfig(routesConfig, routesConfigPath);
    }

    //todo: decide whether we are going to use same endpointConfig json structure or not
    private static EndpointListRouteDTO[] convertEndpointConfig(EndpointConfig endpointConfig, APIEndpointSecurityDTO
            securityDTO){
        EndpointListRouteDTO prodList = new EndpointListRouteDTO();
        EndpointListRouteDTO sandboxList = new EndpointListRouteDTO();

        try {
            prodList.setType(EndpointType.valueOf(endpointConfig.getEndpointType()));
            sandboxList.setType(EndpointType.valueOf(endpointConfig.getEndpointType()));
        } catch (IllegalArgumentException e){
            throw new CLIRuntimeException("The provided Endpoint type is not valid.", e);
        }

        //all the endpoints are added to same list to make structure simple
        for(Endpoint endpoint: endpointConfig.getProdEndpoints().getEndpoints()){
            prodList.addEndpoint(endpoint.getEndpointUrl());
        }
        for(Endpoint endpoint: endpointConfig.getProdFailoverEndpoints().getEndpoints()){
            prodList.addEndpoint(endpoint.getEndpointUrl());
        }
        for(Endpoint endpoint: endpointConfig.getSandEndpoints().getEndpoints()){
            sandboxList.addEndpoint(endpoint.getEndpointUrl());
        }
        for(Endpoint endpoint: endpointConfig.getSandFailoverEndpoints().getEndpoints()){
            sandboxList.addEndpoint(endpoint.getEndpointUrl());
        }

        prodList.setSecurityConfig(securityDTO);
        sandboxList.setSecurityConfig(securityDTO);

        return new EndpointListRouteDTO[]{prodList, sandboxList};
    }

    private static void addBasePath(JsonNode rootNode, String apiId, String basePath){

            JsonNode basePathsNode = rootNode.get("basePaths");
            //todo: validate whether the basePath is already available
            ((ObjectNode) basePathsNode).put(apiId, basePath);
    }

    private static void addGlobalEndpoint(JsonNode rootNode, String apiName, String apiVersion, String apiId,
                                   String endpointConfigJson){

        //todo: validate the endpointConfig
        JsonNode endpointConfig;
        try {
            endpointConfig = OBJECT_MAPPER_JSON.readTree(endpointConfigJson);
        } catch (IOException e) {
            throw new CLIRuntimeException("Error while parsing the provided endpointConfig Json");
        }

        APIRouteEndpointConfig apiEpConfig = new APIRouteEndpointConfig();
        apiEpConfig.setApiName(apiName);
        apiEpConfig.setApiVersion(apiVersion);
        try {
            apiEpConfig.setProdEndpointList(OBJECT_MAPPER_JSON.readValue(endpointConfig.get("prod").asText(),
                    EndpointListRouteDTO.class));
            apiEpConfig.setSandboxEndpointList(OBJECT_MAPPER_JSON.readValue(endpointConfig.get("sandbox").asText(),
                    EndpointListRouteDTO.class));
        } catch (IOException e) {
            throw new CLIRuntimeException("Error while parsing the provided EndpointConfig");
        }
        addAPIRouteEndpointConfigAsGlobalEp(rootNode, apiId, apiEpConfig);
    }

    private static void addAPIRouteEndpointConfigAsGlobalEp(JsonNode rootNode, String apiId,
                                                            APIRouteEndpointConfig apiEpConfig){
        JsonNode globalEpsNode = rootNode.get("global_endpoints");
        //todo: check if the apiId is unique
        ((ObjectNode) globalEpsNode).set(apiId, OBJECT_MAPPER_YAML.valueToTree(apiEpConfig));
    }

    private static void writeRoutesConfig(JsonNode routesConfig, String routesConfigPath){
        try{
            OBJECT_MAPPER_YAML.writeValue(new File(routesConfigPath), routesConfig);
        }catch(IOException e){
            throw new CLIInternalException("Error while writing to the routes.yaml");
        }
    }


    private static JsonNode getRoutesConfig(String routesConfigPath){
        if(routesConfigPath == null){
            throw new CLIInternalException("routes.yaml is not provided");
        }
        JsonNode rootNode;
        try {
            rootNode = OBJECT_MAPPER_YAML.readTree(new File(routesConfigPath));
        } catch (IOException e) {
            throw new CLIInternalException("Error while reading the routesConfiguration in path : " + routesConfigPath);
        }
        if(rootNode == null){
            return OBJECT_MAPPER_YAML.createObjectNode();
        }
        JsonNode basePathsNode = null;
        JsonNode globalEpsNode = null;
        JsonNode resourcesNode = null;

        if(!rootNode.isNull()){
            basePathsNode = rootNode.get("basepaths");
            globalEpsNode = rootNode.get("global_endpoints");
            resourcesNode = rootNode.get("resources");
        }

        if(basePathsNode == null){
            basePathsNode = OBJECT_MAPPER_YAML.createObjectNode();
            ((ObjectNode) rootNode).set("basepaths", basePathsNode);
        }

        if(globalEpsNode == null){
            globalEpsNode = OBJECT_MAPPER_YAML.createObjectNode();
            ((ObjectNode) rootNode).set("global_endpoints", globalEpsNode);
        }

        if(resourcesNode == null){
            resourcesNode = OBJECT_MAPPER_YAML.createObjectNode();
            ((ObjectNode) rootNode).set("resources", resourcesNode);
        }
        return rootNode;
    }

    public static String getBasePath(String apiName, String apiVersion, String routesConfigPath){
        String apiId = HashUtils.generateAPIId(apiName, apiVersion);
        JsonNode rootNode = getRoutesConfig(routesConfigPath);
        return rootNode.get("basepaths").get(apiId).asText();
    }

    public static APIRouteEndpointConfig getGlobalEpConfig(String apiName, String apiVersion, String routesConfigPath){
        String apiId = HashUtils.generateAPIId(apiName, apiVersion);
        JsonNode rootNode = getRoutesConfig(routesConfigPath);
        JsonNode globalEpConfig = rootNode.get("global_endpoints").get(apiId);
        APIRouteEndpointConfig apiRouteEndpointConfig;

        try {
            apiRouteEndpointConfig = OBJECT_MAPPER_YAML.readValue(globalEpConfig.asText(),
                    APIRouteEndpointConfig.class);
        } catch (IOException e) {
            throw new CLIInternalException("Error while mapping Global Endpoint JsonNode object to " +
                    "APIRouteEndpointConfig object");
        }
        return apiRouteEndpointConfig;
    }
}
