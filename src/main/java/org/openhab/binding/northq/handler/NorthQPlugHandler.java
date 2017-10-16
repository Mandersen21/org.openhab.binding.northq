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
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.northq.internal.common.NorthQConfig;
import org.openhab.binding.northq.internal.common.ReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.NGateway;
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

    @SuppressWarnings("null")
    private final Logger logger = LoggerFactory.getLogger(NorthQPlugHandler.class);

    boolean currentStatus;

    // Add to declarations
    private ScheduledFuture<?> pollingJob;

    // stuff in whereever i guess
    private Runnable pollingRunnable = new Runnable() {

        @Override
        public void run() {
            try {
                try {
                    System.out.println("Polling qplug");

                    try {
                        ReadWriteLock.getInstance().lockWrite();
                        NorthqServices services = new NorthqServices();
                        NorthQConfig.NETWORK = services.mapNorthQNetwork(NorthQConfig.USERNAME, NorthQConfig.PASSWORD);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        ReadWriteLock.getInstance().unlockWrite();
                    }

                    String nodeId = getThing().getProperties().get("thingID");
                    Qplug qplug = getPlug(nodeId);
                    // qplug.getStatus()
                    if (qplug.getStatus() != currentStatus) {
                        System.out.println("status changed polling");
                        System.out.println("currentStatus: " + currentStatus);
                        System.out.println("qplug.getStatus()" + qplug.getStatus());
                        updateState("channelplug", qplug.getStatus() ? OnOffType.ON : OnOffType.OFF);
                        currentStatus = qplug.getStatus();

                    }
                    // updateState("channelplug", OnOffType.OFF);
                    // triggerChannel("channelplug", "OFF");

                } catch (Exception e) {
                    // catch block

                    e.printStackTrace();
                }
            } catch (Throwable t) {
                logger.error("An unexpected error occurred: {}", t.getMessage(), t);
            }
        }
    };

    public NorthQPlugHandler(Thing thing) {
        super(thing);
        services = new NorthqServices();
        currentStatus = false;
        pollingJob = scheduler.scheduleWithFixedDelay(pollingRunnable, 1, 2, TimeUnit.SECONDS);

    }

    @SuppressWarnings("unlikely-arg-type")
    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // Recived command
        System.out.println("Debug In handleCommand");
        if (channelUID.getId().equals(CHANNEL_QPLUG)) {
            System.out.println("Debug In handleCommand in Channel Qplug");

            String nodeId = getThing().getProperties().get("thingID");
            Qplug qPlug = getPlug(nodeId);

            if (qPlug == null) {
                updateStatus(ThingStatus.OFFLINE);
                return;
            } else {
                updateStatus(ThingStatus.ONLINE);
            }

            System.out.println(qPlug.getNodeID());

            System.out.println("UID" + super.getThing().getUID());
            // Configurations
            String gateway_id = NorthQConfig.NETWORK.getGateways().get(0).getGatewayId();// TODO: make this dynamic
            String username = NorthQConfig.NETWORK.getUserId();

            System.out.println("gateway:" + gateway_id);
            System.out.println("username" + username);
            System.out.println("config networktoken; " + NorthQConfig.NETWORK.getToken());
            try {
                System.out.println("current token:" + services.postLogin(username, username).token);
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            if (command.toString().equals("ON")) {
                currentStatus = true;
                System.out.println("Debug In handleCommand turn on");

                // Plug should be turned on

                try {
                    ReadWriteLock.getInstance().lockRead();
                    boolean res = services.turnOnPlug(qPlug, NorthQConfig.NETWORK.getToken(), username, gateway_id);
                    System.out.println("Success " + res);
                } catch (Exception e) {
                    e.printStackTrace();
                    // updateStatus(ThingStatus.OFFLINE);
                } finally {
                    ReadWriteLock.getInstance().unlockRead();
                }
            } else {
                currentStatus = false;
                System.out.println("Debug In handleCommand turn off");
                try {
                    ReadWriteLock.getInstance().lockRead();
                    boolean res = services.turnOffPlug(qPlug, NorthQConfig.NETWORK.getToken(), username, gateway_id);
                    System.out.println("Success " + res);
                } catch (Exception e) {
                    e.printStackTrace();
                    // updateStatus(ThingStatus.OFFLINE);
                } finally {
                    ReadWriteLock.getInstance().unlockRead();
                }
            }
        }
    }

    @Override
    public void initialize() {
        updateStatus(ThingStatus.ONLINE);

        System.out.println("Initialized: " + pollingJob);
    }

    public @Nullable Qplug getPlug(String nodeID) {

        ArrayList<NGateway> gateways = NorthQConfig.NETWORK.getGateways();
        for (NGateway gw : gateways) {

            ArrayList<model.Thing> things = gw.getThings();
            for (int i = 0; i < things.size(); i++) {

                if (things.get(i) instanceof model.Qplug && nodeID.equals(things.get(i).getNodeID())) {
                    Qplug plug = (Qplug) things.get(i);

                    return plug;
                }
            }
        }

        return null;
    }
}
