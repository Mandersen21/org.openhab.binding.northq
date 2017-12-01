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
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.northq.NorthQBindingConstants;
import org.openhab.binding.northq.NorthQStringConstants;
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

    @SuppressWarnings("null")
    private final Logger logger = LoggerFactory.getLogger(NorthQThermostatHandler.class);

    private NorthqServices services;

    private ScheduledFuture<?> pollingJob;
    private Runnable pollingRunnable = new Runnable() {
        @Override
        public void run() {
            ScheduleCode();
        }
    };

    /**
     * Constructor
     */
    @SuppressWarnings("null")
    public NorthQThermostatHandler(org.eclipse.smarthome.core.thing.Thing thing) {
        super(thing);

        services = new NorthqServices();
        pollingJob = scheduler.scheduleWithFixedDelay(pollingRunnable, 1, 5, TimeUnit.SECONDS);
    }

    /**
     * Abstract method overwritten
     * Requires:
     * Returns: Initialisation method
     */
    @Override
    public void initialize() {
        updateStatus(ThingStatus.ONLINE);
    }

    /**
     * Abstract method overwritten
     * Requires: a channelId and a command
     * Returns: Updates the state of the device
     */
    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

        if (channelUID.getId().equals(CHANNEL_QTHERMOSTAT)) {
            try {
                ReadWriteLock.getInstance().lockRead();
                String nodeId = getThing().getProperties().get(NorthQStringConstants.THING_ID);
                Qthermostat qThermostat = getThermostat(nodeId);

                if (qThermostat == null) {
                    updateStatus(ThingStatus.OFFLINE);
                    return;
                } else {
                    updateStatus(ThingStatus.ONLINE);
                }

                // Configurations
                String gatewayID = NorthQConfig.getNETWORK().getGateways().get(0).getGatewayId();
                String userID = NorthQConfig.getNETWORK().getUserId();

                if (command.toString() != null) {
                    String temperature = command.toString();
                    services.setTemperature(NorthQConfig.getNETWORK().getToken(), userID, gatewayID, temperature,
                            qThermostat);
                    qThermostat.getTher().temperature = Float.valueOf(temperature);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                ReadWriteLock.getInstance().unlockRead();
            }
        }
    }

    /**
     * Abstract method overwritten
     * Requires:
     * Returns: Scheduled jobs and removes thing
     */
    @Override
    public void handleRemoval() {
        if (pollingJob != null && !pollingJob.isCancelled()) {
            pollingJob.cancel(true);
        }

        // remove thing
        updateStatus(ThingStatus.REMOVED);
    }

    /**
     * Requires: a nodeId
     * Returns: gets the Qthermostat with nodeId
     */
    public @Nullable Qthermostat getThermostat(String nodeID) {
        ArrayList<NGateway> gateways = NorthQConfig.getNETWORK().getGateways();
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

    /**
     * Requires:
     * Returns: updates the thing, when run
     */
    public void ScheduleCode() {
        try {
            ReadWriteLock.getInstance().lockWrite();
            logger.debug("Polling data for thermostat");

            String nodeId = getThing().getProperties().get(NorthQStringConstants.THING_ID);
            Qthermostat qthermostat = getThermostat(nodeId);

            // Configurations
            String gatewayID = NorthQConfig.getNETWORK().getGateways().get(0).getGatewayId();
            String userID = NorthQConfig.getNETWORK().getUserId();

            // 200 = success code, everything else is some fail
            if (services.getGatewayStatus(gatewayID, userID, NorthQConfig.getNETWORK().getToken()).getStatus() != 200) {
                updateStatus(ThingStatus.OFFLINE);
                return;
            } else {
                updateStatus(ThingStatus.ONLINE);
            }

            if (qthermostat != null) {
                updateState(NorthQBindingConstants.CHANNEL_QTHERMOSTAT,
                        DecimalType.valueOf(String.valueOf(qthermostat.getTemp())));
                updateState(NorthQBindingConstants.CHANNEL_QTHERMOSTAT_BATTERY,
                        DecimalType.valueOf(String.valueOf(qthermostat.getBattery())));
            }
            if (NorthQConfig.isHEATONLOCATION()) {
                if (!NorthQConfig.ISHOME()) {
                    // When none is home maximum temp is 30
                    int temp = (int) NorthQConfig.getNOTHOMETEMP();
                    if (temp > 30) {
                        temp = 30;
                    }
                    services.setTemperature(NorthQConfig.getNETWORK().getToken(), userID, gatewayID, temp + "",
                            qthermostat);

                }
                // When someone it home the minimum temp is 5
                else if (NorthQConfig.ISHOME()) {
                    int temp = (int) NorthQConfig.getISHOMETEMP();
                    if (temp < 5) {
                        temp = 5;
                    }
                    services.setTemperature(NorthQConfig.getNETWORK().getToken(), userID, gatewayID, temp + "",
                            qthermostat);
                }
            }

        } catch (Exception e) {
            logger.error("An unexpected error occurred: {}", e.getMessage(), e);
        } finally {
            ReadWriteLock.getInstance().unlockWrite();
        }

        Boolean[] phoneHome = new Boolean[NorthQConfig.getPHONE_MAP().values().toArray().length];
        NorthQConfig.getPHONE_MAP().values().toArray(phoneHome);
    }
}
