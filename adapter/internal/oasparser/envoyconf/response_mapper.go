/*
 *  Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package envoyconf

import (
	access_logv3 "github.com/envoyproxy/go-control-plane/envoy/config/accesslog/v3"
	corev3 "github.com/envoyproxy/go-control-plane/envoy/config/core/v3"
	hcmv3 "github.com/envoyproxy/go-control-plane/envoy/extensions/filters/network/http_connection_manager/v3"
	"google.golang.org/protobuf/types/known/structpb"
	"google.golang.org/protobuf/types/known/wrapperspb"
)

func getErrorResponseMappers() []*hcmv3.ResponseMapper {
	return []*hcmv3.ResponseMapper{
		genErrorResponseMapper(101503, "Connection failed", "UF"),
		genErrorResponseMapper(101504, "Connection timed out", ""),
		genErrorResponseMapper(101505, "Connection timed out", ""),
		genErrorResponseMapper(101506, "Connection timed out", "DPE"),
		genErrorResponseMapper(101507, "Connection timed out", ""),
	}
}

func genErrorResponseMapper(errorCode int32, message string, flag string) *hcmv3.ResponseMapper {
	errorMsgMap := make(map[string]*structpb.Value)
	errorMsgMap["code"] = structpb.NewNumberValue(float64(errorCode))
	errorMsgMap["message"] = structpb.NewStringValue(message)
	errorMsgMap["description"] = structpb.NewStringValue("%LOCAL_REPLY_BODY%")

	mapper := &hcmv3.ResponseMapper{
		Filter: &access_logv3.AccessLogFilter{
			// TODO: (VirajSalaka) Decide if the status code needs to be checked in addition to flags
			FilterSpecifier: &access_logv3.AccessLogFilter_ResponseFlagFilter{
				ResponseFlagFilter: &access_logv3.ResponseFlagFilter{
					Flags: []string{flag},
				},
			},
		},
		StatusCode: wrapperspb.UInt32(500),
		BodyFormatOverride: &corev3.SubstitutionFormatString{
			Format: &corev3.SubstitutionFormatString_JsonFormat{
				JsonFormat: &structpb.Struct{
					Fields: errorMsgMap,
				},
			},
		},
	}
	return mapper
}
