/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openhab.binding.northq.handler;

import static org.openhab.binding.northq.NorthQBindingConstants.*;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.northq.NorthQBindingConstants;
import org.openhab.binding.northq.NorthQStringConstants;
import org.openhab.binding.northq.internal.common.NorthQConfig;
import org.openhab.binding.northq.internal.common.ReadWriteLock;

/**
 * The {@link GatewayHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author DTU_02162_group03 - Initial contribution
 */

public class GatewayHandler extends BaseThingHandler {
    private ScheduledFuture<?> pollingJob;
    private Runnable pollingRunnable = new Runnable() {

        @Override
        public void run() {
            scheduleCode();
        }
    };

    /**
     * Constructor
     */
    @SuppressWarnings("null")
    public GatewayHandler(org.eclipse.smarthome.core.thing.Thing thing) {
        super(thing);
        pollingJob = scheduler.scheduleWithFixedDelay(pollingRunnable, 1, 5, TimeUnit.SECONDS);
    }

    /**
     * Abstract method overwritten
     * Requires: a channelId and a command
     * Returns: Updates the state of the device
     */
    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        try {
            ReadWriteLock.getInstance().lockWrite();

            updateState(NorthQBindingConstants.CHANNEL_SETTINGS_TOGGLEHEATLOCATION,
                    NorthQConfig.isHEATONLOCATION() ? OnOffType.ON : OnOffType.OFF);
            updateState(NorthQBindingConstants.CHANNEL_SETTINGS_ISHOMETEMP,
                    DecimalType.valueOf(String.valueOf(NorthQConfig.getISHOMETEMP())));
            updateState(NorthQBindingConstants.CHANNEL_SETTINGS_NOTHOMETEMP,
                    DecimalType.valueOf(String.valueOf(NorthQConfig.getNOTHOMETEMP())));

            // updating ToggleHeatLocation variable dependent on input from channel
            if (channelUID.getId().equals(CHANNEL_SETTINGS_TOGGLEHEATLOCATION)) {
                if (command.toString().equals(NorthQStringConstants.ON)) {
                    NorthQConfig.setHEATONLOCATION(true);

                } else if (command.toString().equals(NorthQStringConstants.OFF)) {
                    NorthQConfig.setHEATONLOCATION(false);
                }
            }
            // updating IsHomeTemp variable dependent on input from channel
            if (channelUID.getId().equals(CHANNEL_SETTINGS_ISHOMETEMP)) {
                if (command.toString() != null) {
                    NorthQConfig.setISHOMETEMP(Float.valueOf(command.toString()));
                }
            }
            // updating NoHomeTemp variable dependent on input from channel
            if (channelUID.getId().equals(CHANNEL_SETTINGS_NOTHOMETEMP)) {
                if (command.toString() != null) {
                    NorthQConfig.setNOTHOMETEMP(Float.valueOf(command.toString()));
                }
            }
            // updating TogglePowerOnLocation variable dependent on input from channel
            if (channelUID.getId().equals(CHANNEL_SETTINGS_GPSPOWEROFF)) {
                if (command.toString().equals(NorthQStringConstants.ON)) {
                    NorthQConfig.setPOWERONLOCATION(true);

                } else if (command.toString().equals(NorthQStringConstants.OFF)) {
                    NorthQConfig.setPOWERONLOCATION(false);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ReadWriteLock.getInstance().unlockWrite();
        }
    }

    /**
     * Abstract method overwritten
     * Requires:
     * Returns: Scheduled jobs and removes thing
     */
    @Override
    public void handleRemoval() {
        updateStatus(ThingStatus.REMOVED);
        if (pollingJob != null && !pollingJob.isCancelled()) {
            pollingJob.cancel(true);
        }
    }

    /**
     * Abstract method overwritten
     * Requires:
     * Returns: Initialiser
     */
    @Override
    public void initialize() {
        NorthQConfig.setHEATONLOCATION(false);
        updateStatus(ThingStatus.ONLINE);
    }

    private void scheduleCode() {
        try {

            Boolean[] phoneHome = new Boolean[NorthQConfig.getPHONE_MAP().values().toArray().length];
            NorthQConfig.getPHONE_MAP().values().toArray(phoneHome);

            boolean allAway = true;
            for (Boolean b : phoneHome) {
                boolean bol = b.booleanValue();
                if (bol) {
                    allAway = false;
                }
            }

            if (NorthQConfig.isHEATONLOCATION()) {
                updateState(NorthQBindingConstants.CHANNEL_SETTING_STATUS_GPS_HEATING,
                        StringType.valueOf(allAway ? NorthQStringConstants.OUT : NorthQStringConstants.HOME));
            } else {
                updateState(NorthQBindingConstants.CHANNEL_SETTING_STATUS_GPS_HEATING,
                        StringType.valueOf(NorthQStringConstants.INACTIVE));
            }
            if (NorthQConfig.isPOWERONLOCATION()) {
                updateState(NorthQBindingConstants.CHANNEL_SETTING_STATUS_GPS_POWER,
                        StringType.valueOf(allAway ? NorthQStringConstants.OUT : NorthQStringConstants.HOME));
            } else {
                updateState(NorthQBindingConstants.CHANNEL_SETTING_STATUS_GPS_POWER,
                        StringType.valueOf(NorthQStringConstants.INACTIVE));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
