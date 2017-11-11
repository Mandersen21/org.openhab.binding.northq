/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.northq.handler;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.northq.NorthQBindingConstants;
import org.openhab.binding.northq.internal.common.NorthQConfig;

/**
 * The {@link MockNetworkHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Nicolaj - Initial contribution
 */
@NonNullByDefault
public class MockNetworkHandler extends BaseThingHandler {

    public MockNetworkHandler(Thing thing) {
        super(thing);
        // TODO Auto-generated constructor stub
    }

    /**
     * Abstract method overwritten
     * Requires: a channelId and a command
     * Returns: Updates the state of the device
     */
    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (channelUID.getId().equals(NorthQBindingConstants.CHANNEL_MOCKNETWORK)) {
            System.out.println("---------------------------Mock network handler engaged!----------------------------");
            if (command.toString().equals("ON")) {
                NorthQConfig.setMOCK(true);
            } else if (command.toString().equals("OFF")) {
                NorthQConfig.setMOCK(false);
            }
        }
    }

    @Override
    public void initialize() {
        updateStatus(ThingStatus.ONLINE);
    }

}
