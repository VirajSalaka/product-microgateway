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
package configs

import (
	"io/ioutil"
	"os"
	"strings"
	"sync"

	"github.com/BurntSushi/toml"
	logger "github.com/sirupsen/logrus"
	config "github.com/wso2/micro-gw/configs/confTypes"
)

var (
	onceConfigRead    sync.Once
	onceLogConfigRead sync.Once
	onceGetMgwHome    sync.Once
	configs           *config.Config
	logConfigs        *config.LogConfig
	mgwHome           string
	e                 error
)

const (
	mgwHomeEnvVariable = "MGW_HOME"
)

/**
 * Read the control plane main configs.
 *
 * @return *config.Config Reference for config instance
 * @return *error Error
 */
func ReadConfigs() (*config.Config, error) {
	onceConfigRead.Do(func() {
		configs = new(config.Config)
		getMgwHome()

		// logger.Info("MGW_HOME: ", mgwHome)
		_, err := os.Stat(mgwHome + "/conf/config.toml")
		if err != nil {
			logger.Fatal("Configuration file not found.", err)
		}
		content, readErr := ioutil.ReadFile(mgwHome + "/conf/config.toml")
		if readErr != nil {
			logger.Fatal("Error reading configurations. ", readErr)
		}
		_, e = toml.Decode(string(content), configs)
	})

	return configs, e
}

/**
 * Read the control plane log configs.
 *
 * @return *config.LogConfig Reference for log config instance
 * @return *error Error
 */
func ReadLogConfigs() (*config.LogConfig, error) {
	onceLogConfigRead.Do(func() {
		logConfigs = new(config.LogConfig)
		getMgwHome()
		//TODO: (VirajSalaka) Provide path properly
		_, err := os.Stat(mgwHome + "/conf/log_config.toml")
		if err != nil {
			logger.Fatal("Log configuration file not found.", err)
		}
		content, readErr := ioutil.ReadFile(mgwHome + "/conf/log_config.toml")
		if readErr != nil {
			logger.Fatal("Error reading log configurations. ", readErr)
		}
		_, e = toml.Decode(string(content), logConfigs)

	})

	return logConfigs, e
}

/**
 * Clear the singleton log config instance for the hot update.
 *
 */
func ClearLogConfigInstance() {
	onceLogConfigRead = sync.Once{}
}

func getMgwHome() {
	onceGetMgwHome.Do(func() {
		mgwHome = os.Getenv(mgwHomeEnvVariable)
		if len(strings.TrimSpace(mgwHome)) == 0 {
			mgwHome, _ = os.Getwd()
		}
	})
}
