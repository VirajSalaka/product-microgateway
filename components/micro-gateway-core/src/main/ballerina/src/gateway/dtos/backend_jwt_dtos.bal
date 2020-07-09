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

# This is the DTO sent from Microgateway to retrieve user claims.
# Additionally we can provide "domain", "dialect" properties as well.
# + username - username
# + accessToken - accessToken
public type UserInfoDTO record {
    string username = "";
    string accessToken = "";
};

# This represents a single key-value pair returned from the APIM.
# + uri - claim
# + value - value
public type ClaimDTO record {|
    string uri;
    string value;
|};

# This represents the DTO which is mapped with the response with userclaims.
# + count - number of claims
# + list - claims list
public type ClaimsListDTO record {|
    int count;
    ClaimDTO[] list;
|};

# This DTO is used to pass the customClaims, when there is no self contained token is involved.
# + customClaims - custom claims
public type ClaimsMapDTO record {|
    CustomClaimsMapDTO customClaims = {};
|};

# This DTO is used to pass the customClaims, when there is no self contained token is involved.
# + sub - subscription claim
# + application - application claim
public type CustomClaimsMapDTO record {
    string sub = "";
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

# This DTO is used to pass the claims related to application, when there is no self contained token is involved.
# + localClaim - local claim
# + remoteClaim - remote claim
public type RemoteClaimMappingDTO record {|
    string localClaim;
    string remoteClaim;
|};
