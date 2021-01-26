/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.micro.gateway.enforcer.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

/**
 * Utility Functions related to TLS Certificates.
 */
public class TLSUtils {
    private static final String X509 = "X.509";

    /**
     * Read the certificate file and return the certificate.
     *
     * @param filePath Filepath of the corresponding certificate
     * @return Certificate
     */
    public static Certificate getCertificateFromFile(String filePath)
            throws CertificateException, FileNotFoundException {
        CertificateFactory fact = CertificateFactory.getInstance(X509);
        FileInputStream is = new FileInputStream(filePath);
        return fact.generateCertificate(is);
    }
}
