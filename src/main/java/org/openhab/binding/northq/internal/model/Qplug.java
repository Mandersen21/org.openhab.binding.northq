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
 * The {@link NorthQNetworkHandler} is a pojo using the interface Ithing, extending thing, which holds the Qplug
 * variables
 *
 * @author Dan / Nicolaj - Initial contribution (from standalone java)
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
            return bs.wattage;
        }
        return 0;
    }

    @Override
    public String getNodeID() {
        return bs.node_id + "";
    }
}
