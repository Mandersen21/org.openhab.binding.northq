/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openhab.binding.northq.internal.model;

import org.openhab.binding.northq.handler.NorthQNetworkHandler;
import org.openhab.binding.northq.internal.model.json.BinarySensor;

/**
 * The {@link NorthQNetworkHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author DTU_02162_group03 - Initial contribution
 */

public class Qmotion extends Thing implements IThing {
    private BinarySensor bs;

    public Qmotion(BinarySensor bs) {
        super();
        this.bs = bs;
    }

    @Override
    public String getNodeID() {
        return bs.node_id + "";

    }

    public BinarySensor getBs() {
        return bs;
    }

    public void setBs(BinarySensor bs) {
        this.bs = bs;
    }

    public boolean getStatus() {
        return (bs.armed == 1);
    }

    public float getHumidity() {
        if (bs != null) {
            return bs.sensors.get(2).value;
        }
        return 0;
    }

    public float getLight() {
        if (bs != null) {
            return bs.sensors.get(1).value;
        }
        return 0;
    }

    public float getTmp() {
        if (bs != null) {
            return bs.sensors.get(0).value;
        }
        return 0;
    }

    public int getBattery() {
        if (bs != null) {
            return bs.battery;
        }
        return 0;
    }
}
