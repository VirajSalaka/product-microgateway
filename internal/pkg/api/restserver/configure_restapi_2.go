// This file is safe to edit. Once it exists it will not be overwritten

// Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package restserver

import (
	"crypto/tls"
	"io/ioutil"
	"log"
	"net/http"
	"os"

	"github.com/go-openapi/errors"
	"github.com/go-openapi/loads"
	"github.com/go-openapi/runtime"
	"github.com/go-openapi/runtime/middleware"
	flags "github.com/jessevdk/go-flags"

	keystore "github.com/pavel-v-chernykh/keystore-go/v3"
	"github.com/wso2/micro-gw/configs/confTypes"
	logger "github.com/wso2/micro-gw/internal/loggers"
	apiServer "github.com/wso2/micro-gw/internal/pkg/api"
	"github.com/wso2/micro-gw/internal/pkg/api/models"
	"github.com/wso2/micro-gw/internal/pkg/api/restserver/operations"
	"github.com/wso2/micro-gw/internal/pkg/api/restserver/operations/api_individual"
)

var (
	mgwConfig *confTypes.Config
)

//go:generate swagger generate server --target ../../api --name Restapi --spec ../../../../resources/adminAPI.yaml --server-package restserver --principal models.Principal

func configureFlags(api *operations.RestapiAPI) {
	// api.CommandLineOptionsGroups = []swag.CommandLineOptionsGroup{ ... }
}

func configureAPI(api *operations.RestapiAPI) http.Handler {
	// configure the api here
	api.ServeError = errors.ServeError

	// Set your custom logger if needed. Default one is log.Printf
	// Expected interface func(string, ...interface{})
	//
	// Example:
	// api.Logger = log.Printf

	// api.UseSwaggerUI()
	// To continue using redoc as your UI, uncomment the following line
	// api.UseRedoc()

	api.JSONConsumer = runtime.JSONConsumer()
	api.MultipartformConsumer = runtime.DiscardConsumer

	api.JSONProducer = runtime.JSONProducer()

	// Applies when the Authorization header is set with the Basic scheme
	api.BasicAuthAuth = func(user string, pass string) (*models.Principal, error) {
		logger.LoggerMgw.Infof("token %v", user)
		//TODO: implement authentication
		p := models.Principal{
			Token:    "xxxx",
			Tenant:   "xxxx",
			Username: "xxx",
		}
		return &p, nil
	}

	// Set your custom authorizer if needed. Default one is security.Authorized()
	// Expected interface runtime.Authorizer
	//
	// Example:
	// api.APIAuthorizer = security.Authorized()
	api.APIIndividualPostImportAPIHandler = api_individual.PostImportAPIHandlerFunc(func(params api_individual.PostImportAPIParams, principal *models.Principal) middleware.Responder {

		jsonByteArray, _ := ioutil.ReadAll(params.File)
		apiServer.UnzipAndApplyZippedProject(jsonByteArray)
		return api_individual.NewPostImportAPIOK()
	})

	api.PreServerShutdown = func() {}

	api.ServerShutdown = func() {}

	return setupGlobalMiddleware(api.Serve(setupMiddlewares))
}

// The TLS configuration before HTTPS server starts.
func configureTLS(tlsConfig *tls.Config) {
	// Make all necessary changes to the TLS configuration here.
	//TODO: (VirajSalaka)
	// certArray, _ =
	tlsConfig.Certificates, _ = getCertificates(mgwConfig.Server.PublicKeyPath, mgwConfig.Server.PrivateKeyPath)
	// tlsConfig.Certificates = getCertificatesFromByteArr(getPrivateKeyFile(), getPublicKeyFile())
}

func getCertificates(publicKeyPath, privateKeyPath string) ([]tls.Certificate, error) {
	certificates := make([]tls.Certificate, 1)
	//TODO: (VirajSalaka) make these paramters configurable
	tlsCertificate := publicKeyPath
	tlsCertificateKey := privateKeyPath
	certificate, err := tls.LoadX509KeyPair(string(tlsCertificate), string(tlsCertificateKey))
	if err != nil {
		logger.LoggerMgw.Fatal("Error while loading the tls keypair.", err)
		return certificates, err
	}
	certificates[0] = certificate

	return certificates, nil
}

func getCertificatesFromByteArr(keyPem, certPem []byte) []tls.Certificate {
	certificates := make([]tls.Certificate, 1)
	cert, err := tls.X509KeyPair(certPem, keyPem)
	if err != nil {
		log.Fatal(err)
	} else {
		certificates[0] = cert
	}
	return certificates
}

// As soon as server is initialized but not run yet, this function will be called.
// If you need to modify a config, store server instance to stop it individually later, this is the place.
// This function can be called multiple times, depending on the number of serving schemes.
// scheme value will be set accordingly: "http", "https" or "unix"
func configureServer(s *http.Server, scheme, addr string) {
}

// The middleware configuration is for the handler executors. These do not apply to the swagger.json document.
// The middleware executes after routing but before authentication, binding and validation
func setupMiddlewares(handler http.Handler) http.Handler {
	return handler
}

// The middleware configuration happens before anything, this middleware also applies to serving the swagger.json document.
// So this is a good place to plug in a panic handling middleware, logging and metrics
func setupGlobalMiddleware(handler http.Handler) http.Handler {
	return handler
}

func StartRestServer(config *confTypes.Config) {
	mgwConfig = config
	swaggerSpec, err := loads.Embedded(SwaggerJSON, FlatSwaggerJSON)
	if err != nil {
		log.Fatalln(err)
	}

	api := operations.NewRestapiAPI(swaggerSpec)
	server := NewServer(api)
	defer server.Shutdown()

	parser := flags.NewParser(server, flags.Default)
	parser.ShortDescription = "WSO2 API Manager - Admin"
	parser.LongDescription = "This document specifies a **RESTful API** for WSO2 **API Manager** - Admin Portal.\nPlease see [full swagger definition](https://raw.githubusercontent.com/wso2/carbon-apimgt/v6.5.176/components/apimgt/org.wso2.carbon.apimgt.rest.api.admin/src/main/resources/admin-api.yaml) of the API which is written using [swagger 2.0](http://swagger.io/) specification.\n"
	server.ConfigureFlags()
	for _, optsGroup := range api.CommandLineOptionsGroups {
		_, err := parser.AddGroup(optsGroup.ShortDescription, optsGroup.LongDescription, optsGroup.Options)
		if err != nil {
			log.Fatalln(err)
		}
	}

	if _, err := parser.Parse(); err != nil {
		code := 1
		if fe, ok := err.(*flags.Error); ok {
			if fe.Type == flags.ErrHelp {
				code = 0
			}
		}
		os.Exit(code)
	}

	server.ConfigureAPI()
	server.TLSPort = 9099

	if err := server.Serve(); err != nil {
		log.Fatalln(err)
	}

}

func getPrivateKeyFile() []byte {
	var privateKeyByteArr []byte
	f, err := os.Open("/Users/viraj/Desktop/temp/wso2am-micro-gw-macos-3.2.0-alpha/runtime/bre/security/ballerinaKeystore.p12")
	if err != nil {
		log.Fatal(err)
	}
	defer f.Close()
	keyStore, err := keystore.Decode(f, []byte("ballerina"))
	if err != nil {
		log.Fatal(err)
	}
	key, ok := keyStore["ballerina"]
	if ok {
		privateKey := key.(*keystore.PrivateKeyEntry)
		privateKeyByteArr = privateKey.PrivateKey
		logger.LoggerMgw.Infof("private key found \n%v", string(privateKeyByteArr))
	}
	return privateKeyByteArr
}

func getPublicKeyFile() []byte {
	var publicKeyByteArr []byte
	f, err := os.Open("/Users/viraj/Desktop/temp/wso2am-micro-gw-macos-3.2.0-alpha/runtime/bre/security/ballerinaTruststore.p12")
	if err != nil {
		log.Fatal(err)
	}
	defer f.Close()
	keyStore, err := keystore.Decode(f, []byte("ballerina"))
	if err != nil {
		log.Fatal(err)
	}
	key, ok := keyStore["ballerina"]
	if ok {
		certEntry := key.(*keystore.TrustedCertificateEntry)
		publicKeyByteArr = certEntry.Certificate.Content
		logger.LoggerMgw.Infof("public key found \n%v", string(publicKeyByteArr))

	}
	return publicKeyByteArr
}
