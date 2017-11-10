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
import org.openhab.binding.northq.internal.model.json.Settings;

/**
 * The {@link NorthQNetworkHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author DTU_02162_group03 - Initial contribution
 */

public class Qsettings extends Thing implements IThing {
    private Settings set;

    public Qsettings(Settings set) {
        super();
        this.set = set;
    }

    public Settings getQsettings() {
        return set;
    }

    public void setQsettings(Settings set) {
        this.set = set;
    }

    public float getIsHomeTemp() {
        return set.isHomeTemp;
    }

    public float getNotHomeTemp() {
        return set.notHomeTemp;
    }

    public boolean getToggleHeatOnLocation() {
        return set.toggleHeatOnLocation;
    }

    @Override
    public String getNodeID() {
        return null;
    }
}