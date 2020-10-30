#!/bin/bash
# --------------------------------------------------------------------
# Copyright (c) 2020, WSO2 Inc. (http://wso2.com) All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# -----------------------------------------------------------------------

pushd ../../java-filter-chain
mvn clean install
if [ $? -ne 0 ] 
then
  echo "FAILED"
  exit 1
fi  
popd

pushd ../../
GOOS=linux GOARCH=amd64 go build -v -o target/micro-gw-ubuntu main.go
if [ $? -ne 0 ] 
then
  echo "FAILED"
  exit 1
fi  

docker build --file docker/with-external-build/filter-chain/Dockerfile --tag vsalaka/mg-filter-chain:0.0.1 --no-cache .
if [ $? -ne 0 ] 
then
  echo "FAILED"
  exit 1
fi

docker build --file docker/with-external-build/control-plane/Dockerfile --tag vsalaka/mg-control-plane:0.0.1 --no-cache .
if [ $? -ne 0 ] 
then
  echo "FAILED"
  exit 1
fi
popd

CreateDockerDirectory () {
    mkdir ../target/docker
    cp docker-compose.yaml ../target/docker/docker-compose.yaml
    cp ../../envoy.yaml ../target/docker/envoy.yaml
    cp -R ../../resources ../target/docker/resources
}

CreateDockerDirectory

# cd docker/with-external-build
# docker-compose up