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

// Package messaging holds the implementation for event listeners functions
package messaging

import (
	"encoding/base64"
	"encoding/json"
	"strings"

	"github.com/streadway/amqp"
	"github.com/wso2/micro-gw/config"
	logger "github.com/wso2/micro-gw/loggers"
	resourceTypes "github.com/wso2/micro-gw/pkg/resourcetypes"
	"github.com/wso2/micro-gw/pkg/subscription"
	"github.com/wso2/micro-gw/pkg/synchronizer"
	"github.com/wso2/micro-gw/pkg/xds"
)

// constant variables
const (
	apiEventType                = "API"
	applicationEventType        = "APPLICATION"
	subscriptionEventType       = "SUBSCRIPTIONS"
	scopeEvenType               = "SCOPE"
	removeAPIFromGateway        = "REMOVE_API_FROM_GATEWAY"
	deployAPIToGateway          = "DEPLOY_API_IN_GATEWAY"
	applicationRegistration     = "APPLICATION_REGISTRATION_CREATE"
	removeApplicationKeyMapping = "REMOVE_APPLICATION_KEYMAPPING"
	apiLifeCycleChange          = "API_LIFECYCLE_CHANGE"
)

// var variables
var (
	APIList                   = make([]resourceTypes.API, 0)
	ScopeList                 = make([]resourceTypes.Scope, 0)
	APIListTimeStamp          = make(map[string]int64, 0)
	SubsriptionsListTimeStamp = make(map[int]int64, 0)
	ApplicationListTimeStamp  = make(map[string]int64, 0)
)

// handleNotification to process
func handleNotification(deliveries <-chan amqp.Delivery, done chan error) {
	for d := range deliveries {
		var notification EventNotification
		var eventType string
		json.Unmarshal([]byte(string(d.Body)), &notification)
		var decodedByte, err = base64.StdEncoding.DecodeString(notification.Event.PayloadData.Event)
		if err != nil {
			if _, ok := err.(base64.CorruptInputError); ok {
				panic("\nbase64 input is corrupt, check the provided key")
			}
			panic(err)
		}
		logger.LoggerMsg.Debugf("\n\n[%s]", decodedByte)
		eventType = notification.Event.PayloadData.EventType

		if strings.Contains(eventType, apiEventType) {
			handleAPIEvents(decodedByte, eventType)
		} else if strings.Contains(eventType, applicationEventType) {
			handleApplicationEvents(decodedByte, eventType)
		} else if strings.Contains(eventType, subscriptionEventType) {
			handleSubscriptionEvents(decodedByte, eventType)
		} else if strings.Contains(eventType, scopeEvenType) {
			handleScopeEvents(decodedByte, eventType)
		} else {
			handlePolicyEvents(decodedByte, eventType)
		}
		d.Ack(false)
	}
	logger.LoggerMsg.Infof("handle: deliveries channel closed")
	done <- nil
}

// handleAPIEvents to process api related data
func handleAPIEvents(data []byte, eventType string) {
	var (
		apiEvent         APIEvent
		currentTimeStamp int64 = apiEvent.Event.TimeStamp
	)

	json.Unmarshal([]byte(string(data)), &apiEvent)
	// Per each revision, synchronization should happen.
	if strings.EqualFold(deployAPIToGateway, apiEvent.Event.Type) {
		go synchronizer.FetchAPIsFromControlPlane(apiEvent.UUID, apiEvent.GatewayLabels)
	}

	// TODO: (VirajSalaka) Handle API Blocked event
	if len(apiEvent.GatewayLabels) > 0 {
		for _, env := range apiEvent.GatewayLabels {
			// TODO: (VirajSalaka) This stores unnecessary keyvalue pairs as well.
			if timeStamp, ok := APIListTimeStamp[apiEvent.UUID+":"+env]; ok {
				APIListTimeStamp[apiEvent.UUID+":"+env] = currentTimeStamp
				if timeStamp > currentTimeStamp {
					return
				}
			}
			if strings.EqualFold(deployAPIToGateway, apiEvent.Event.Type) {
				conf, _ := config.ReadConfigs()
				for _, configuredEnv := range conf.ControlPlane.EventHub.EnvironmentLabels {
					if configuredEnv == env {
						if _, ok := subscription.APIList[env]; ok {
							apiListOfEnv := subscription.APIList[env].List
							for i := range apiListOfEnv {
								// If API is already found, it is a new revision deployement.
								// Subscription relates details of an API does not change between new revisions
								if apiEvent.APIID == apiListOfEnv[i].APIID {
									logger.LoggerMsg.Debugf("APIList for apiIId: %s is not updated as it already exists", apiEvent.UUID)
									return
								}
							}
						}
						queryParamMap := make(map[string]string, 3)
						queryParamMap[subscription.GatewayLabelParam] = configuredEnv
						queryParamMap[subscription.ContextParam] = apiEvent.Context
						queryParamMap[subscription.VersionParam] = apiEvent.Version
						// TODO: (VirajSalaka) Fix the REST API call once the APIM Event hub implementation is fixed.
						go subscription.InvokeService(subscription.ApisEndpoint, subscription.APIList, queryParamMap,
							subscription.APIListChannel, 0)
					}
				}
			} else {
				if _, ok := subscription.APIList[env]; ok {
					apiListOfEnv := subscription.APIList[env].List
					for i := range apiListOfEnv {
						if apiEvent.APIID == apiListOfEnv[i].APIID {
							subscription.APIList[env].List = deleteAPIFromList(apiListOfEnv, i, apiEvent.APIID)
							break
						}
					}
				}
			}
		}
	}
	// if isFound && oldTimeStamp < newTimeStamp && strings.EqualFold(removeAPIFromGateway, apiEvent.Event.Type) {
	// 	deleteAPIFromList(indexOfAPI, apiEvent.APIID)
	// } else if strings.EqualFold(deployAPIToGateway, apiEvent.Event.Type) {
	// 	conf, _ := config.ReadConfigs()
	// 	for _, env := range apiEvent.GatewayLabels {
	// 		for _, configuredEnv := range conf.ControlPlane.EventHub.EnvironmentLabels {
	// 			if configuredEnv == env {
	// 				queryParamMap := make(map[string]string, 3)
	// 				queryParamMap[subscription.GatewayLabelParam] = configuredEnv
	// 				queryParamMap[subscription.ContextParam] = apiEvent.Context
	// 				queryParamMap[subscription.VersionParam] = apiEvent.Version
	// 				// TODO: (VirajSalaka) Fix the REST API call once the APIM Event hub implementation is fixed.
	// 				go subscription.InvokeService(subscription.ApisEndpoint, subscription.APIList, queryParamMap,
	// 					subscription.APIListChannel, 0)
	// 			}
	// 		}
	// 	}
	// 	go synchronizer.FetchAPIsFromControlPlane(apiEvent.UUID, apiEvent.GatewayLabels)
	// }
}

// deleteAPIFromList when remove API From Gateway event happens
func deleteAPIFromList(apiList []resourceTypes.API, indexToBeDeleted int, apiID int) []resourceTypes.API {
	// copy(apiList[indexToBeDeleted:], apiList[indexToBeDeleted+1:])
	// apiList[len(apiList)-1] = resourceTypes.API{}
	apiList[indexToBeDeleted] = apiList[len(apiList)-1]

	//apiList = apiList[:len(apiList)-1]
	logger.LoggerMsg.Infof("API %d is deleted from APIList", apiID)
	return apiList
}

// handleApplicationEvents to process application related events
func handleApplicationEvents(data []byte, eventType string) {
	if strings.EqualFold(applicationRegistration, eventType) ||
		strings.EqualFold(removeApplicationKeyMapping, eventType) {
		var applicationRegistrationEvent ApplicationRegistrationEvent
		json.Unmarshal([]byte(string(data)), &applicationRegistrationEvent)

		applicationKeyMapping := resourceTypes.ApplicationKeyMapping{ApplicationID: applicationRegistrationEvent.ApplicationID,
			ConsumerKey: applicationRegistrationEvent.ConsumerKey, KeyType: applicationRegistrationEvent.KeyType,
			KeyManager: applicationRegistrationEvent.KeyManager, TenantID: -1, TenantDomain: applicationRegistrationEvent.TenantDomain,
			TimeStamp: applicationRegistrationEvent.TimeStamp}

		subscription.AppKeyMappingList.List = append(subscription.AppKeyMappingList.List, applicationKeyMapping)
		xds.UpdateEnforcerApplicationKeyMappings(xds.GenerateApplicationKeyMappingList(subscription.AppKeyMappingList))
	} else {
		var applicationEvent ApplicationEvent
		json.Unmarshal([]byte(string(data)), &applicationEvent)
		application := resourceTypes.Application{UUID: applicationEvent.UUID, ID: applicationEvent.ApplicationID,
			Name: applicationEvent.ApplicationName, SubName: applicationEvent.Subscriber, Policy: applicationEvent.ApplicationPolicy, TokenType: applicationEvent.TokenType, GroupIds: applicationEvent.GroupID, Attributes: nil,
			TenantID: -1, TenantDomain: applicationEvent.TenantDomain, TimeStamp: applicationEvent.TimeStamp}

		if applicationEvent.Event.Type == "APPLICATION_CREATE" {
			subscription.AppList.List = append(subscription.AppList.List, application)
		} else if applicationEvent.Event.Type == "APPLICATION_UPDATE" {
			subscription.AppList.List = removeApplication(subscription.AppList.List, applicationEvent.ApplicationID)
			subscription.AppList.List = append(subscription.AppList.List, application)
		} else if applicationEvent.Event.Type == "APPLICATION_DELETE" {
			subscription.AppList.List = removeApplication(subscription.AppList.List, applicationEvent.ApplicationID)
		}
		xds.UpdateEnforcerApplications(xds.GenerateApplicationList(subscription.AppList))
	}
}

// handleSubscriptionRelatedEvents to process subscription related events
func handleSubscriptionEvents(data []byte, eventType string) {
	var subscriptionEvent SubscriptionEvent
	json.Unmarshal([]byte(string(data)), &subscriptionEvent)
	sub := resourceTypes.Subscription{SubscriptionID: subscriptionEvent.SubscriptionID, PolicyID: subscriptionEvent.PolicyID,
		APIID: subscriptionEvent.APIID, AppID: subscriptionEvent.ApplicationID, SubscriptionState: subscriptionEvent.SubscriptionState,
		TenantID: subscriptionEvent.TenantID, TenantDomain: subscriptionEvent.TenantDomain, TimeStamp: subscriptionEvent.TimeStamp}
	if subscriptionEvent.Event.Type == "SUBSCRIPTIONS_CREATE" {
		subscription.SubList.List = append(subscription.SubList.List, sub)
	} else if subscriptionEvent.Event.Type == "SUBSCRIPTIONS_UPDATE" {
		subscription.SubList.List = removeSubscription(subscription.SubList.List, subscriptionEvent.SubscriptionID)
		subscription.SubList.List = append(subscription.SubList.List, sub)
	} else if subscriptionEvent.Event.Type == "SUBSCRIPTIONS_DELETE" {
		subscription.SubList.List = removeSubscription(subscription.SubList.List, subscriptionEvent.SubscriptionID)
	}
	xds.UpdateEnforcerSubscriptions(xds.GenerateSubscriptionList(subscription.SubList))
	// EventTypes: SUBSCRIPTIONS_CREATE, SUBSCRIPTIONS_UPDATE, SUBSCRIPTIONS_DELETE
}

// handleScopeRelatedEvents to process scope related events
func handleScopeEvents(data []byte, eventType string) {
	var scopeEvent ScopeEvent
	json.Unmarshal([]byte(string(data)), &scopeEvent)
	scope := resourceTypes.Scope{Name: scopeEvent.Name, DisplayName: scopeEvent.DisplayName, ApplicationName: scopeEvent.ApplicationName}
	ScopeList = append(ScopeList, scope)
	// EventTypes: SCOPE_CREATE, SCOPE_UPDATE,SCOPE_DELETE
}

// handlePolicyRelatedEvents to process policy related events
func handlePolicyEvents(data []byte, eventType string) {
	var policyEvent PolicyInfo
	json.Unmarshal([]byte(string(data)), &policyEvent)

	// TODO: Handle policy events
	if strings.EqualFold(eventType, "POLICY_CREATE") {
		logger.LoggerMsg.Infof("Policy: %s for policy type: %s", policyEvent.PolicyName, policyEvent.PolicyType)
	} else if strings.EqualFold(eventType, "POLICY_UPDATE") {
		logger.LoggerMsg.Infof("Policy: %s for policy type: %s", policyEvent.PolicyName, policyEvent.PolicyType)
	} else if strings.EqualFold(eventType, "POLICY_DELETE") {
		logger.LoggerMsg.Infof("Policy: %s for policy type: %s", policyEvent.PolicyName, policyEvent.PolicyType)
	}

	if strings.EqualFold(apiEventType, policyEvent.PolicyType) {
		var apiPolicyEvent APIPolicyEvent
		json.Unmarshal([]byte(string(data)), &apiPolicyEvent)
	} else if strings.EqualFold(applicationEventType, policyEvent.PolicyType) {
		applicationPolicy := resourceTypes.ApplicationPolicy{ID: policyEvent.PolicyID, TenantID: -1, Name: policyEvent.PolicyName,
			QuotaType: policyEvent.QuotaType}

		if policyEvent.Event.Type == "POLICY_CREATE" {
			subscription.AppPolicyList.List = append(subscription.AppPolicyList.List, applicationPolicy)
		} else if policyEvent.Event.Type == "POLICY_UPDATE" {
			subscription.AppPolicyList.List = removeAppPolicy(subscription.AppPolicyList.List, policyEvent.PolicyID)
			subscription.AppPolicyList.List = append(subscription.AppPolicyList.List, applicationPolicy)
		} else if policyEvent.Event.Type == "POLICY_DELETE" {
			subscription.AppPolicyList.List = removeAppPolicy(subscription.AppPolicyList.List, policyEvent.PolicyID)
		}
		xds.UpdateEnforcerApplicationPolicies(xds.GenerateApplicationPolicyList(subscription.AppPolicyList))

	} else if strings.EqualFold(subscriptionEventType, policyEvent.PolicyType) {
		var subscriptionPolicyEvent SubscriptionPolicyEvent
		json.Unmarshal([]byte(string(data)), &subscriptionPolicyEvent)

		subscriptionPolicy := resourceTypes.SubscriptionPolicy{ID: subscriptionPolicyEvent.PolicyID, TenantID: -1,
			Name: subscriptionPolicyEvent.PolicyName, QuotaType: subscriptionPolicyEvent.QuotaType,
			GraphQLMaxComplexity: subscriptionPolicyEvent.GraphQLMaxComplexity,
			GraphQLMaxDepth:      subscriptionPolicyEvent.GraphQLMaxDepth, RateLimitCount: subscriptionPolicyEvent.RateLimitCount,
			RateLimitTimeUnit: subscriptionPolicyEvent.RateLimitTimeUnit, StopOnQuotaReach: subscriptionPolicyEvent.StopOnQuotaReach,
			TenantDomain: subscriptionPolicyEvent.TenantDomain, TimeStamp: subscriptionPolicyEvent.TimeStamp}

		if subscriptionPolicyEvent.Event.Type == "POLICY_CREATE" {
			subscription.SubPolicyList.List = append(subscription.SubPolicyList.List, subscriptionPolicy)
		} else if subscriptionPolicyEvent.Event.Type == "POLICY_UPDATE" {
			subscription.SubPolicyList.List = removeSubPolicy(subscription.SubPolicyList.List, subscriptionPolicyEvent.PolicyID)
			subscription.SubPolicyList.List = append(subscription.SubPolicyList.List, subscriptionPolicy)
		} else if subscriptionPolicyEvent.Event.Type == "POLICY_DELETE" {
			subscription.SubPolicyList.List = removeSubPolicy(subscription.SubPolicyList.List, subscriptionPolicyEvent.PolicyID)
		}
		xds.UpdateEnforcerSubscriptionPolicies(xds.GenerateSubscriptionPolicyList(subscription.SubPolicyList))
	}
}

func removeApplication(applications []resourceTypes.Application, id int32) []resourceTypes.Application {
	index := 0
	for _, i := range applications {
		if i.ID != id {
			applications[index] = i
			index++
		}
	}
	return applications[:index]
}

func removeSubscription(subscriptions []resourceTypes.Subscription, id int32) []resourceTypes.Subscription {
	index := 0
	for _, i := range subscriptions {
		if i.SubscriptionID != id {
			subscriptions[index] = i
			index++
		}
	}
	return subscriptions[:index]
}

func removeAppPolicy(appPolicies []resourceTypes.ApplicationPolicy, id int32) []resourceTypes.ApplicationPolicy {
	index := 0
	for _, i := range appPolicies {
		if i.ID != id {
			appPolicies[index] = i
			index++
		}
	}
	return appPolicies[:index]
}

func removeSubPolicy(subPolicies []resourceTypes.SubscriptionPolicy, id int32) []resourceTypes.SubscriptionPolicy {
	index := 0
	for _, i := range subPolicies {
		if i.ID != id {
			subPolicies[index] = i
			index++
		}
	}
	return subPolicies[:index]
}

// func removeAPI(apis []resourceTypes.API, id int) []resourceTypes.API {
// 	index := 0
// 	for _, i := range apis {
// 		if i.APIID != id {
// 			apis[index] = i
// 			index++
// 		}
// 	}
// 	return apis[:index]
// }
