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

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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

    private String sqlUser = "root";
    private String sqlPassword = "changeme";

    private long lastNotification = 0;

    private Runnable pollingRunnable = new Runnable() {
        @Override
        public void run() {
            ScheduledCode();
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
                String gatewayID = NorthQConfig.getNETWORK().getGateways().get(0).getGatewayId();
                String nodeId = getThing().getProperties().get("thingID");
                Qmotion qMotion = getQmotion(nodeId);
                if (qMotion != null) {
                    if (command.toString().equals("ON")) {
                        arm(gatewayID, qMotion);
                    } else if (command.toString().equals("OFF")) {
                        disarm(gatewayID, qMotion);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                ReadWriteLock.getInstance().unlockWrite();
            }
        }
    }

    /**
     * Requires: a gatewayId, and a qmotion thing
     * Returns: disarms the requested qmotion and updates state.
     */
    private void disarm(String gatewayID, Qmotion qMotion) throws IOException, Exception {
        services.disarmMotion(NorthQConfig.getNETWORK().getUserId(), NorthQConfig.getNETWORK().getToken(), gatewayID,
                qMotion);
        currentStatus = false;
        qMotion.getBs().armed = 0;
    }

    /**
     * Requires: a gatewayId, and a qmotion thing
     * Returns: arms the requested qmotion and updates state.
     */
    private void arm(String gatewayID, Qmotion qMotion) throws IOException, Exception {
        services.armMotion(NorthQConfig.getNETWORK().getUserId(), NorthQConfig.getNETWORK().getToken(), gatewayID,
                qMotion);
        currentStatus = true;
        qMotion.getBs().armed = 1;
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
     * Returns: gets the Qmotion with nodeId
     */
    public @Nullable Qmotion getQmotion(String nodeID) {
        ArrayList<NGateway> gateways = NorthQConfig.getNETWORK().getGateways();
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

    /**
     * Requires:
     * Returns: updates the thing, when run
     */
    public void ScheduledCode() {
        try {
            ReadWriteLock.getInstance().lockRead();
            System.out.println("Polling data for q motion");

            String nodeId = getThing().getProperties().get("thingID");
            Qmotion qMotion = getQmotion(nodeId);

            boolean triggered = services.isTriggered(services.getNotificationArray(
                    NorthQConfig.getNETWORK().getUserId(), NorthQConfig.getNETWORK().getToken(),
                    NorthQConfig.getNETWORK().getHouses()[0].id + "", 1 + ""));
            // Moved here
            if (triggered && (lastNotification + 900000) < System.currentTimeMillis()) {
                // unregister database tracking
                Connection conn;
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    conn = DriverManager.getConnection("jdbc:Mysql://localhost:3306", sqlUser, sqlPassword);
                    PreparedStatement createStatement = null;
                    createStatement = conn.prepareStatement(
                            "insert into gpsapp.notifications (`TimeStamp`,`Device`) values (NOW(),?);");
                    createStatement.setString(1, qMotion.getBs().name);
                    createStatement.executeQuery();
                } catch (Exception e) {
                    System.out.println(e.getClass().getName() + ": " + e.getMessage());
                }
                lastNotification = System.currentTimeMillis();
            }
            // 200 = success code, everything else is some fail
            if (services
                    .getGatewayStatus(NorthQConfig.getNETWORK().getGateways().get(0).getGatewayId(),
                            NorthQConfig.getNETWORK().getUserId(), NorthQConfig.getNETWORK().getToken())
                    .getStatus() != 200) {
                updateStatus(ThingStatus.OFFLINE);
                return;
            } else {
                updateStatus(ThingStatus.ONLINE);
            }

            if (qMotion != null && qMotion.getStatus()) { // Trigger state update
                updateState(NorthQBindingConstants.CHANNEL_QMOTION_NOTIFICATION,
                        StringType.valueOf(triggered ? "TRIGGERED" : "NOT_TRIGGERED"));

                currentTriggered = triggered;
            } else {
                updateState(NorthQBindingConstants.CHANNEL_QMOTION_NOTIFICATION, StringType.valueOf("NOT_ARMED"));
                currentTriggered = false;
            }

            if (qMotion != null && qMotion.getStatus() != currentStatus) { // Check if external change occurs
                updateState(NorthQBindingConstants.CHANNEL_QMOTION, qMotion.getStatus() ? OnOffType.ON : OnOffType.OFF);
                currentStatus = qMotion.getStatus();
            }

            if (qMotion != null) { // Update items:
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
            e.printStackTrace();
            logger.error("An unexpected error occurred: {}", e.getMessage(), e);
        } finally {
            ReadWriteLock.getInstance().unlockRead();
        }
    }
}
