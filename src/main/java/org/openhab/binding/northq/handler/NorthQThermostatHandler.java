/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.northq.handler;

import static org.openhab.binding.northq.NorthQBindingConstants.CHANNEL_QTHERMOSTAT;

import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.northq.internal.common.NorthQConfig;
import org.openhab.binding.northq.internal.common.ReadWriteLock;
import org.openhab.binding.northq.internal.model.NGateway;
import org.openhab.binding.northq.internal.model.Qthermostat;
import org.openhab.binding.northq.internal.model.Thing;
import org.openhab.binding.northq.internal.services.NorthqServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link NorthQThermostatHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author DTU_02162_group03 - Initial contribution
 */
@NonNullByDefault
public class NorthQThermostatHandler extends BaseThingHandler {

    private NorthqServices services;

    @SuppressWarnings("null")
    private final Logger logger = LoggerFactory.getLogger(NorthQThermostatHandler.class);

    boolean currentStatus;

    // Add to declarations
    private ScheduledFuture<?> pollingJob;

    private Runnable pollingRunnable = new Runnable() {

        @Override
        public void run() {
            try {
                ReadWriteLock.getInstance().lockWrite();
                System.out.println("Polling qthermostat");

                NorthqServices services = new NorthqServices();
                NorthQConfig.NETWORK = services.mapNorthQNetwork(NorthQConfig.USERNAME, NorthQConfig.PASSWORD);

                String nodeId = getThing().getProperties().get("thingID");
                Qthermostat qthermostat = getThermostat(nodeId);

                // Configurations
                String gatewayID = NorthQConfig.NETWORK.getGateways().get(0).getGatewayId();// TODO: make this dynamic
                String userID = NorthQConfig.NETWORK.getUserId();

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
    public NorthQThermostatHandler(org.eclipse.smarthome.core.thing.Thing thing) {
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

        if (channelUID.getId().equals(CHANNEL_QTHERMOSTAT)) {
            try {
                ReadWriteLock.getInstance().lockRead();
                String nodeId = getThing().getProperties().get("thingID");
                Qthermostat qThermostat = getThermostat(nodeId);

                if (qThermostat == null) {
                    updateStatus(ThingStatus.OFFLINE);
                    return;
                } else {
                    updateStatus(ThingStatus.ONLINE);
                }

                // Configurations
                String gatewayID = NorthQConfig.NETWORK.getGateways().get(0).getGatewayId();// TODO: make this dynamic
                String userID = NorthQConfig.NETWORK.getUserId();

                if (command.toString() != null) {

                    String temperature = command.toString();

                    // services.setTemperature(NorthQConfig.NETWORK.getToken(), NorthQConfig.NETWORK.getUserId(),
                    // gatewayID, temperature, qThermostat);

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
     * Returns: Fetches the thermostat given the ID
     */
    public @Nullable Qthermostat getThermostat(String nodeID) {
        ArrayList<NGateway> gateways = NorthQConfig.NETWORK.getGateways();
        for (NGateway gw : gateways) {
            ArrayList<Thing> things = gw.getThings();
            for (int i = 0; i < things.size(); i++) {
                if (things.get(i) instanceof Qthermostat && nodeID.equals(things.get(i).getNodeID())) {
                    Qthermostat thermostat = (Qthermostat) things.get(i);
                    return thermostat;
                }
            }
        }
        return null;
    }
}
