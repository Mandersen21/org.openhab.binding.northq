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

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.northq.NorthQBindingConstants;
import org.openhab.binding.northq.internal.common.NorthQConfig;
import org.openhab.binding.northq.internal.common.ReadWriteLock;

/**
 * The {@link GatewayHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author DTU_02162_group03 - Initial contribution
 */

public class GatewayHandler extends BaseThingHandler {
    /**
     * Constructor
     */
    public GatewayHandler(org.eclipse.smarthome.core.thing.Thing thing) {
        super(thing);
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

            // updating ToggleHeatLocation variable dependent on input from channel
            if (channelUID.getId().equals(CHANNEL_SETTINGS_TOGGLEHEATLOCATION)) {
                if (command.toString().equals("ON")) {
                    NorthQConfig.setHEATONLOCATION(true);

                } else if (command.toString().equals("OFF")) {
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
            // updating ToggleHeatLocation variable dependent on input from channel
            if (channelUID.getId().equals(CHANNEL_SETTINGS_GPSPOWEROFF)) {
                if (command.toString().equals("ON")) {
                    NorthQConfig.setHEATONLOCATION(true);

                } else if (command.toString().equals("OFF")) {
                    NorthQConfig.setHEATONLOCATION(false);
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
        // set initial values
        updateState(NorthQBindingConstants.CHANNEL_SETTINGS_TOGGLEHEATLOCATION,
                NorthQConfig.isHEATONLOCATION() ? OnOffType.ON : OnOffType.OFF);
        updateState(NorthQBindingConstants.CHANNEL_SETTINGS_ISHOMETEMP,
                DecimalType.valueOf(String.valueOf(NorthQConfig.getISHOMETEMP())));
        updateState(NorthQBindingConstants.CHANNEL_SETTINGS_NOTHOMETEMP,
                DecimalType.valueOf(String.valueOf(NorthQConfig.getNOTHOMETEMP())));

    }

}
