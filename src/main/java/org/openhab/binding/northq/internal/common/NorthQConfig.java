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
    private static NorthNetwork NETWORK = null;

    private static String USERNAME = "";
    private static String PASSWORD = "";
    private static boolean ISHOME = true;
    private static boolean HEATONLOCATION = false;
    private static float ISHOMETEMP = 23;
    private static float NOTHOMETEMP = 18;

    private static boolean MOCK = false;
    private static NorthQMockNetwork MOCK_NETWORK;

    private static Map<String, Boolean> PHONE_MAP = new HashMap<String, Boolean>();

    public static boolean isHEATONLOCATION() {
        return HEATONLOCATION;
    }

    public static void setHEATONLOCATION(boolean hEATONLOCATION) {
        HEATONLOCATION = hEATONLOCATION;
    }

    public static float getISHOMETEMP() {
        return ISHOMETEMP;
    }

    public static void setISHOMETEMP(float iSHOMETEMP) {
        ISHOMETEMP = iSHOMETEMP;
    }

    public static float getNOTHOMETEMP() {
        return NOTHOMETEMP;
    }

    public static void setNOTHOMETEMP(float nOTHOMETEMP) {
        NOTHOMETEMP = nOTHOMETEMP;
    }

    public static boolean ISHOME() {
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

    public static NorthNetwork getNETWORK() {
        return NETWORK;
    }

    public static void setNETWORK(NorthNetwork nETWORK) {
        NETWORK = nETWORK;
    }

    public static boolean isMOCK() {
        return MOCK;
    }

    public static void setMOCK(boolean mOCK) {
        MOCK = mOCK;
    }

    public static NorthQMockNetwork getMOCK_NETWORK() {
        return MOCK_NETWORK;
    }

    public static void setMOCK_NETWORK(NorthQMockNetwork mOCK_NETWORK) {
        MOCK_NETWORK = mOCK_NETWORK;
    }

    public static Map<String, Boolean> getPHONE_MAP() {
        return PHONE_MAP;
    }

    public static void setPHONE_MAP(Map<String, Boolean> pHONE_MAP) {
        PHONE_MAP = pHONE_MAP;
    }

}
