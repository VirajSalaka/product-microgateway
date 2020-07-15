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

# This represents a single key-value pair returned from the APIM.
# + uri - claim
# + value - value
public type ClaimDTO record {|
    string uri;
    string value;
|};

# This represents the DTO which is mapped with the result received from user specific claim retrieval process.
# + count - number of claims
# + list - claims list
public type ClaimsListDTO record {|
    int count;
    ClaimDTO[] list = [];
|};

# This DTO is used to pass the Claims to JWT generation (preserving ballerina jwt payload structure where
# the self contained access token structure), when there is no self contained token is involved.
# + sub - subscription claim
# + customClaims - custom claims
public type ClaimsMapDTO record {|
    string sub = "";
    CustomClaimsMapDTO customClaims = {};
|};

# This DTO is used to pass specifically the customClaims (preserving ballerina jwt payload structure where there
# is self contained access token), when there is no self contained token is involved.
# + application - application claim
public type CustomClaimsMapDTO record {
    ApplicationClaimsMapDTO application = {};
};

# This DTO is used to pass the claims related to application, when there is no self contained token is involved.
# + id - application id
# + owner - application owner
# + name - application name
# + tier - application tier
public type ApplicationClaimsMapDTO record {|
    string id = "";
    string owner = "";
    string name = "";
    string tier = "";
|};

# This DTO is to pass the required information for user specific claim retrieval process.
# + token - Opaque Token
# + scope - scope
# + client_id - client ID
# + username - username
# + token_type - token type
# + exp - expiring timestamp
# + iat - issued timestamp
# + nbf - not before timestamp
public type OpaqueTokenInfoDTO record {|
    //token field is introduced as it is required to get the claims from cache in wso2 implementation
    string token = "";
    string scope = "";
    string client_id = "";
    string username = "";
    string token_type = "Bearer";
    int exp = 0;
    int nbf = 0;
    int iat = 0;
|};
