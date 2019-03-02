package org.wso2.apimgt.gateway.cli.cmd;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.gson.Gson;
import io.swagger.models.Swagger;
import io.swagger.parser.SwaggerParser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.apimgt.gateway.cli.config.TOMLConfigParser;
import org.wso2.apimgt.gateway.cli.constants.RESTServiceConstants;
import org.wso2.apimgt.gateway.cli.exception.CLIInternalException;
import org.wso2.apimgt.gateway.cli.exception.CLIRuntimeException;
import org.wso2.apimgt.gateway.cli.exception.CliLauncherException;
import org.wso2.apimgt.gateway.cli.exception.ConfigParserException;
import org.wso2.apimgt.gateway.cli.model.config.Client;
import org.wso2.apimgt.gateway.cli.model.config.Config;
import org.wso2.apimgt.gateway.cli.model.config.Token;
import org.wso2.apimgt.gateway.cli.model.config.TokenBuilder;
import org.wso2.apimgt.gateway.cli.model.rest.APIDetailedDTO;
import org.wso2.apimgt.gateway.cli.oauth.OAuthService;
import org.wso2.apimgt.gateway.cli.oauth.OAuthServiceImpl;
import org.wso2.apimgt.gateway.cli.rest.RESTAPIService;
import org.wso2.apimgt.gateway.cli.rest.RESTAPIServiceImpl;
import org.wso2.apimgt.gateway.cli.utils.GatewayCmdUtils;
import org.wso2.apimgt.gateway.cli.utils.OpenApiCodegenUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * This class represents the "register" command and it holds arguments and flags specified by the user.
 */
@Parameters(commandNames = "register", commandDescription = "register information")
public class RegisterCmd implements GatewayLauncherCmd {
    private static final Logger logger = LoggerFactory.getLogger(RegisterCmd.class);
    private static PrintStream outStream = System.out;

    @SuppressWarnings("unused")
    @Parameter(names = "--java.debug", hidden = true)
    private String javaDebugPort;

    @Parameter(names = {"-u", "--username"}, hidden = true)
    private String username;

    @Parameter(names = {"-p", "--password"}, hidden = true)
    private String password;

    @Parameter(names = {"-s", "--server-url"}, hidden = true)
    private String baseUrl;

    @Parameter(names = {"-oa", "--openapi"}, hidden = true)
    private String openApi;

    @Parameter(names = {"-e", "--endpoint"}, hidden = true)
    private String endpoint;

    @Parameter(names = {"-ec", "--endpointConfig"}, hidden = true)
    private String endpointConfig;

    @Parameter(names = {"-t", "--truststore"}, hidden = true)
    private String trustStoreLocation;

    @Parameter(names = {"-w", "--truststore-pass"}, hidden = true)
    private String trustStorePassword;

    @Parameter(names = {"-c", "--config"}, hidden = true)
    private String toolkitConfigPath;

    @SuppressWarnings("unused")
    @Parameter(names = {"-f", "--force"}, hidden = true, arity = 0)
    private boolean isForcefully;

    @SuppressWarnings("unused")
    @Parameter(names = {"-k", "--insecure"}, hidden = true, arity = 0)
    private boolean isInsecure;

    @Parameter(names = {"-b", "--security"}, hidden = true)
    private String security;

    private String publisherEndpoint;
    private String adminEndpoint;
    private String registrationEndpoint;
    private String tokenEndpoint;
    private String clientSecret;
    private String clientID;
    private String clientCertEndpoint;

    @Override
    public void execute() {
        //to check the availability of openAPI Definition argument
        boolean openApiAvailable = StringUtils.isNotEmpty(openApi);

        if(!openApiAvailable){
            throw GatewayCmdUtils.createUsageException("Swagger definition is not provided as an argument");
        }

        Swagger swagger = GatewayCmdUtils.parseAPIDef(openApi);
        GatewayCmdUtils.validateSwaggerFile(swagger);

        if (StringUtils.isEmpty(toolkitConfigPath)) {
            toolkitConfigPath = GatewayCmdUtils.getMainConfigLocation();
        }

        init(toolkitConfigPath);

        Config config = GatewayCmdUtils.getConfig();
        boolean isOverwriteRequired = false;

        //to setup the endpointconfig
        if (StringUtils.isEmpty(endpointConfig)) {
            if (StringUtils.isEmpty(endpoint)) {
                /*
                 * if an endpoint config or an endpoint is not provided as an argument, it is prompted from
                 * the user
                 */
                if ((endpoint = GatewayCmdUtils.promptForTextInput(outStream, "Enter Endpoint URL: ")).trim().isEmpty()) {
                    throw GatewayCmdUtils.createUsageException("Micro gateway setup failed: empty endpoint.");
                }
            }
            //todo:  change endpoint type accordingly
            if(endpoint.contains("https")){
                endpointConfig = "{\"production_endpoints\":{\"url\":\"" + endpoint.trim() +
                        "\"},\"endpoint_type\":\"https\"}";
            } else{
                endpointConfig = "{\"production_endpoints\":{\"url\":\"" + endpoint.trim() +
                        "\"},\"endpoint_type\":\"http\"}";
            }
        }

        //to setup the username
        String configuredUser = config.getToken().getUsername();
        if (StringUtils.isEmpty(configuredUser)) {
            if (StringUtils.isEmpty(username)) {
                isOverwriteRequired = true;
                if ((username = GatewayCmdUtils.promptForTextInput(outStream,"Enter Username: ")).trim().isEmpty()) {
                    throw GatewayCmdUtils.createUsageException("Micro gateway setup failed: empty username.");
                }
            }
        } else {
            username = configuredUser;
        }

        //to setup the password
        if (StringUtils.isEmpty(password)) {
            if ((password = GatewayCmdUtils.promptForPasswordInput(outStream, "Enter Password for " + username + ": ")).trim().isEmpty()) {
                if (StringUtils.isEmpty(password)) {
                    password = GatewayCmdUtils.promptForPasswordInput(outStream, "Password can't be empty; enter password for "
                            + username + ": ");
                    if (password.trim().isEmpty()) {
                        throw GatewayCmdUtils.createUsageException("Micro gateway setup failed: empty password.");
                    }
                }
            }
        }

        //to configure endpoints for publisher, admin, registration, and token
        publisherEndpoint = config.getToken().getPublisherEndpoint();
        adminEndpoint = config.getToken().getAdminEndpoint();
        registrationEndpoint = config.getToken().getRegistrationEndpoint();
        tokenEndpoint = config.getToken().getTokenEndpoint();
        if (StringUtils.isEmpty(publisherEndpoint) || StringUtils.isEmpty(registrationEndpoint) ||
                StringUtils.isEmpty(tokenEndpoint)) {
            if (StringUtils.isEmpty(baseUrl)) {
                isOverwriteRequired = true;
                if ((baseUrl = GatewayCmdUtils.promptForTextInput(outStream, "Enter APIM base URL [" +
                        RESTServiceConstants.DEFAULT_HOST + "]: ")).trim().isEmpty()) {
                    baseUrl = RESTServiceConstants.DEFAULT_HOST;
                }
            }
            populateHosts(baseUrl);
        }

        //configure trust store
        String configuredTrustStore = config.getToken().getTrustStoreLocation();
        if (StringUtils.isEmpty(configuredTrustStore)) {
            if (StringUtils.isEmpty(trustStoreLocation)) {
                isOverwriteRequired = true;
                if ((trustStoreLocation = GatewayCmdUtils.promptForTextInput( outStream,
                        "Enter Trust store location: [" + RESTServiceConstants.DEFAULT_TRUSTSTORE_PATH +
                                "]")).trim()
                        .isEmpty()) {
                    trustStoreLocation = RESTServiceConstants.DEFAULT_TRUSTSTORE_PATH;
                }
            }
        } else {
            trustStoreLocation = configuredTrustStore;
        }

        //configure trust store password
        String encryptedPass = config.getToken().getTrustStorePassword();
        String configuredTrustStorePass;
        if (StringUtils.isEmpty(encryptedPass)) {
            configuredTrustStorePass = null;
        } else {
            try {
                configuredTrustStorePass = GatewayCmdUtils.decrypt(encryptedPass, password);
            } catch (CliLauncherException e) {
                //different password used to encrypt
                configuredTrustStorePass = null;
            }
        }

        if (StringUtils.isEmpty(configuredTrustStorePass)) {
            if (StringUtils.isEmpty(trustStorePassword)) {
                isOverwriteRequired = true;
                if ((trustStorePassword = GatewayCmdUtils.promptForPasswordInput(outStream,
                        "Enter Trust store password: " + "[ use default? ]")).trim().isEmpty()) {
                    trustStorePassword = RESTServiceConstants.DEFAULT_TRUSTSTORE_PASS;
                }
            }
        } else {
            trustStorePassword = configuredTrustStorePass;
        }

        File trustStoreFile = new File(trustStoreLocation);
        if (!trustStoreFile.isAbsolute()) {
            trustStoreLocation = GatewayCmdUtils.getUnixPath(GatewayCmdUtils.getCLIHome() + File.separator
                    + trustStoreLocation);
        }
        trustStoreFile = new File(trustStoreLocation);
        if (!trustStoreFile.exists()) {
            logger.error("Provided trust store location {} does not exist.", trustStoreLocation);
            throw new CLIRuntimeException("Provided trust store location does not exist.");
        }

        //set the trustStore
        System.setProperty("javax.net.ssl.keyStoreType", "pkcs12");
        System.setProperty("javax.net.ssl.trustStore", trustStoreLocation);
        System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);

        //Security Schemas settings
        if (security == null) {
            security = "oauth2";
        } else if (security == "") {
            security = "oauth2";
        }
        GatewayCmdUtils.setSecuritySchemas(security);

        //Authentication
        OAuthService manager = new OAuthServiceImpl();
        clientID = config.getToken().getClientId();
        String encryptedSecret = config.getToken().getClientSecret();
        if (!StringUtils.isEmpty(clientID.trim()) && !StringUtils.isEmpty(encryptedSecret.trim())) {
            try {
                clientSecret = GatewayCmdUtils.decrypt(encryptedSecret, password);
            } catch (CliLauncherException e) {
                //different password used to encrypt
                clientSecret = null;
            }
        }

        if (StringUtils.isEmpty(clientID) || StringUtils.isEmpty(clientSecret)) {
            String[] clientInfo = manager
                    .generateClientIdAndSecret(registrationEndpoint, username, password.toCharArray(), isInsecure);
            clientID = clientInfo[0];
            clientSecret = clientInfo[1];
        }

        String accessToken = manager
                .generateAccessTokenAPICreate(tokenEndpoint, username, password.toCharArray(), clientID, clientSecret,
                        isInsecure);

        //to call the publisher API
        RESTAPIService service = new RESTAPIServiceImpl(publisherEndpoint, adminEndpoint, isInsecure);

        //to generate the json for the Publisher API as required
        String jsonPayload = generatePayLoad(OpenApiCodegenUtils.readApi(openApi), swagger, endpointConfig);
        if(service.pushAPIToPublisher(jsonPayload,accessToken)){
            outStream.println("API Registeration is Successfull");
        }

        if (isOverwriteRequired) {
            Config newConfig = new Config();
            Client client = new Client();
            client.setHttpRequestTimeout(1000000);
            newConfig.setClient(client);

            String encryptedCS = GatewayCmdUtils.encrypt(clientSecret, password);
            String encryptedTrustStorePass = GatewayCmdUtils.encrypt(trustStorePassword, password);
            Token token = new TokenBuilder()
                    .setPublisherEndpoint(publisherEndpoint)
                    .setAdminEndpoint(adminEndpoint)
                    .setRegistrationEndpoint(registrationEndpoint)
                    .setTokenEndpoint(tokenEndpoint)
                    .setUsername(username)
                    .setClientId(clientID)
                    .setClientSecret(encryptedCS)
                    .setTrustStoreLocation(trustStoreLocation)
                    .setTrustStorePassword(encryptedTrustStorePass)
                    .build();
            newConfig.setToken(token);
            newConfig.setCorsConfiguration(GatewayCmdUtils.getDefaultCorsConfig());
        }

    }

    //to populate the endpoints if all the endpoints are in the same host
    private void populateHosts(String host) {
        try {
            publisherEndpoint = new URL(new URL(host), RESTServiceConstants.PUB_RESOURCE_PATH).toString();
            clientCertEndpoint = new URL(new URL(host), RESTServiceConstants.PUB_CLIENT_CERT_PATH).toString();
            adminEndpoint = new URL(new URL(host), RESTServiceConstants.ADMIN_RESOURCE_PATH).toString();
            registrationEndpoint = new URL(new URL(host), RESTServiceConstants.DCR_RESOURCE_PATH).toString();
            tokenEndpoint = new URL(new URL(host), RESTServiceConstants.TOKEN_PATH).toString();
        } catch (MalformedURLException e) {
            logger.error("Malformed URL provided {}", host);
            throw new CLIInternalException("Error occurred while setting up URL configurations.");
        }
    }

    //todo: remove taking apiDef as a parameter, solve that using swagger object
    //todo: this is minimum detailed definition to create an API. Remove the hard coded segments
    //to generate the json payload as required for the Publisher API using swagger and endpoint info
    private String generatePayLoad(String apiDef, Swagger swagger, String endpointDef){

        APIDetailedDTO api = new APIDetailedDTO();
        api.setName(swagger.getInfo().getTitle());
        api.setVersion(swagger.getInfo().getVersion());
        api.setContext(formattedContext(swagger.getBasePath(),swagger.getInfo().getVersion()));
        api.setApiDefinition(apiDef);
        api.setEndpointConfig(endpointDef);
        api.setTransport(Arrays.asList("http", "https"));
        api.setIsDefaultVersion(false);
        api.setTiers(Arrays.asList("Unlimited"));
        api.setVisibility(APIDetailedDTO.VisibilityEnum.PUBLIC);

        String json = new Gson().toJson(api);
        return json;
    }

    private static void init(String configPath){
        Path configurationFile = Paths.get(configPath);
        if (Files.exists(configurationFile)) {
            Config config = null;
            try {
                config = TOMLConfigParser.parse(configPath, Config.class);
            } catch (ConfigParserException e) {
                e.printStackTrace();
            }
            GatewayCmdUtils.setConfig(config);
        } else {
            logger.error("Configuration: {} Not found.", configPath);
            throw new CLIInternalException("Error occurred while loading configurations.");
        }
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setParentCmdParser(JCommander parentCmdParser) {

    }

    private String formattedContext(String basePath, String version){
        if(basePath.contains("/"+version)){
            return basePath.substring(0,basePath.indexOf("/"+version));
        }
        return basePath;
    }
}
