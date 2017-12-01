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
import org.openhab.binding.northq.NorthQStringConstants;
import org.openhab.binding.northq.internal.common.NorthQConfig;
import org.openhab.binding.northq.internal.common.ReadWriteLock;
import org.openhab.binding.northq.internal.mock.NorthQMockNetwork;
import org.openhab.binding.northq.internal.mock.gui.MockGui;
import org.openhab.binding.northq.internal.model.NorthNetwork;
import org.openhab.binding.northq.internal.services.CredentialsService;
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

    @SuppressWarnings("null")
    private final Logger logger = LoggerFactory.getLogger(NorthQNetworkHandler.class);

    private NorthqServices services;
    private CredentialsService credentialsService;
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
    public NorthQNetworkHandler(Bridge bridge) {
        super(bridge);

        services = new NorthqServices();
        credentialsService = new CredentialsService();
        pollingJob = scheduler.scheduleWithFixedDelay(pollingRunnable, 1, 5, TimeUnit.SECONDS);
    }

    /**
     * Initialiser
     * Requires:
     * Returns: Sets up a northQ network, storing user and password information
     */
    @Override
    public void initialize() {

        // Get parameters from configuration
        NorthQConfig.setUSERNAME(getThing().getConfiguration().get(NorthQStringConstants.USERNAME).toString());
        NorthQConfig.setPASSWORD(getThing().getConfiguration().get(NorthQStringConstants.PASSWORD).toString());
        NorthQConfig.setHOMELOCATION(getThing().getConfiguration().get(NorthQStringConstants.HOMELOCATION).toString());

        NorthNetwork network = null;
        try {
            network = services.mapNorthQNetwork(NorthQConfig.getUSERNAME(), NorthQConfig.getPASSWORD());
            NorthQConfig.setNETWORK(network);
            updateStatus(ThingStatus.ONLINE);
            logger.info("Q-stick is online");
        } catch (Exception e1) {
            updateStatus(ThingStatus.OFFLINE);
            logger.info("Q-stick received an error in initialization");
        }
    }

    /**
     * Abstract method overwritten
     * Requires:
     * Returns:
     */
    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

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
     * Requires:
     * Returns: updates the thing, when run
     */
    private void scheduleCode() {
        logger.debug("In network handler");
        // Only run polling job with NETWORK is not null
        if (NorthQConfig.getNETWORK() != null) {
            try {
                ReadWriteLock.getInstance().lockWrite();

                // Configurations
                String gatewayID = NorthQConfig.getNETWORK().getGateways().get(0).getGatewayId();
                String userID = NorthQConfig.getNETWORK().getUserId();

                // 200 = success code, everything else is some fail
                if (!NorthQConfig.isMOCK() && services
                        .getGatewayStatus(gatewayID, userID, NorthQConfig.getNETWORK().getToken()).getStatus() != 200) {
                    updateStatus(ThingStatus.OFFLINE);
                    return;
                } else {
                    updateStatus(ThingStatus.ONLINE);
                }

                if (!NorthQConfig.isMOCK()) {
                    // live
                    NorthQConfig.setNETWORK(
                            services.mapNorthQNetwork(NorthQConfig.getUSERNAME(), NorthQConfig.getPASSWORD()));
                } else {

                    // mock network
                    if (NorthQConfig.getMOCK_NETWORK() == null) {
                        NorthQConfig.setMOCK_NETWORK(new NorthQMockNetwork());

                        MockGui gui = new MockGui();
                        gui.setVisible(true);
                    }
                    NorthQConfig.setNETWORK(NorthQConfig.getMOCK_NETWORK().getNetwork());
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                ReadWriteLock.getInstance().unlockWrite();
            }
        }
    }
}