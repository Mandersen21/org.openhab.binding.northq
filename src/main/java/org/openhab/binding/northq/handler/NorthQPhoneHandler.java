/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.northq.handler;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.northq.NorthQBindingConstants;
import org.openhab.binding.northq.internal.common.NorthQConfig;
import org.openhab.binding.northq.internal.services.NetworkUtils;
import org.openhab.binding.northq.internal.services.NorthqServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link NorthQPhoneHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author DTU_02162_group03 - Initial contribution
 */
@NonNullByDefault
public class NorthQPhoneHandler extends BaseThingHandler {

    private NorthqServices services;

    @SuppressWarnings("null")
    private final Logger logger = LoggerFactory.getLogger(NorthQPhoneHandler.class);

    // boolean status:
    private boolean status;

    // Add to declarations
    private ScheduledFuture<?> pollingJob;

    private Runnable pollingRunnable = new Runnable() {

        @Override
        public void run() {
            System.out.println("Polling Qphone");
            Form form = new Form();
            form.param("getGPS", NorthQConfig.USERNAME);
            NetworkUtils nu = new NetworkUtils();
            try {
                if (status) {
                    Response res = nu.getHttpPostResponse("http://95.85.57.71:8080/NorthqGpsService/gps", form);
                    String result = String.valueOf(res.readEntity(String.class).charAt(0));
                    res.close();

                    // If a new update comes in and the ishome is to be switched
                    // If not home
                    System.out.println("away is set: " + result.equals("0"));
                    System.out.println("away is set: " + result.equals("1"));
                    if (status && result.equals("0") && NorthQConfig.ISHOME) {
                        // turn off device
                        NorthQConfig.setISHOME(false);
                        System.out.println("Set config to: " + NorthQConfig.ISHOME);

                    } // If home
                    else if (status && result.equals("1") && !NorthQConfig.ISHOME) {
                        NorthQConfig.setISHOME(true);
                        System.out.println("Set config to: " + NorthQConfig.ISHOME);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    /**
     * Constructor
     */
    @SuppressWarnings("null")
    public NorthQPhoneHandler(Thing thing) {
        super(thing);
        status = false;
        services = new NorthqServices();
        pollingJob = scheduler.scheduleWithFixedDelay(pollingRunnable, 1, 5, TimeUnit.SECONDS);
    }

    /**
     * Abstract method overwritten
     * Requires: a channelId and a command
     * Returns: Updates the state of the device
     */
    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (channelUID.getId().equals(NorthQBindingConstants.CHANNEL_QPHONE)) {
            System.out.println("In enable GPS services");
            if (command.toString().equals("ON")) {
                status = true;
                // NorthQConfig.setISHOME(true);
            } else if (command.toString().equals("OFF")) {
                NorthQConfig.setISHOME(true);
                status = false;
            }
        }
    }

    /**
     * Abstract method overwritten
     * Requires:
     * Returns: Initialisation method
     */
    @Override
    public void initialize() {
        updateStatus(ThingStatus.ONLINE);
        System.out.println("Initialized: " + pollingJob);
    }

}
