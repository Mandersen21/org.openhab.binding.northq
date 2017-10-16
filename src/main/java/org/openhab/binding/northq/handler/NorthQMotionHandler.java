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
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.northq.NorthQBindingConstants;
import org.openhab.binding.northq.internal.common.NorthQConfig;
import org.openhab.binding.northq.internal.common.ReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.NGateway;
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

    private NorthqServices services;
    private boolean currentStatus;
    private boolean currentTriggered;

    @SuppressWarnings("null")
    private final Logger logger = LoggerFactory.getLogger(NorthQMotionHandler.class);

    // Add to declarations
    private ScheduledFuture<?> pollingJob;

    // stuff in whereever i guess
    private Runnable pollingRunnable = new Runnable() {

        @Override
        public void run() {
            try {
                try {
                    String nodeId = getThing().getProperties().get("thingID");
                    Qmotion qMotion = getQmotion(nodeId);
                    System.out.println("Polling qmotion -- ID: " + nodeId);

                    try {
                        ReadWriteLock.getInstance().lockWrite();
                        NorthqServices services = new NorthqServices();
                        NorthQConfig.NETWORK = services.mapNorthQNetwork(NorthQConfig.USERNAME, NorthQConfig.PASSWORD);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        ReadWriteLock.getInstance().unlockWrite();
                    }
                    boolean triggered = services.isTriggered(services.getNotificationArray(
                            NorthQConfig.NETWORK.getUserId(), NorthQConfig.NETWORK.getToken(),
                            NorthQConfig.NETWORK.getHouses()[0].id + "", 1 + ""));

                    System.out.println("is there a trigger event ?!?!?!?!?!  " + triggered);

                    // triggerChannel("channelnotification", triggered ? "TRIGGERED" : "NOT_TRIGGERED");
                    if (qMotion.getStatus()) {
                        updateState(NorthQBindingConstants.CHANNEL_QMOTION_NOTIFICATION,
                                StringType.valueOf(triggered ? "TRIGGERED" : "NOT_TRIGGERED"));

                        currentTriggered = triggered;
                    } else {
                        updateState(NorthQBindingConstants.CHANNEL_QMOTION_NOTIFICATION,
                                StringType.valueOf("NOT_ARMED"));
                        currentTriggered = false;

                    }

                    // qMotion.getStatus()
                    System.out.println("currentStatus: " + currentStatus);
                    System.out.println("qplug.getStatus()" + qMotion.getStatus());
                    if (qMotion.getStatus() != currentStatus) {
                        System.out.println("status changed polling");

                        updateState(NorthQBindingConstants.CHANNEL_QMOTION,
                                qMotion.getStatus() ? OnOffType.ON : OnOffType.OFF);
                        currentStatus = qMotion.getStatus();

                    }
                    // updateState("channelmotion", OnOffType.OFF);
                    // triggerChannel("channelmotion", "OFF");

                } catch (Exception e) {
                    // catch block

                    e.printStackTrace();
                }
            } catch (Throwable t) {
                logger.error("An unexpected error occurred: {}", t.getMessage(), t);
            }
        }
    };

    public NorthQMotionHandler(Thing thing) {
        super(thing);
        services = new NorthqServices();
        currentStatus = false;
        pollingJob = scheduler.scheduleWithFixedDelay(pollingRunnable, 1, 2, TimeUnit.SECONDS);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (channelUID.getId().equals(CHANNEL_QMOTION)) {

            String gateway_id = NorthQConfig.NETWORK.getGateways().get(0).getGatewayId();// TODO: make this dynamic
            String nodeId = getThing().getProperties().get("thingID");
            Qmotion qMotion = getQmotion(nodeId);

            System.out.println("Token in config " + NorthQConfig.getTOKEN());
            System.out.println("Dan token: " + NorthQConfig.NETWORK.getToken());

            // Get token from helper
            // tokenHelper.getToken(username, password);
            // String token = NorthQConfig.getTOKEN();

            if (command.toString().equals("ON")) {
                // Plug should be turned on
                try {
                    services.armMotion(NorthQConfig.NETWORK.getUserId(), NorthQConfig.NETWORK.getToken(), gateway_id,
                            qMotion);
                    currentStatus = true;
                } catch (Exception e) {
                    // TODO: Add more exceptions
                    updateStatus(ThingStatus.OFFLINE);
                }
            } else {
                // Plug should be turned off
                try {

                    services.disarmMotion(NorthQConfig.NETWORK.getUserId(), NorthQConfig.NETWORK.getToken(), gateway_id,
                            qMotion);
                    currentStatus = false;
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

    public @Nullable Qmotion getQmotion(String nodeID) {

        ArrayList<NGateway> gateways = NorthQConfig.NETWORK.getGateways();
        for (NGateway gw : gateways) {

            ArrayList<model.Thing> things = gw.getThings();
            for (int i = 0; i < things.size(); i++) {

                if (things.get(i) instanceof model.Qmotion && nodeID.equals(things.get(i).getNodeID())) {
                    Qmotion qmotion = (Qmotion) things.get(i);

                    return qmotion;
                }
            }
        }

        return null;
    }
}
