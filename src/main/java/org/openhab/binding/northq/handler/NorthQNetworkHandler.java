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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.northq.internal.common.NorthQConfig;
import org.openhab.binding.northq.internal.common.ReadWriteLock;
import org.openhab.binding.northq.internal.mock.NorthQMockNetwork;
import org.openhab.binding.northq.internal.model.NorthNetwork;
import org.openhab.binding.northq.internal.services.NorthqServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link NorthQNetworkHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author DTU_02162_group03 - Initial contribution
 */
@NonNullByDefault
public class NorthQNetworkHandler extends BaseBridgeHandler {

    private final Logger logger = LoggerFactory.getLogger(NorthQNetworkHandler.class);

    private ScheduledFuture<?> pollingJob;
    private NorthqServices services;

    /**
     * Constructor
     */
    public NorthQNetworkHandler(Bridge bridge) {
        super(bridge);

        services = new NorthqServices();
        pollingJob = scheduler.scheduleWithFixedDelay(pollingRunnable, 1, 10, TimeUnit.SECONDS);
    }

    /**
     * Initialiser
     * Requires:
     * Returns: Sets up a northQ network, storing user and password information
     */
    @Override
    public void initialize() {

        // Get parameters from configuration
        NorthQConfig.setUSERNAME(getThing().getConfiguration().get("username").toString());
        NorthQConfig.setPASSWORD(getThing().getConfiguration().get("password").toString());

        NorthNetwork network = null;
        try {
            network = services.mapNorthQNetwork(NorthQConfig.getUSERNAME(), NorthQConfig.getPASSWORD());
            NorthQConfig.NETWORK = network;
            updateStatus(ThingStatus.ONLINE);
            logger.info("Q-stick is online");
        } catch (Exception e1) {
            updateStatus(ThingStatus.OFFLINE);
            logger.info("Q-stick received an error in initialization");
        }
    }

    private Runnable pollingRunnable = new Runnable() {

        @Override
        public void run() {

            // if we are not running on Mock network

            // Only run polling job with NETWORK is not null
            if (NorthQConfig.NETWORK != null) {
                try {
                    ReadWriteLock.getInstance().lockWrite();
                    System.out.println("are we testing !?!?!?!?! " + NorthQConfig.MOCK);
                    if (!NorthQConfig.MOCK) {
                        NorthQConfig.NETWORK = services.mapNorthQNetwork(NorthQConfig.USERNAME, NorthQConfig.PASSWORD);
                    } else {
                        if (NorthQConfig.MOCK_NETWORK == null) {
                            NorthQConfig.MOCK_NETWORK = new NorthQMockNetwork();
                        }
                        NorthQConfig.NETWORK = NorthQConfig.MOCK_NETWORK.getNetwork();
                        System.out.println("Test data has overwritten config");
                    }

                    System.out.println("Network fetched");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    ReadWriteLock.getInstance().unlockWrite();
                }
            }

        }
    };

    /**
     * Abstract method overwritten
     * Requires:
     * Returns:
     */
    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

    }
}
