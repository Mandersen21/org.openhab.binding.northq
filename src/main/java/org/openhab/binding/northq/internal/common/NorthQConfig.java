/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.northq.internal.common;

import java.util.HashMap;
import java.util.Map;

import org.openhab.binding.northq.internal.mock.NorthQMockNetwork;
import org.openhab.binding.northq.internal.model.NorthNetwork;

/**
 * The {@link NorthQConfig} is responsible for keeping config variables to use in the binding.
 *
 * @author DTU_02162_group03 - Initial contribution
 */

public class NorthQConfig {

    // North network
    public static NorthNetwork NETWORK = null;

    public static String USERNAME = "";
    public static String PASSWORD = "";
    public static boolean ISHOME = true;

    public static boolean MOCK = false;
    public static NorthQMockNetwork MOCK_NETWORK;

    public static Map<String, Boolean> PHONE_MAP = new HashMap<String, Boolean>();

    // TODO: clean this, so we do not die

    public static boolean isISHOME() {
        return ISHOME;
    }

    public static void setISHOME(boolean ishome) {
        ISHOME = ishome;
    }

    public static String getUSERNAME() {
        return USERNAME;
    }

    public static void setUSERNAME(String username) {
        USERNAME = username;
    }

    public static String getPASSWORD() {
        return PASSWORD;
    }

    public static void setPASSWORD(String password) {
        PASSWORD = password;
    }
}
