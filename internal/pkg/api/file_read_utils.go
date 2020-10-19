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

package api

import (
	"archive/zip"
	"bytes"
	"fmt"
	"io/ioutil"
	"log"
	"strings"

	logger "github.com/wso2/micro-gw/internal/loggers"
	"github.com/wso2/micro-gw/internal/pkg/oasparser/utills"
	xds "github.com/wso2/micro-gw/internal/pkg/xds"
)

func UnzipAndApplyZippedProject(payload []byte) {
	zipReader, err := zip.NewReader(bytes.NewReader(payload), int64(len(payload)))

	if err != nil {
		fmt.Println("Error occured in unzipping")
		fmt.Println(err.Error())
	}

	//TODO: (VirajSalaka) this won't support for distributed openAPI definition
	for _, f := range zipReader.File {
		//TODO: (VirajSalaka) provide a proper regex to filter openAPI json
		//TODO: (VirajSalaka) support .yaml files
		if strings.HasSuffix(f.Name, "swagger.yaml") {
			fmt.Println(f.Name)
			unzippedFileBytes, err := readZipFile(f)
			if err != nil {
				logger.LoggerMgw.Error(err)
				continue
			}
			apiJsn, conversionErr := utills.ToJSON(unzippedFileBytes)
			if conversionErr != nil {
				log.Fatal("Error converting api file to json:", err)
				return
			}
			xds.UpdateEnvoyByteArr(apiJsn)
		}
	}
}

func ApplyOpenAPIFile(payload []byte) {
	apiJsn, err := utills.ToJSON(payload)
	if err != nil {
		log.Fatal("Error converting api file to json:", err)
		return
	}
	xds.UpdateEnvoyByteArr(apiJsn)
}

func readZipFile(zf *zip.File) ([]byte, error) {
	f, err := zf.Open()
	if err != nil {
		return nil, err
	}
	defer f.Close()
	return ioutil.ReadAll(f)
}
