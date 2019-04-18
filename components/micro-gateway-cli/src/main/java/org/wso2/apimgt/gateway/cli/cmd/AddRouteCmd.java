package org.wso2.apimgt.gateway.cli.cmd;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.apimgt.gateway.cli.exception.CLIRuntimeException;
import org.wso2.apimgt.gateway.cli.utils.GatewayCmdUtils;
import org.wso2.apimgt.gateway.cli.utils.OpenAPICodegenUtils;
import org.wso2.apimgt.gateway.cli.utils.RouteUtils;

import java.io.PrintStream;

/**
 * This class represents the "add route" command and it holds arguments and flags specified by the user.
 */
@Parameters(commandNames = "add route", commandDescription = "add api to the microgateway")
public class AddRouteCmd implements GatewayLauncherCmd {
    private static final Logger logger = LoggerFactory.getLogger(AddRouteCmd.class);
    private static PrintStream outStream = System.out;

    @Parameter(names = {"--project"}, hidden = true)
    private String projectName;

    @Parameter(names = "--java.debug", hidden = true)
    private String javaDebugPort;

    @Parameter(names = {"-e", "--endpoint"}, hidden = true)
    private String endpoint;

    @Parameter(names = {"-ec", "--endpoint-config"}, hidden = true)
    private String endpointConfig;

    @Parameter(names = {"-r", "--resource"}, hidden = true)
    private String resourceId;

    @Parameter(names = {"-a", "--api"}, hidden = true)
    private String apiId;

    @Parameter(names = {"-f", "--force"}, hidden = true, arity = 0)
    private boolean isForcefully;

    @Override
    public void execute() {
        projectName = GatewayCmdUtils.buildProjectName(projectName);

        //if both ids are provided, should not proceed
        if ((apiId == null || apiId.isEmpty()) && (resourceId == null || resourceId.isEmpty())) {
            throw new CLIRuntimeException("Error: API Id or resource id is not provided.");
        }

        //if no id is provided should not proceed
        if ((apiId != null) && (resourceId != null)) {
            throw new CLIRuntimeException("Error: Please provide one Id.");
        }

        if (apiId != null) {
            //check if the apiId is already available
            if (!RouteUtils.hasApiInRoutesConfig(apiId)) {
                throw GatewayCmdUtils.createUsageException("API id '" + apiId + "' does not exist.");
            }
        } else {
            if (RouteUtils.hasResourceInRoutesConfig(resourceId) && !isForcefully) {
                throw GatewayCmdUtils.createUsageException("Resource id `" + resourceId + "` already has an " +
                        "endpointConfiguration. use -f or --force to forcefully update the endpointConfiguration");
            }
            if (OpenAPICodegenUtils.getResource(projectName, resourceId) == null) {
                throw new CLIRuntimeException("Provided resource id '" + resourceId + "' does not exist.");
            }
        }

        //setup endpoint configuration json
        String endpointConfigString;
        if (StringUtils.isEmpty(endpointConfig)) {
            if (StringUtils.isEmpty(endpoint)) {
                /*
                 * if an endpoint config or an endpoint is not provided as an argument, it is prompted from
                 * the user
                 */
                if ((endpoint = GatewayCmdUtils.promptForTextInput(outStream, "Enter Endpoint URL for Resource " +
                        resourceId + ": ")).trim().isEmpty()) {
                    throw GatewayCmdUtils.createUsageException("Micro gateway setup failed: empty endpoint.");
                }
            }
            endpointConfigString = "{\"prod\": {\"type\": \"http\", \"endpoints\" : [\"" + endpoint.trim() + "\"]}}";
        } else {
            endpointConfigString = OpenAPICodegenUtils.readJson(endpointConfig);
        }

        if (apiId != null) {
            RouteUtils.updateAPIRoute(apiId, endpointConfigString);
        } else {
            RouteUtils.saveResourceRoute(resourceId, endpointConfigString);
            outStream.println("Successfully added route for resource ID '" + resourceId + "'.");
        }
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setParentCmdParser(JCommander parentCmdParser) {

    }
}
