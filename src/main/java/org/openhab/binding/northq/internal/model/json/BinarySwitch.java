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
 * The {@link BinarySwitch} is a pojo based on JSON from NorthQs restful API
 *
 * @author Dan / Nicolaj - Initial contribution (from standalone java)
 */

public class BinarySwitch {

    public float[] assocuations;
    public long uploaded;
    public int product;
    public VersionV2Model versionV2;
    public int room;
    public String name;
    public long read;
    public float wattage;
    public int firmware;
    public int pos;
    public float value;
    public int no_range;
    public int node_id;
    public int id;
    public Vacation vacation;
    public OverrideObject override;
    public String serial;
    public int type;
    public ArrayList<Sensor> sensors;
    public int manufacture;

}
