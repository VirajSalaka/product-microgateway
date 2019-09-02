// Copyright (c)  WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file   except
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

public type GrpcFilter object {

    map<string> httpGrpcStatusCodeMap = {};
    map<string> httpGrpcErrorMsgMap = {};
    
    public function _init() returns error? {
        self.httpGrpcStatusCodeMap["401"] = "16";
        self.httpGrpcStatusCodeMap["403"] = "7";
        self.httpGrpcStatusCodeMap["404"] = "12";
        self.httpGrpcStatusCodeMap["429"] = "8";
        //todo: verify https://github.com/grpc/grpc/blob/master/doc/statuscodes.md
        self.httpGrpcStatusCodeMap["500"] = "2";

        self.httpGrpcErrorMsgMap["401"] = "Unauthenticated";
        self.httpGrpcErrorMsgMap["403"] = "Unauthorized";
        self.httpGrpcErrorMsgMap["404"] = "Service not found";
        self.httpGrpcErrorMsgMap["429"] = "Too many function calls";
        self.httpGrpcErrorMsgMap["500"] = "Internal server error";
    }
    public function filterRequest(http:Caller caller, http:Request request, @tainted http:FilterContext context)
                        returns boolean {                                        
        checkOrSetMessageID(context);
        addGrpcToFilterContext(context);
        return true;
    }

    public function filterResponse(http:Response response, http:FilterContext context) returns boolean {
        if (!filterGrpcResponse(response, context)) {
            return true;
        }
        string statusCode = response.statusCode.toString();
        string grpcStatus = self.httpGrpcStatusCodeMap[statusCode] ?: "";
        string grpcErrorMessage = self.httpGrpcErrorMsgMap[statusCode] ?: "";

        if(statusCode == "") {
            response.setHeader("grpc-status", "2");
            response.setHeader("grpc-message", "Response is not recognized by the gateway.");
            return true;
        }
        response.setHeader("grpc-status", grpcStatus);
        response.setHeader("grpc-message", grpcErrorMessage);
        response.setContentType("application/grpc");
        response.setPayload("");
        return true;
    }
};

function addGrpcToFilterContext(http:FilterContext context){
    //todo: check if ballerina map support boolean
    context.attributes["isGrpc"] = true;
    printDebug(KEY_GRPC_FILTER, "\"isGrpc\" key is added to the request " + context.attributes[MESSAGE_ID].toString());
}

function filterGrpcResponse(http:Response response, http:FilterContext context) returns boolean {
    //todo: check if needs to check the content type as well.
    if(response.hasHeader("grpc-status")){
        string grpcStatus = response.getHeader("grpc-status").toUpperAscii();
            if(grpcStatus != "UNIMPLEMENTED") {
                return false;
            }
        }
    any isGrpcAttr = context.attributes["isGrpc"];

    if(isGrpcAttr is boolean){
        return true;
    }
    return false;
}