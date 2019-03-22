package org.wso2.apimgt.gateway.cli.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.wso2.apimgt.gateway.cli.exception.CLIInternalException;
import org.wso2.apimgt.gateway.cli.hashing.HashUtils;
import org.wso2.apimgt.gateway.cli.model.route.APIRouteEndpointConfig;

import java.io.File;
import java.io.IOException;

public class RouteUtils {
    //todo: rename variable name
    private static final ObjectMapper OBJECT_MAPPER_YAML = new ObjectMapper(new YAMLFactory());
    //todo: set routesConfigPath as class variable

    public static void saveGlobalEpAndBasepath(String apiName, String apiVersion, String routesConfigPath, String basePath,
                                        JsonNode endpointConfig){
        String apiId = HashUtils.generateAPIId(apiName,apiVersion);
        JsonNode routesConfig = getRoutesConfig(routesConfigPath);
        addBasePath(routesConfig, apiId, basePath);
        addGlobalEndpoint(routesConfig, apiName, apiVersion, apiId, endpointConfig);
        writeRoutesConfig(routesConfig, routesConfigPath);

    }

    private static void addBasePath(JsonNode rootNode, String apiId, String basePath){

            JsonNode basePathsNode = rootNode.get("basePaths");
            //todo: validate whether the basePath is already available
            ((ObjectNode) basePathsNode).put(apiId, basePath);
    }

    private static void addGlobalEndpoint(JsonNode rootNode, String apiName, String apiVersion, String apiId,
                                   JsonNode endpointConfig){

            JsonNode globalEpsNode = rootNode.get("global_endpoints");
            ObjectNode globalEp = OBJECT_MAPPER_YAML.createObjectNode();
            globalEp.put("name", apiName).put("version", apiVersion);
            globalEp.set("endpointConfig", endpointConfig);
            //todo: check if the apiId is unique
            ((ObjectNode) globalEpsNode).set(apiId, globalEp);
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
