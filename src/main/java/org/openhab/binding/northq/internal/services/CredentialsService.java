/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openhab.binding.northq.internal.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import org.openhab.binding.northq.internal.common.NorthQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link CredentialsService} is responsible for fetching credentials from our password file
 *
 * @author Jakob / Philip - Initial contribution (from standalone java)
 * @author Mads / Mikkel - updated to openHAB version (All code)
 */

public class CredentialsService {

    @SuppressWarnings("null")
    private final Logger logger = LoggerFactory.getLogger(CredentialsService.class);
    private final String PATH = new File(System.getProperty("user.dir")) + "";

    public CredentialsService() {

        // Set user credentials
        ArrayList<String> userCre = getUserCredentials();
        NorthQConfig.setUSERNAME(userCre.get(0));
        NorthQConfig.setPASSWORD(userCre.get(1));

        // Set database credentials
        ArrayList<String> databaseCre = getDatabaseCredentials();
        NorthQConfig.setSQL_USERNAME(databaseCre.get(0));
        NorthQConfig.setSQL_PASSWORD(databaseCre.get(1));

        // Set SecretKey
        ArrayList<String> secretKeycre = getSecretKey();
        NorthQConfig.setSECRET_KEY(secretKeycre.get(0));
    }

    public ArrayList<String> getUserCredentials() {

        ArrayList<String> credentials = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(PATH + "/config.txt"));

            String line;
            while ((line = br.readLine()) != null) {
                if (line.substring(0, line.indexOf("#")).equals("user")) {
                    credentials.add(line.substring(line.indexOf("#") + 1));
                }
                if (line.substring(0, line.indexOf("#")).equals("password")) {
                    credentials.add(line.substring(line.indexOf("#") + 1));
                }
            }
        } catch (Exception e) {
            System.out.println("Config.txt could not be found, please place in inside: " + PATH);
            e.printStackTrace();
        }
        return credentials;
    }

    public ArrayList<String> getDatabaseCredentials() {

        ArrayList<String> credentials = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(PATH + "/config.txt"));

            String line;
            while ((line = br.readLine()) != null) {
                if (line.substring(0, line.indexOf("#")).equals("sqlUser")) {
                    credentials.add(line.substring(line.indexOf("#") + 1));
                }
                if (line.substring(0, line.indexOf("#")).equals("sqlPassword")) {
                    credentials.add(line.substring(line.indexOf("#") + 1));
                }
            }
        } catch (Exception e) {
            System.out.println("Config.txt could not be found, please place in inside: " + PATH);
            e.printStackTrace();
        }
        return credentials;
    }

    public ArrayList<String> getSecretKey() {

        ArrayList<String> credentials = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(PATH + "/config.txt"));

            String line;
            while ((line = br.readLine()) != null) {
                if (line.substring(0, line.indexOf("#")).equals("secretKey")) {
                    credentials.add(line.substring(line.indexOf("#") + 1));
                }
            }
        } catch (Exception e) {
            System.out.println("Config.txt could not be found, please place in inside: " + PATH);
            e.printStackTrace();
        }
        return credentials;
    }

}
