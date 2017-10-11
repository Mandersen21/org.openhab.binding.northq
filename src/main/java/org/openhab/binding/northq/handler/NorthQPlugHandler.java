/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.northq.handler;

import static org.openhab.binding.northq.NorthQBindingConstants.CHANNEL_QPLUG;

import java.util.ArrayList;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.northq.internal.common.NorthQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.Qplug;
import services.NorthqServices;

/**
 * The {@link NorthQPlugHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author DTU_02162_group03 - Initial contribution
 */
@NonNullByDefault
public class NorthQPlugHandler extends BaseThingHandler {

    private NorthqServices services;
    private NorthQConfig config;

    @SuppressWarnings("null")
    private final Logger logger = LoggerFactory.getLogger(NorthQPlugHandler.class);

    public NorthQPlugHandler(Thing thing) {
        super(thing);
        services = new NorthqServices();
        config = new NorthQConfig();
    }

    @SuppressWarnings("unlikely-arg-type")
    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

        if (channelUID.getId().equals(CHANNEL_QPLUG)) {

            Qplug qPlug = getPlug();

            // Configurations
            String gateway_id = NorthQConfig.getGATEWAY_ID();
            String username = NorthQConfig.getUSERNAME();

            if (command.toString().equals("ON")) {
                // Plug should be turned on
                try {
                    services.turnOnPlug(qPlug, NorthQConfig.NETWORK.getToken(), username, gateway_id);
                } catch (Exception e) {
                    // TODO: Add more exceptions
                    updateStatus(ThingStatus.OFFLINE);
                }
            } else {
                // Plug should be turned off
                try {
                    services.turnOffPlug(qPlug, NorthQConfig.NETWORK.getToken(), username, gateway_id);
                } catch (Exception e) {
                    // TODO: Add more exceptions
                    updateStatus(ThingStatus.OFFLINE);
                }
            }
        }
    }

    @Override
    public void initialize() {
        updateStatus(ThingStatus.ONLINE);
    }

    public @Nullable Qplug getPlug() {
        Qplug qPlug = null;

        ArrayList<model.Thing> things = NorthQConfig.NETWORK.getGateways().get(0).getThings();
        for (int i = 0; i < things.size(); i++) {
            if (things.get(i) instanceof model.Qplug) {
                return qPlug = (Qplug) things.get(i);
            }
        }
        return qPlug;
    }
}
