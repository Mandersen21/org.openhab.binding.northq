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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import org.openhab.binding.northq.handler.NorthQNetworkHandler;

/**
 * The {@link NorthQNetworkHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author DTU_02162_group03 - Initial contribution
 */

public class CredentialsService {

    public ArrayList<String> getUserCredentials() {

        ArrayList<String> credentials = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader("../file.txt"));
            credentials.add(br.readLine());
            credentials.add(br.readLine());
            br.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not load credentials, create file.txt with username and password");
        } catch (Exception e) {
            System.out.println("Problem occurred with loading credentials");
        }
        return credentials;
    }

}
