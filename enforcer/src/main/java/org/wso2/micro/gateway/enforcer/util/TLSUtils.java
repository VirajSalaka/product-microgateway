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

import org.apache.commons.lang3.RandomStringUtils;
import org.checkerframework.checker.units.qual.C;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.List;

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

    public static void addCertsToTruststore(KeyStore trustStore, String filePath) throws IOException {
        if (!Files.exists(Paths.get(filePath))){
            //Print error
            return;
        }
        if (Files.isDirectory(Paths.get(filePath))) {
            Files.walk(Paths.get(filePath)).filter(path -> {
                Path fileName = path.getFileName();
                return fileName != null && (fileName.toString().endsWith(".crt") ||
                        fileName.toString().endsWith(".pem"));
            }).forEach(path -> {
                try {
                    List<Certificate> certificateList = getCertsFromFile(path.toAbsolutePath().toString(), false);
                    certificateList.forEach(certificate -> {
                        try {
                            trustStore.setCertificateEntry(RandomStringUtils.random(10,true, false),
                                    certificate);
                        } catch (KeyStoreException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (CertificateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } else {

        }

    }

    public static List<Certificate> getCertsFromFile(String filepath, boolean restrictToOne)
            throws CertificateException, IOException {
        FileInputStream fis = new FileInputStream(filepath);
        BufferedInputStream bis = new BufferedInputStream(fis);
        List<Certificate> certList = new ArrayList<>();
        CertificateFactory cf = null;
        cf = CertificateFactory.getInstance("X.509");
        int count = 0;
        while (bis.available() > 0) {
            if (count > 1 && restrictToOne) {
                //TODO: (VirajSalaka) Print warning
                return certList;
            }
            Certificate cert = cf.generateCertificate(bis);
            certList.add(cert);
            count++;
        }
        return certList;
    }
}
