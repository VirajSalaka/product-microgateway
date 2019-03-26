package org.wso2.apimgt.gateway.cli.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.models.Swagger;
import io.swagger.parser.SwaggerParser;
import io.swagger.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.apache.commons.lang.StringUtils;
import org.wso2.apimgt.gateway.cli.exception.CLIRuntimeException;
import org.wso2.apimgt.gateway.cli.hashing.HashUtils;
import org.wso2.apimgt.gateway.cli.model.rest.ext.ExtendedAPI;

import java.io.File;
import java.io.IOException;

public class SwaggerUtils {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String generateAPIdForSwagger(String apiDefPath){

        String swaggerVersion = findSwaggerVersion(apiDefPath, true);
        String apiId;

        switch(swaggerVersion){
            case "2":
                apiId = generateAPIdForSwaggerV2(apiDefPath);
                break;
            case "3":
                apiId = generateAPIIdForSwaggerV3(apiDefPath);
                break;
            default:
                throw new CLIRuntimeException("Error: Swagger version is not identified");
        }
        return apiId;
    }

    private static String findSwaggerVersion(String apiDefinition, boolean isFilePath){
        try {
            JsonNode rootNode;
            if(isFilePath){
                //if filepath to the swagger is provided
                rootNode = objectMapper.readTree(new File(apiDefinition));
            } else {
                //if the raw string of the swagger is provided
                rootNode = objectMapper.readTree(apiDefinition);
            }

            if(rootNode.has("swagger") && rootNode.get("swagger").asText().contains("2")){
                //todo: introduce a constant for swagger version
                return "2";
            }
            else if(rootNode.has("openapi") && rootNode.get("openapi").asText().contains("3")){
                return "3";
            }
        } catch (IOException e) {
            throw new CLIRuntimeException("Error while reading the swagger file, check again.");
        }

        throw new CLIRuntimeException("Error while reading the swagger file, check again.");
    }

    private static String generateAPIdForSwaggerV2(String apiDefPath){
        SwaggerParser parser;
        Swagger swagger;
        parser = new SwaggerParser();
        swagger = parser.read(apiDefPath);

        String apiName = swagger.getInfo().getTitle();
        String apiVersion = swagger.getInfo().getVersion();

        return HashUtils.generateAPIId(apiName, apiVersion);
    }

    private static String generateAPIIdForSwaggerV3(String apiDefPath){

        OpenAPI openAPI = new OpenAPIV3Parser().read(apiDefPath);

        String apiName = openAPI.getInfo().getTitle();
        String apiVersion = openAPI.getInfo().getVersion();

        return HashUtils.generateAPIId(apiName, apiVersion);
    }


    //todo: check if this is required
    public static String generateSwaggerString(ExtendedAPI api){
        String swaggerVersion = findSwaggerVersion(api.getApiDefinition(), false);

        switch(swaggerVersion){
            case "2":
                Swagger swagger = new SwaggerParser().parse(api.getApiDefinition());
                swagger.setBasePath(api.getContext() + "/" + api.getVersion());

                return Json.pretty(swagger);
            case "3":
                return api.getApiDefinition();
            default:
                throw new CLIRuntimeException("Error: Swagger version is not identified");
        }
    }

    static String[] getAPINameVersionFromSwagger(String apiDefPath){
        String swaggerVersion = findSwaggerVersion(apiDefPath, true);

        switch(swaggerVersion){
            case "2":
                Swagger swagger = new SwaggerParser().read(apiDefPath);
                return new String[] {swagger.getInfo().getTitle(), swagger.getInfo().getVersion()};

            case "3":
                OpenAPI openAPI = new OpenAPIV3Parser().read(apiDefPath);
                return new String[] {openAPI.getInfo().getTitle(), openAPI.getInfo().getVersion()};
        }
        //this is already checked by the parser. Therefore never reach this statement
        throw new CLIRuntimeException("Name and version is not provided in the OpenAPI definition");
    }

    public static String getBasePathFromSwagger(String apiDefPath){
        String swaggerVersion = findSwaggerVersion(apiDefPath, true);

        if(swaggerVersion.equals("2")){
            Swagger swagger = new SwaggerParser().read(apiDefPath);
            if(!StringUtils.isEmpty(swagger.getBasePath())){
                return swagger.getBasePath();
            }
        }
        return null;
    }

}
