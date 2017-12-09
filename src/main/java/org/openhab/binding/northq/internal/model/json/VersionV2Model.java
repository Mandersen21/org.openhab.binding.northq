/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openhab.binding.northq.internal.model.json;

/**
 * The {@link VersionV2Model} is a pojo based on JSON from NorthQs restful API
 *
 * @author Dan / Nicolaj - Initial contribution (from standalone java)
 */

public class VersionV2Model {

    public int hardware;
    public float zw_app;
    public float[] apps;
    public float zw_proto;
    public int zw_lib;

}
