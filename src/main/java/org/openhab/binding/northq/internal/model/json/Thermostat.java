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

import org.openhab.binding.northq.handler.NorthQNetworkHandler;

/**
 * The {@link NorthQNetworkHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author DTU_02162_group03 - Initial contribution
 */

public class Thermostat {

    public long uploaded;
    public int product;
    public int firmware;
    public int room;
    public int battery;
    public int interval;
    public VersionV2Model versionV2;
    public long read;
    public int range;
    public int node_id;
    public int scale;
    public int range_testing;
    public String serial;
    public int controle_mode;
    public int type;
    public ArrayList<Sensor> sensors;
    public int manufacturer;
    public float temperature;

}
