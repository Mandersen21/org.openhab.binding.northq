/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openhab.binding.northq.internal.model;

import java.util.ArrayList;

import org.openhab.binding.northq.handler.NorthQNetworkHandler;
import org.openhab.binding.northq.internal.model.json.GatewayStatus;

/**
 * The {@link NorthQNetworkHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author DTU_02162_group03 - Initial contribution
 */

public class NGateway {
    private String gatewayId;
    private ArrayList<Thing> things;

    public NGateway(String gatewayId, GatewayStatus gatewayStatus) {
        this.gatewayId = gatewayId;
        things = new ArrayList<>();
        if (gatewayStatus != null) {
            if (gatewayStatus.BinarySwitches != null) {
                for (int i = 0; i < gatewayStatus.BinarySwitches.size(); i++) {
                    things.add(new Qplug(gatewayStatus.BinarySwitches.get(i)));
                }
            }
            if (gatewayStatus.BinarySensors != null) {
                for (int i = 0; i < gatewayStatus.BinarySensors.size(); i++) {
                    things.add(new Qmotion(gatewayStatus.BinarySensors.get(i)));
                }
            }
            if (gatewayStatus.Thermostats != null) {
                for (int i = 0; i < gatewayStatus.Thermostats.size(); i++) {
                    things.add(new Qthermostat(gatewayStatus.Thermostats.get(i)));
                }
            }

        }

    }

    public String getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
    }

    public ArrayList<Thing> getThings() {
        return things;
    }

    public void setThings(ArrayList<Thing> things) {
        this.things = things;
    }

    public void addThing(Thing t) {
        if (things != null) {
            things.add(t);
        }
    }

}
