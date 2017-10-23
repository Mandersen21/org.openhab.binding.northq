/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.northq.internal.common;

import model.NorthNetwork;

/**
 * The {@link NorthQConfig} is responsible for keeping config variables to use in the binding.
 *
 * @author DTU_02162_group03 - Initial contribution
 */

public class NorthQConfig {

    // North network
    public static NorthNetwork NETWORK = null;

    // Authentication
    public static String USERNAME = "";
    public static String PASSWORD = "";
    public static boolean ISHOME = true;

    public static boolean isISHOME() {
        return ISHOME;
    }

    public static void setISHOME(boolean iSHOME) {
        ISHOME = iSHOME;
    }

    public static String getUSERNAME() {
        return USERNAME;
    }

    public static void setUSERNAME(String uSERNAME) {
        USERNAME = uSERNAME;
    }

    public static String getPASSWORD() {
        return PASSWORD;
    }

    public static void setPASSWORD(String pASSWORD) {
        PASSWORD = pASSWORD;
    }
}
