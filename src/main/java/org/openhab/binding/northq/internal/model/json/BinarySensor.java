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
 * The {@link BinarySensor} is a pojo based on JSON from NorthQs restful API
 *
 * @author Dan / Nicolaj - Initial contribution (from standalone java)
 */

public class BinarySensor {

    public int type_id;
    public int battery;
    public int pos;
    public String serial;
    public int armed;
    public int firmware;
    public String name;
    public int trigger;
    public int manufacture;
    public int type;
    public long uploaded;
    public int product;
    public int power;
    public long read;
    public int node_id;
    public ArrayList<Sensor> sensors;
    public VersionV2Model versionV2;
    public int trigger_reset;
    public int range_testing;
    public int room;
    public int range;
    public float[] relays;

}
