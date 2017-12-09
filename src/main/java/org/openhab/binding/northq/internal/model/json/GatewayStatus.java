/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openhab.binding.northq.internal.model.json;

import java.util.ArrayList;

/**
 * The {@link GatewayStatus} is a pojo based on JSON from NorthQs restful API
 *
 * @author Dan / Nicolaj - Initial contribution (from standalone java)
 */

public class GatewayStatus {

    public ArrayList<BinarySensor> BinarySensors;
    public ArrayList<BinarySwitch> BinarySwitches;
    public ArrayList<Thermostat> Thermostats;

}
