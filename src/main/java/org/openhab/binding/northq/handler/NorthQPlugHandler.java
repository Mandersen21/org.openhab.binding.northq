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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.northq.helpers.TokenHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import services.NorthqServices;

/**
 * The {@link NorthQPlugHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author DTU_02162_group03 - Initial contribution
 */
@NonNullByDefault
public class NorthQPlugHandler extends BaseThingHandler {

    private TokenHelper tokenHelper;
    private NorthqServices services;

    @SuppressWarnings("null")
    private final Logger logger = LoggerFactory.getLogger(NorthQPlugHandler.class);

    public NorthQPlugHandler(Thing thing) {
        super(thing);
        tokenHelper = new TokenHelper();
        services = new NorthqServices();
    }

    @SuppressWarnings("unlikely-arg-type")
    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

        if (channelUID.getId().equals(CHANNEL_QPLUG)) {

            String username = "";
            String password = "";

            String token = tokenHelper.getToken(username, password);
            System.out.println("Token received from token service: " + token);

            if (command.toString().equals("ON")) {
                // Plug should be turned on
                // services.turnOnPlug(plug, token, user, gatewayId)

            } else {
                // Plug should be turned off
                // services.turnOffPlug(plug, token, user, gatewayId)
            }
        }
    }

    @Override
    public void initialize() {
        updateStatus(ThingStatus.ONLINE);
    }
}
