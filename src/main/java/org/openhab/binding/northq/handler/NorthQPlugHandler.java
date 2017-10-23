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

import java.io.IOException;
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

    private Runnable pollingRunnable = new Runnable() {

        @Override
        public void run() {
            try {
                ReadWriteLock.getInstance().lockWrite();
                System.out.println("Polling qplug");

                NorthqServices services = new NorthqServices();
                NorthQConfig.NETWORK = services.mapNorthQNetwork(NorthQConfig.USERNAME, NorthQConfig.PASSWORD);

                String nodeId = getThing().getProperties().get("thingID");
                Qplug qplug = getPlug(nodeId);

                // Configurations
                String gatewayID = NorthQConfig.NETWORK.getGateways().get(0).getGatewayId();// TODO: make this dynamic
                String userID = NorthQConfig.NETWORK.getUserId();
                System.out.println("Turn off plug automatically" + qplug != null && !NorthQConfig.ISHOME);

                if (qplug != null && !NorthQConfig.ISHOME) {
                    System.out.println("Is home changed");
                    try {
                        boolean res = services.turnOffPlug(qplug, NorthQConfig.NETWORK.getToken(), userID, gatewayID);
                        System.out.println("Success " + res);
                        currentStatus = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                        updateStatus(ThingStatus.OFFLINE);
                    }
                    updateState("channelplug", OnOffType.OFF);
                    currentStatus = false;

                }

                if (qplug != null && qplug.getStatus() != currentStatus) {
                    System.out.println("currentStatus: " + currentStatus);
                    System.out.println("qplug.getStatus()" + qplug.getStatus());
                    updateState("channelplug", qplug.getStatus() ? OnOffType.ON : OnOffType.OFF);
                    currentStatus = qplug.getStatus();
                }
            } catch (Exception e) {
                logger.error("An unexpected error occurred: {}", e.getMessage(), e);
            } finally {
                ReadWriteLock.getInstance().unlockWrite();
            }
        }
    };

    /**
     * Constructor
     */
    public NorthQPlugHandler(Thing thing) {
        super(thing);
        services = new NorthqServices();
        currentStatus = false;
        pollingJob = scheduler.scheduleWithFixedDelay(pollingRunnable, 1, 5, TimeUnit.SECONDS);
    }

    /**
     * Abstract method overwritten
     * Requires: a channelId and a command
     * Returns: Updates the state of the device
     */
    @SuppressWarnings("unlikely-arg-type")
    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

        if (channelUID.getId().equals(CHANNEL_QPLUG)) {
            try {
                ReadWriteLock.getInstance().lockRead();
                String nodeId = getThing().getProperties().get("thingID");
                Qplug qPlug = getPlug(nodeId);

                if (qPlug == null) {
                    updateStatus(ThingStatus.OFFLINE);
                    return;
                } else {
                    updateStatus(ThingStatus.ONLINE);
                }

                // Configurations
                String gatewayID = NorthQConfig.NETWORK.getGateways().get(0).getGatewayId();// TODO: make this dynamic
                String userID = NorthQConfig.NETWORK.getUserId();

                if (command.toString().equals("ON")) {
                    turnPlugOn(qPlug, gatewayID, userID);
                } else {
                    turnPlugOff(qPlug, gatewayID, userID);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                ReadWriteLock.getInstance().unlockRead();
            }
        }
        // Start polling job
        pollingRunnable.run();
    }

    /**
     * Abstract method overwritten
     * Requires: qplug, gatewayId and the userID
     * Returns: Turns the physical device on
     *
     * @throws Exception
     * @throws IOException
     */
    private void turnPlugOff(Qplug qPlug, String gatewayID, String userID) throws IOException, Exception {
        boolean res = services.turnOffPlug(qPlug, NorthQConfig.NETWORK.getToken(), userID, gatewayID);
        System.out.println("Success " + res);
        currentStatus = false;

    }

    /**
     * Abstract method overwritten
     * Requires: qplug, gatewayId and the userID
     * Returns: Turns the physical device on
     * 
     * @throws Exception
     * @throws IOException
     */
    private void turnPlugOn(Qplug qPlug, String gatewayID, String userID) throws IOException, Exception {
        boolean res = services.turnOnPlug(qPlug, NorthQConfig.NETWORK.getToken(), userID, gatewayID);
        System.out.println("Success " + res);
        currentStatus = true;
    }

    /**
     * Abstract method overwritten
     * Requires:
     * Returns: Initialization method
     */
    @Override
    public void initialize() {
        updateStatus(ThingStatus.ONLINE);
        System.out.println("Initialized: " + pollingJob);
    }

    /**
     * Requires: A nodeID
     * Returns: Fetches the plug given the ID
     */
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
