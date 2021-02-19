/*
 *  Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 *
 */

package subscription

import (
	"crypto/tls"
	"encoding/json"
	"errors"
	"io/ioutil"
	"net/http"
	"reflect"
	"strconv"

	"github.com/wso2/micro-gw/config"
	logger "github.com/wso2/micro-gw/loggers"
	"github.com/wso2/micro-gw/pkg/auth"
	resourceTypes "github.com/wso2/micro-gw/pkg/resourcetypes"
	"github.com/wso2/micro-gw/pkg/tlsutils"
	"github.com/wso2/micro-gw/pkg/xds"
)

const (
	authorizationBasic         string = "Basic "
	authorizationHeaderDefault string = "Authorization"
	internalWebAppEP           string = "internal/data/v1/"
)

var (
	// SubList contains the Subscription list
	SubList *resourceTypes.SubscriptionList
	// AppList contains the Application list
	AppList *resourceTypes.ApplicationList
	// AppKeyMappingList contains the Application key mapping list
	AppKeyMappingList *resourceTypes.ApplicationKeyMappingList
	// APIList contains the Api list
	APIList *resourceTypes.APIList
	// AppPolicyList contains the Application policy list
	AppPolicyList *resourceTypes.ApplicationPolicyList
	// SubPolicyList contains the Subscription policy list
	SubPolicyList *resourceTypes.SubscriptionPolicyList
	resources     = []resource{
		{
			endpoint:     "subscriptions",
			responseType: SubList,
		},
		{
			endpoint:     "applications",
			responseType: AppList,
		},
		{
			endpoint:     "application-key-mappings",
			responseType: AppKeyMappingList,
		},
		{
			endpoint:     "application-policies",
			responseType: AppPolicyList,
		},
		{
			endpoint:     "subscription-policies",
			responseType: SubPolicyList,
		},
	}
	// APIListChannel is used to add apis
	APIListChannel chan response
	accessToken    string
	conf           *config.Config
)

type response struct {
	Error    error
	Payload  []byte
	Endpoint string
	Type     interface{}
}

type resource struct {
	endpoint     string
	responseType interface{}
}

const (
	gatewayLabelParam    string = "gatewayLabel"
	apisResourceEndpoint string = "apis"
)

func init() {
	APIListChannel = make(chan response)
}

// LoadSubscriptionData loads subscription data from control-plane
func LoadSubscriptionData(configFile *config.Config) {
	conf = configFile
	accessToken = auth.GetBasicAuth(configFile.ControlPlane.EventHub.Username, configFile.ControlPlane.EventHub.Password)

	var responseChannel = make(chan response)
	for _, url := range resources {
		go InvokeService(url.endpoint, url.responseType, nil, responseChannel, 0)
	}

	// Take the configured labels from the adapter
	configuredEnvs := conf.ControlPlane.EventHub.EnvironmentLabels

	if len(configuredEnvs) > 0 {
		for _, configuredEnv := range configuredEnvs {
			queryParamMap := make(map[string]string, 1)
			queryParamMap[gatewayLabelParam] = configuredEnv
			go InvokeService(apisResourceEndpoint, APIList, queryParamMap, APIListChannel, 0)
		}
	}

	var response response
	// for _, env := range configuredEnvs {
	// 	response = <-apiListChannel

	// 	responseType := reflect.TypeOf(response.Type).Elem()
	// 	newResponse := reflect.New(responseType).Interface()

	// 	if response.Error == nil && response.Payload != nil {
	// 		err := json.Unmarshal(response.Payload, &newResponse)

	// 		if err != nil {
	// 			logger.LoggerSubscription.Errorf("Error occurred while unmarshalling the APIList response received for: "+response.Endpoint, err)
	// 		} else {
	// 			switch t := newResponse.(type) {
	// 			case *resourceTypes.APIList:
	// 				logger.LoggerSubscription.Debug("Received API information.")
	// 				APIList = newResponse.(*resourceTypes.APIList)
	// 				xds.UpdateEnforcerAPIList(env, xds.GenerateAPIList(APIList))
	// 			default:
	// 				logger.LoggerSubscription.Debugf("Unknown type %T", t)
	// 			}
	// 		}
	// 	}
	// }
	go retrieveSubscriptionsFromChannel(APIListChannel)

	for i := 1; i <= len(resources); i++ {
		response = <-responseChannel

		responseType := reflect.TypeOf(response.Type).Elem()
		newResponse := reflect.New(responseType).Interface()

		if response.Error == nil && response.Payload != nil {
			err := json.Unmarshal(response.Payload, &newResponse)

			if err != nil {
				logger.LoggerSubscription.Errorf("Error occurred while unmarshalling the response received for: "+response.Endpoint, err)
			} else {
				switch t := newResponse.(type) {
				case *resourceTypes.SubscriptionList:
					logger.LoggerSubscription.Debug("Received Subscription information.")
					SubList = newResponse.(*resourceTypes.SubscriptionList)
					xds.UpdateEnforcerSubscriptions(xds.GenerateSubscriptionList(SubList))
				case *resourceTypes.ApplicationList:
					logger.LoggerSubscription.Debug("Received Application information.")
					AppList = newResponse.(*resourceTypes.ApplicationList)
					xds.UpdateEnforcerApplications(xds.GenerateApplicationList(AppList))
				case *resourceTypes.ApplicationPolicyList:
					logger.LoggerSubscription.Debug("Received Application Policy information.")
					AppPolicyList = newResponse.(*resourceTypes.ApplicationPolicyList)
					xds.UpdateEnforcerApplicationPolicies(xds.GenerateApplicationPolicyList(AppPolicyList))
				case *resourceTypes.SubscriptionPolicyList:
					logger.LoggerSubscription.Debug("Received Subscription Policy information.")
					SubPolicyList = newResponse.(*resourceTypes.SubscriptionPolicyList)
					xds.UpdateEnforcerSubscriptionPolicies(xds.GenerateSubscriptionPolicyList(SubPolicyList))
				case *resourceTypes.ApplicationKeyMappingList:
					logger.LoggerSubscription.Debug("Received Application Key Mapping information.")
					AppKeyMappingList = newResponse.(*resourceTypes.ApplicationKeyMappingList)
					xds.UpdateEnforcerApplicationKeyMappings(xds.GenerateApplicationKeyMappingList(AppKeyMappingList))
				default:
					logger.LoggerSubscription.Debugf("Unknown type %T", t)
				}
			}
		}
	}
}

// InvokeService invokes the internal data resource
func InvokeService(endpoint string, responseType interface{}, queryParamMap map[string]string, c chan response, retryAttempt int) {

	serviceURL := conf.ControlPlane.EventHub.ServiceURL + internalWebAppEP + endpoint
	// Create the request
	req, err := http.NewRequest("GET", serviceURL, nil)

	if queryParamMap != nil && len(queryParamMap) > 0 {
		q := req.URL.Query()
		// Making necessary query parameters for the request
		for queryParamKey, queryParamValue := range queryParamMap {
			q.Add(queryParamKey, queryParamValue)
		}
		req.URL.RawQuery = q.Encode()
	}
	if err != nil {
		c <- response{err, nil, endpoint, responseType}
		logger.LoggerSubscription.Errorf("Error occurred while creating an HTTP request for serviceURL: "+serviceURL, err)
		return
	}

	// Check if TLS is enabled
	skipSSL := conf.ControlPlane.EventHub.SkipSSLVerfication
	logger.LoggerSubscription.Debugf("Skip SSL Verification: %v", skipSSL)
	tr := &http.Transport{}
	if !skipSSL {
		caCertPool := tlsutils.GetTrustedCertPool()
		tr = &http.Transport{
			TLSClientConfig: &tls.Config{RootCAs: caCertPool},
		}
	} else {
		tr = &http.Transport{
			TLSClientConfig: &tls.Config{InsecureSkipVerify: true},
		}
	}

	// Configuring the http client
	client := &http.Client{
		Transport: tr,
	}

	// Setting authorization header
	req.Header.Set(authorizationHeaderDefault, authorizationBasic+accessToken)

	// Make the request
	logger.LoggerSubscription.Debug("Sending the request to the control plane over the REST API: " + serviceURL)
	resp, err := client.Do(req)

	if err != nil {
		c <- response{err, nil, endpoint, responseType}
		logger.LoggerSubscription.Errorf("Error occurred while calling the REST API: "+serviceURL, err)
		return
	}

	responseBytes, err := ioutil.ReadAll(resp.Body)
	if resp.StatusCode == http.StatusOK {
		if err != nil {
			c <- response{err, nil, endpoint, responseType}
			logger.LoggerSubscription.Errorf("Error occurred while reading the response received for: "+serviceURL, err)
			return
		}
		c <- response{nil, responseBytes, endpoint, responseType}

	} else {
		c <- response{errors.New(string(responseBytes)), nil, endpoint, responseType}
		logger.LoggerSubscription.Errorf("Failed to fetch data! "+serviceURL+" responded with "+strconv.Itoa(resp.StatusCode), err)
	}
}

func retrieveSubscriptionsFromChannel(c chan response) {
	for response := range c {
		responseType := reflect.TypeOf(response.Type).Elem()
		newResponse := reflect.New(responseType).Interface()

		if response.Error == nil && response.Payload != nil {
			err := json.Unmarshal(response.Payload, &newResponse)

			if err != nil {
				logger.LoggerSubscription.Errorf("Error occurred while unmarshalling the APIList response received for: "+response.Endpoint, err)
			} else {
				switch t := newResponse.(type) {
				case *resourceTypes.APIList:
					logger.LoggerSubscription.Debug("Received API information.")
					APIList = newResponse.(*resourceTypes.APIList)
					xds.UpdateEnforcerAPIList("Production and Sandbox", xds.GenerateAPIList(APIList))
				default:
					logger.LoggerSubscription.Debugf("Unknown type %T", t)
				}
			}
		}
	}
}
