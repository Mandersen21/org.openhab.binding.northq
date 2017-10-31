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
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.northq.NorthQBindingConstants;
import org.openhab.binding.northq.internal.common.NorthQConfig;
import org.openhab.binding.northq.internal.common.ReadWriteLock;
import org.openhab.binding.northq.internal.model.NGateway;
import org.openhab.binding.northq.internal.model.Qmotion;
import org.openhab.binding.northq.internal.model.Thing;
import org.openhab.binding.northq.internal.services.NorthqServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link NorthQMotionHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author DTU_02162_group03 - Initial contribution
 */
@NonNullByDefault
public class NorthQMotionHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(NorthQMotionHandler.class);

    private NorthqServices services;
    private boolean currentStatus;
    private boolean currentTriggered;

    private ScheduledFuture<?> pollingJob;
    private Runnable pollingRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                ReadWriteLock.getInstance().lockRead();
                System.out.println("Polling data for q motion");

                String nodeId = getThing().getProperties().get("thingID");
                Qmotion qMotion = getQmotion(nodeId);

                boolean triggered = services.isTriggered(services.getNotificationArray(NorthQConfig.NETWORK.getUserId(),
                        NorthQConfig.NETWORK.getToken(), NorthQConfig.NETWORK.getHouses()[0].id + "", 1 + ""));

                if (qMotion != null && qMotion.getStatus()) {
                    updateState(NorthQBindingConstants.CHANNEL_QMOTION_NOTIFICATION,
                            StringType.valueOf(triggered ? "TRIGGERED" : "NOT_TRIGGERED"));

                    currentTriggered = triggered;
                } else {
                    updateState(NorthQBindingConstants.CHANNEL_QMOTION_NOTIFICATION, StringType.valueOf("NOT_ARMED"));
                    currentTriggered = false;
                }

                if (qMotion != null && qMotion.getStatus() != currentStatus) {
                    updateState(NorthQBindingConstants.CHANNEL_QMOTION,
                            qMotion.getStatus() ? OnOffType.ON : OnOffType.OFF);
                    currentStatus = qMotion.getStatus();
                }

                if (qMotion != null) {
                    updateState(NorthQBindingConstants.CHANNEL_QMOTION_TEMP,
                            DecimalType.valueOf(String.valueOf(qMotion.getTmp())));
                    updateState(NorthQBindingConstants.CHANNEL_QMOTION_LIGHT,
                            DecimalType.valueOf(String.valueOf(qMotion.getLight())));
                    updateState(NorthQBindingConstants.CHANNEL_QMOTION_HUMIDITY,
                            DecimalType.valueOf(String.valueOf(qMotion.getHumidity())));
                    updateState(NorthQBindingConstants.CHANNEL_QMOTION_BATTERY,
                            DecimalType.valueOf(String.valueOf(qMotion.getBattery())));
                }
            } catch (Exception e) {
                logger.error("An unexpected error occurred: {}", e.getMessage(), e);
            } finally {
                ReadWriteLock.getInstance().unlockRead();
            }
        }
    };

    /**
     * Constructor
     */
    public NorthQMotionHandler(org.eclipse.smarthome.core.thing.Thing thing) {
        super(thing);

        services = new NorthqServices();
        currentStatus = false;
        pollingJob = scheduler.scheduleWithFixedDelay(pollingRunnable, 1, 5, TimeUnit.SECONDS);
    }

    /**
     * Abstract method overwritten
     * Requires:
     * Returns: Initialiser
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

        if (channelUID.getId().equals(CHANNEL_QMOTION)) {
            try {
                ReadWriteLock.getInstance().lockWrite();
                String gatewayID = NorthQConfig.NETWORK.getGateways().get(0).getGatewayId();
                String nodeId = getThing().getProperties().get("thingID");
                Qmotion qMotion = getQmotion(nodeId);

                if (command.toString().equals("ON")) {
                    services.armMotion(NorthQConfig.NETWORK.getUserId(), NorthQConfig.NETWORK.getToken(), gatewayID,
                            qMotion);
                    currentStatus = true;
                    qMotion.getBs().armed = 1;

                } else if (command.toString().equals("OFF")) {
                    services.disarmMotion(NorthQConfig.NETWORK.getUserId(), NorthQConfig.NETWORK.getToken(), gatewayID,
                            qMotion);
                    currentStatus = false;
                    qMotion.getBs().armed = 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                ReadWriteLock.getInstance().unlockWrite();
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

    public @Nullable Qmotion getQmotion(String nodeID) {
        ArrayList<NGateway> gateways = NorthQConfig.NETWORK.getGateways();
        for (NGateway gw : gateways) {
            ArrayList<Thing> things = gw.getThings();
            for (int i = 0; i < things.size(); i++) {
                if (things.get(i) instanceof Qmotion && nodeID.equals(things.get(i).getNodeID())) {
                    Qmotion qmotion = (Qmotion) things.get(i);
                    return qmotion;
                }
            }
        }
        return null;
    }
}
