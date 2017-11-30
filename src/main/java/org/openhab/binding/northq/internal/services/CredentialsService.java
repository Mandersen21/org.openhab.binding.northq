/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openhab.binding.northq.internal.services;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import org.openhab.binding.northq.handler.NorthQNetworkHandler;
import org.openhab.binding.northq.internal.common.NorthQConfig;

/**
 * The {@link NorthQNetworkHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author DTU_02162_group03 - Initial contribution
 */

public class CredentialsService {

    public CredentialsService() {

        // Set user credentials
        ArrayList<String> userCre = getUserCredentials();
        NorthQConfig.setUSERNAME(userCre.get(0));
        NorthQConfig.setPASSWORD(userCre.get(1));

        // Set database credentials
        ArrayList<String> databaseCre = getUserCredentials();
        NorthQConfig.setSQL_USERNAME(databaseCre.get(0));
        NorthQConfig.setSQL_PASSWORD(databaseCre.get(1));

        // Set secret key
        NorthQConfig.setSECRET_KEY(getSecretKey());
    }

    public ArrayList<String> getUserCredentials() {

        ArrayList<String> credentials = new ArrayList<>();
        File file = readFile("../config.txt");

        if (file != null) {
            try {
                Scanner scanner = new Scanner(file);

                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.substring(0, line.indexOf("#")).equals("user")) {
                        credentials.add(line.substring(line.indexOf("#") + 1));
                    }
                    if (line.substring(0, line.indexOf("#")).equals("password")) {
                        credentials.add(line.substring(line.indexOf("#") + 1));
                    }
                }
                scanner.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return credentials;
    }

    public ArrayList<String> getDatabaseCredentials() {
        ArrayList<String> credentials = new ArrayList<>();
        File file = readFile("../config.txt");

        if (file != null) {
            try {
                Scanner scanner = new Scanner(file);

                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.substring(0, line.indexOf("#")).equals("sqlUser")) {
                        credentials.add(line.substring(line.indexOf("#") + 1));
                    }
                    if (line.substring(0, line.indexOf("#")).equals("sqlPassword")) {
                        credentials.add(line.substring(line.indexOf("#") + 1));
                    }
                }
                scanner.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return credentials;
    }

    public String getSecretKey() {
        File file = readFile("../config.txt");
        String key = null;

        if (file != null) {
            try {
                Scanner scanner = new Scanner(file);

                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.substring(0, line.indexOf("#")).equals("secretKey")) {
                        key = line.substring(line.indexOf("#") + 1);
                    }
                }
                scanner.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return key;
    }

    public File readFile(String filename) {
        File file = null;

        try {
            file = new File(filename);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return file;
    }

}
