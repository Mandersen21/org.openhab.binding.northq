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
import org.openhab.binding.northq.internal.model.json.BinarySwitch;

/**
 * The {@link NorthQNetworkHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author DTU_02162_group03 - Initial contribution
 */

public class Qplug extends Thing implements IThing {
    private BinarySwitch bs;

    public Qplug(BinarySwitch bs) {
        super();
        this.bs = bs;
    }

    public boolean getStatus() {
        return (bs.pos != 0);
    }

    public BinarySwitch getBs() {
        return bs;
    }

    public void setBs(BinarySwitch bs) {
        this.bs = bs;
    }

    public float getPowerConsumption() {
        if (bs != null) {
            return bs.sensors.get(0).value;
        }
        return 0;
    }

    @Override
    public String getNodeID() {
        return bs.node_id + "";
    }
}
