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
 * The {@link SettingsHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author DTU_02162_group03 - Initial contribution
 */

public class SettingsHandler extends BaseThingHandler {

    public SettingsHandler(org.eclipse.smarthome.core.thing.Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        try {
            ReadWriteLock.getInstance().lockRead();

            updateState(NorthQBindingConstants.CHANNEL_SETTINGS_TOGGLEHEATLOCATION,
                    NorthQConfig.isHEATONLOCATION() ? OnOffType.ON : OnOffType.OFF);
            updateState(NorthQBindingConstants.CHANNEL_SETTINGS_ISHOMETEMP,
                    DecimalType.valueOf(String.valueOf(NorthQConfig.getISHOMETEMP())));
            updateState(NorthQBindingConstants.CHANNEL_SETTINGS_NOTHOMETEMP,
                    DecimalType.valueOf(String.valueOf(NorthQConfig.getNOTHOMETEMP())));

            if (channelUID.getId().equals(CHANNEL_SETTINGS_TOGGLEHEATLOCATION)) {
                if (command.toString().equals("ON")) {
                    NorthQConfig.setHEATONLOCATION(true);

                } else if (command.toString().equals("OFF")) {
                    NorthQConfig.setHEATONLOCATION(false);
                }
            }
            if (channelUID.getId().equals(CHANNEL_SETTINGS_ISHOMETEMP)) {
                if (command.toString() != null) {
                    NorthQConfig.setISHOMETEMP(Float.valueOf(command.toString()));
                }
            }
            if (channelUID.getId().equals(CHANNEL_SETTINGS_NOTHOMETEMP)) {
                if (command.toString() != null) {
                    NorthQConfig.setNOTHOMETEMP(Float.valueOf(command.toString()));
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
        updateStatus(ThingStatus.ONLINE);
    }

}
