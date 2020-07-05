// Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

import ballerina/http;

function retrieveClaims (string accessToken) returns @tainted ClaimsListDTO ? {
    http:Request claimRetrieveRequest = new;
    //todo: set request payload
    claimRetrieveRequest.setHeader(AUTHORIZATION_HEADER, BASIC_PREFIX_WITH_SPACE + accessToken);
    UserInfoDTO userInfoDTO = {};
    //todo: populate userInfoDTO properly
    json | error userInfoDTOJson = json.constructFrom(userInfoDTO);
    if (userInfoDTOJson is json) {
        claimRetrieveRequest.setJsonPayload(userInfoDTOJson);
    }
    http:Response | error userInfoClaimsResponse = userInfoClaimsEndpoint->post("/user-info/claims/generate",
        claimRetrieveRequest);

    if (userInfoClaimsResponse is http:Response) {
        if (userInfoClaimsResponse.statusCode == http:STATUS_OK) {
            json | error jsonPayload = userInfoClaimsResponse.getJsonPayload();
            if (jsonPayload is json) {
                ClaimsListDTO | error claimsListDTO = ClaimsListDTO.constructFrom(jsonPayload);
                if (claimsListDTO is ClaimsListDTO) {
                    return claimsListDTO;
                }  else {

                    printError("CLAIM_RETRIEVER", "Failed to map the json response to a valid ClaimList Object: " + jsonPayload.toString(),
                        claimsListDTO);
                }
            } else {
                //todo: bring constant for Error Log Key
                printError("CLAIM_RETRIEVER", "Response does not contain JSON payload", jsonPayload);
            }
        } else {
            //todo: bring constant for Error Log Key
            printError("CLAIM_RETRIEVER", "Response code from claim retrieve endpoint is " +
                userInfoClaimsResponse.statusCode.toString());
        }
    } else {
        //todo: bring constant for Error Log Key
        printError("CLAIM_RETRIEVER", "Error Response received while receiving user claims", userInfoClaimsResponse);
    }
}
