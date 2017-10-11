/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.northq.handler;

import static org.openhab.binding.northq.NorthQBindingConstants.CHANNEL_QMOTION;

import java.util.ArrayList;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.northq.internal.common.NorthQConfig;
import org.openhab.binding.northq.internal.helpers.TokenHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.Qmotion;
import services.NorthqServices;

/**
 * The {@link NorthQMotionHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author DTU_02162_group03 - Initial contribution
 */
@NonNullByDefault
public class NorthQMotionHandler extends BaseThingHandler {

    private TokenHelper tokenHelper;
    private NorthqServices services;
    private NorthQConfig config;

    @SuppressWarnings("null")
    private final Logger logger = LoggerFactory.getLogger(NorthQMotionHandler.class);

    public NorthQMotionHandler(Thing thing) {
        super(thing);
        tokenHelper = new TokenHelper();
        services = new NorthqServices();
        config = new NorthQConfig();
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (channelUID.getId().equals(CHANNEL_QMOTION)) {

            // Configurations
            String gateway_id = NorthQConfig.getGATEWAY_ID();
            String username = NorthQConfig.getUSERNAME();
            String password = NorthQConfig.getPASSWORD();
            Qmotion qMotion = null;

            ArrayList<model.Thing> things = NorthQConfig.NETWORK.getGateways().get(0).getThings();
            for (int i = 0; i < things.size(); i++) {
                if (things.get(i) instanceof model.Qmotion) {
                    qMotion = (Qmotion) things.get(i);
                }
            }

            System.out.println("Token in config " + NorthQConfig.getTOKEN());
            System.out.println("Dan token: " + NorthQConfig.NETWORK.getToken());

            // Get token from helper
            // tokenHelper.getToken(username, password);
            // String token = NorthQConfig.getTOKEN();

            if (command.toString().equals("ON")) {
                // Plug should be turned on
                try {
                    services.armMotion(NorthQConfig.NETWORK.getUserId(), NorthQConfig.NETWORK.getToken(),
                            NorthQConfig.GATEWAY_ID, qMotion);
                } catch (Exception e) {
                    // TODO: Add more exceptions
                    updateStatus(ThingStatus.OFFLINE);
                }
            } else {
                // Plug should be turned off
                try {
                    services.disarmMotion(NorthQConfig.NETWORK.getUserId(), NorthQConfig.NETWORK.getToken(),
                            NorthQConfig.GATEWAY_ID, qMotion);
                } catch (Exception e) {
                    // TODO: Add more exceptions
                    updateStatus(ThingStatus.OFFLINE);
                }
            }
        }

        // if (channelUID.getId().equals(CHANNEL_QMOTION))
        //
        // {
        // System.out.print("Registered interactions with motion");
        // }
    }

    @Override
    public void initialize() {
        updateStatus(ThingStatus.ONLINE);
    }
}
