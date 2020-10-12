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
	"strings"

	logger "github.com/wso2/micro-gw/internal/loggers"
	xds "github.com/wso2/micro-gw/internal/pkg/xds"
)

func UnzipFileToByteArray(payload []byte) {
	zipReader, err := zip.NewReader(bytes.NewReader(payload), int64(len(payload)))

	if err != nil {
		fmt.Println("Error occured in unzipping")
		fmt.Println(err.Error())
	}

	for _, f := range zipReader.File {
		if strings.HasSuffix(f.Name, ".json") {
			fmt.Println(f.Name)
			unzippedFileBytes, err := readZipFile(f)
			if err != nil {
				logger.LoggerMgw.Error(err)
				continue
			}
			xds.UpdateEnvoyByteArr(unzippedFileBytes)
		}
	}
}

func readZipFile(zf *zip.File) ([]byte, error) {
	f, err := zf.Open()
	if err != nil {
		return nil, err
	}
	defer f.Close()
	return ioutil.ReadAll(f)
}
