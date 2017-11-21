/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.northq.internal.mock;

import java.util.ArrayList;

import org.openhab.binding.northq.internal.model.NGateway;
import org.openhab.binding.northq.internal.model.Qmotion;
import org.openhab.binding.northq.internal.model.Qplug;
import org.openhab.binding.northq.internal.model.Qthermostat;
import org.openhab.binding.northq.internal.model.json.BinarySensor;
import org.openhab.binding.northq.internal.model.json.BinarySwitch;
import org.openhab.binding.northq.internal.model.json.Sensor;
import org.openhab.binding.northq.internal.model.json.Thermostat;

/**
 * The {@link MockFactory} is responsible generating mock things for
 * the mock network
 *
 * @author Nicolaj - Initial contribution
 */
public class MockFactory {

    private static int autoNodeId = 100;

    public static Qplug createQplug() {
        autoNodeId++;
        return createQplug("plug-" + autoNodeId, autoNodeId, 1);
    }

    public static Qmotion createQmotion() {
        autoNodeId++;
        return createQmotion("motionSensor-" + autoNodeId, autoNodeId, 1);
    }

    public static Qthermostat createQthermostat() {
        autoNodeId++;
        return createQthermostat(autoNodeId, 1);
    }

    /**
     *
     * @param name
     * @param nodeId
     * @param roomId
     * @return
     */
    public static Qplug createQplug(String name, int nodeId, int roomId) {
        BinarySwitch bs = new BinarySwitch();

        // relevant data

        bs.room = roomId;
        bs.name = name;
        bs.node_id = nodeId;

        // non-relevant data
        bs.pos = 0;
        bs.uploaded = System.currentTimeMillis() / 1000l;
        bs.wattage = 0.0f;

        return new Qplug(bs);

    }

    /**
     *
     * @param name
     * @param nodeId
     * @param roomId
     * @return
     */
    public static Qmotion createQmotion(String name, int nodeId, int roomId) {
        BinarySensor bs = new BinarySensor();

        bs.node_id = nodeId;
        bs.name = name;
        bs.room = roomId;

        Sensor s1 = new Sensor();
        s1.scale = 0;
        s1.type = 1;
        s1.value = 25.0f;

        Sensor s2 = new Sensor();
        s2.scale = 0;
        s2.type = 3;
        s2.value = 45.0f;

        Sensor s3 = new Sensor();
        s3.scale = 0;
        s3.type = 5;
        s3.value = 90.3f;

        ArrayList<Sensor> sensorList = new ArrayList();

        sensorList.add(s1);
        sensorList.add(s2);
        sensorList.add(s3);

        bs.sensors = sensorList;

        return new Qmotion(bs);

    }

    /**
     *
     * @param nodeId
     * @param roomId
     * @return
     */
    public static Qthermostat createQthermostat(int nodeId, int roomId) {
        Thermostat t = new Thermostat();

        t.node_id = nodeId;
        t.room = roomId;

        t.temperature = 20.0f;
        t.battery = -1;
        t.uploaded = System.currentTimeMillis() / 1000;

        return new Qthermostat(t);
    }

    /**
     *
     * @param gatewayId - a String of 10 digits
     * @return - an NGateway object with no things attached
     */
    public static NGateway createGateway(String gatewayId) {
        return new NGateway(gatewayId, null, null);
    }

}
