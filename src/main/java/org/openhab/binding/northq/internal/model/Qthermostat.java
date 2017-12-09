/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openhab.binding.northq.internal.model;

import org.openhab.binding.northq.internal.model.json.Thermostat;

/**
 * The {@link Qthermostat} is a pojo using the interface Ithing, extending thing, which holds the Qthermostat variables
 *
 * @author Dan / Nicolaj - Initial contribution (from standalone java)
 */

public class Qthermostat extends Thing implements IThing {
    private Thermostat ther;

    public Qthermostat(Thermostat ther) {
        super();

        this.ther = ther;
    }

    public Thermostat getTher() {
        return ther;
    }

    public float getTemp() {
        if (ther != null) {
            return ther.temperature;
        }
        return 0;
    }

    public void setTher(Thermostat ther) {
        this.ther = ther;
    }

    @Override
    public String getNodeID() {

        return ther.node_id + "";

    }

    public int getBattery() {
        if (ther != null) {
            return ther.battery;
        }
        return 0;
    }

}
