/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.apimgt.gateway.cli.cmd;

import com.beust.jcommander.JCommander;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is to parse JCommander arguments containing space.
 */
public class ExtendedJCommander extends JCommander {
    private List<String> specialCommandList = new ArrayList<>();

    ExtendedJCommander(Object object) {
        super(object);
    }

    @Override
    public void addCommand(String var1, Object var2) {
        String[] commandArgs = var1.split(" ");
        if (commandArgs.length == 2 && !specialCommandList.contains(commandArgs[0])) {
            specialCommandList.add(commandArgs[0]);
        }
        super.addCommand(var1, var2);
    }

    @Override
    public void parse(String... args) {
        if (specialCommandList.contains(args[0])) {
            //if any special command which appears with space, the args will be modified before executed by parent
            // class
            String[] modifiedCmdArgs = new String[args.length - 1];
            System.arraycopy(args, 2, modifiedCmdArgs, 1, modifiedCmdArgs.length - 1);
            modifiedCmdArgs[0] = args[0] + " " + args[1];
            super.parse(modifiedCmdArgs);
        } else {
            super.parse(args);
        }
    }
}
