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

package org.wso2.micro.gateway.enforcer.analytics;

import org.wso2.carbon.apimgt.common.gateway.analytics.collectors.AnalyticsDataProvider;
import org.wso2.carbon.apimgt.common.gateway.analytics.publishers.dto.API;
import org.wso2.carbon.apimgt.common.gateway.analytics.publishers.dto.Application;
import org.wso2.carbon.apimgt.common.gateway.analytics.publishers.dto.Error;
import org.wso2.carbon.apimgt.common.gateway.analytics.publishers.dto.Latencies;
import org.wso2.carbon.apimgt.common.gateway.analytics.publishers.dto.MetaInfo;
import org.wso2.carbon.apimgt.common.gateway.analytics.publishers.dto.Operation;
import org.wso2.carbon.apimgt.common.gateway.analytics.publishers.dto.Target;
import org.wso2.carbon.apimgt.common.gateway.analytics.publishers.dto.enums.EventCategory;
import org.wso2.carbon.apimgt.common.gateway.analytics.publishers.dto.enums.FaultCategory;
import org.wso2.carbon.apimgt.common.gateway.analytics.publishers.dto.enums.FaultSubCategory;
import org.wso2.micro.gateway.enforcer.api.RequestContext;
import org.wso2.micro.gateway.enforcer.constants.APIConstants;
import org.wso2.micro.gateway.enforcer.constants.AnalyticsConstants;
import org.wso2.micro.gateway.enforcer.security.AuthenticationContext;

/**
 * Generate FaultDTO for the errors generated from enforcer.
 */
public class MgwFaultAnalyticsProvider implements AnalyticsDataProvider {
    private final RequestContext requestContext;

    public MgwFaultAnalyticsProvider(RequestContext requestContext) {
        this.requestContext = requestContext;
    }

    @Override
    public EventCategory getEventCategory() {
        return EventCategory.FAULT;
    }

    @Override
    public boolean isAnonymous() {
        // TODO: (VirajSalaka) fix
        return false;
    }

    @Override
    public boolean isAuthenticated() {
        AuthenticationContext authenticationContext = requestContext.getAuthenticationContext();
        return authenticationContext != null && authenticationContext.isAuthenticated();
    }

    @Override
    public FaultCategory getFaultType() {
        if (requestContext.getProperties().containsKey(APIConstants.MessageFormat.STATUS_CODE)) {
            int statusCode = Integer.parseInt(requestContext.getProperties()
                    .get(APIConstants.MessageFormat.STATUS_CODE).toString());
            switch (statusCode) {
                case 401:
                case 403:
                    return FaultCategory.AUTH;
                case 429:
                    return FaultCategory.THROTTLED;
                default:
                    return FaultCategory.OTHER;
            }
        }
        return FaultCategory.OTHER;
    }

    @Override
    public API getApi() {
        API api = new API();
        api.setApiId(AnalyticsUtils.getAPIId(requestContext));
        api.setApiCreator(AnalyticsUtils.setDefaultIfNull(
                requestContext.getAuthenticationContext() == null
                        ? null : requestContext.getAuthenticationContext().getApiPublisher()));
        api.setApiType(requestContext.getMathedAPI().getAPIConfig().getApiType());
        api.setApiName(requestContext.getMathedAPI().getAPIConfig().getName());
        api.setApiVersion(requestContext.getMathedAPI().getAPIConfig().getVersion());
        // TODO: (VirajSalaka) pick tenant from subscription detail straightaway.
        api.setApiCreatorTenantDomain("carbon.super");
        return api;
    }

    @Override
    public Application getApplication() {
        AuthenticationContext authContext = AnalyticsUtils.getAuthenticationContext(requestContext);
        Application application = new Application();
        // Default Value would be PRODUCTION
        application.setKeyType(
                authContext.getKeyType() == null ? APIConstants.API_KEY_TYPE_PRODUCTION : authContext.getKeyType());
        application.setApplicationId(AnalyticsUtils.setDefaultIfNull(authContext.getApplicationId()));
        application.setApplicationOwner(AnalyticsUtils.setDefaultIfNull(authContext.getSubscriber()));
        application.setApplicationName(AnalyticsUtils.setDefaultIfNull(authContext.getApplicationName()));
        return application;
    }

    @Override
    public Operation getOperation() {
        // This could be null if  OPTIONS request comes
        if (requestContext.getMatchedResourcePath() != null) {
            Operation operation = new Operation();
            operation.setApiMethod(requestContext.getMatchedResourcePath().getMethod().name());
            operation.setApiResourceTemplate(requestContext.getMatchedResourcePath().getPath());
            return operation;
        }
        return null;
    }

    @Override
    public Target getTarget() {
        Target target = new Target();
        target.setResponseCacheHit(false);
        target.setTargetResponseCode(Integer.parseInt(
                requestContext.getProperties().get(APIConstants.MessageFormat.STATUS_CODE).toString()));
        // Destination is not included in the fault event scenario
        return target;
    }

    @Override
    public Latencies getLatencies() {
        // Latencies information are not required.
        return null;
    }

    @Override
    public MetaInfo getMetaInfo() {
        MetaInfo metaInfo = new MetaInfo();
        metaInfo.setRegionId("UnAssigned");
        metaInfo.setGatewayType(AnalyticsConstants.GATEWAY_LABEL);
        metaInfo.setCorrelationId(requestContext.getRequestID());
        return metaInfo;
    }

    @Override
    public int getProxyResponseCode() {
        return Integer.parseInt(requestContext.getProperties()
                .get(APIConstants.MessageFormat.STATUS_CODE).toString());
    }

    @Override
    public int getTargetResponseCode() {
        return Integer.parseInt(requestContext.getProperties()
                .get(APIConstants.MessageFormat.STATUS_CODE).toString());
    }

    @Override
    public long getRequestTime() {
        return requestContext.getRequestTimeStamp();
    }

    @Override
    public Error getError(FaultCategory faultCategory) {
        // All the messages should have the error_code
        if (requestContext.getProperties().containsKey(APIConstants.MessageFormat.ERROR_CODE)) {
            FaultCodeClassifier faultCodeClassifier =
                    new FaultCodeClassifier(Integer.parseInt(requestContext.getProperties()
                            .get(APIConstants.MessageFormat.ERROR_CODE).toString()));
            FaultSubCategory faultSubCategory = faultCodeClassifier.getFaultSubCategory(faultCategory);
            Error error = new Error();
            error.setErrorCode(faultCodeClassifier.getErrorCode());
            error.setErrorMessage(faultSubCategory);
            return error;
        }
        return null;
    }

    @Override
    public String getUserAgentHeader() {
        // User agent is not required for fault scenario
        // TODO: (VirajSalaka) Throw exception
        return null;
    }

    @Override
    public String getEndUserIP() {
        // EndUserIP is not required for fault event type
        return null;
    }
}
