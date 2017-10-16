/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.northq.internal.discovery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.northq.NorthQBindingConstants;
import org.openhab.binding.northq.handler.NorthQNetworkHandler;
import org.openhab.binding.northq.internal.FreeboxDataListener;
import org.openhab.binding.northq.internal.common.NorthQConfig;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.NGateway;
import model.NorthNetwork;
import model.Qmotion;
import model.Qplug;
import model.Qthermostat;
import model.Thing;
import services.NorthqServices;

/**
 * The {@link NorthQDiscoveryService} is responsible for creating things and thing
 * handlers.
 *
 * @author DTU_02162_group03 - Initial contribution
 */
@Component(service = DiscoveryService.class, immediate = true, configurationPid = "discovery.northq")
public class NorthQDiscoveryService extends AbstractDiscoveryService implements FreeboxDataListener {

    private NorthQNetworkHandler bridgeHandler;

    @SuppressWarnings("null")
    private final Logger logger = LoggerFactory.getLogger(NorthQDiscoveryService.class);

    private ScheduledFuture<?> pollingJob;

    // stuff in whereever i guess
    private Runnable pollingRunnable = new Runnable() {

        @Override
        public void run() {
            try {
                try {
                    System.out.println("Polling run");
                    NorthqServices services = new NorthqServices();

                    NorthQConfig.NETWORK = services.mapNorthQNetwork(NorthQConfig.USERNAME, NorthQConfig.PASSWORD);
                } catch (Exception e) {
                    // catch block

                    e.printStackTrace();
                }
            } catch (Throwable t) {
                logger.error("An unexpected error occurred: {}", t.getMessage(), t);
            }
        }
    };

    /**
     * creates a discovery service with background discovery (enabled)
     *
     * @param timeout
     * @throws IllegalArgumentException
     */
    public NorthQDiscoveryService() {
        super(NorthQBindingConstants.SUPPORTED_THING_TYPES_UIDS, 0, true);
        System.out.println("DEBUG2 - in DiscoveryService");
    }

    public NorthQDiscoveryService(NorthQNetworkHandler bridge) {
        super(NorthQBindingConstants.SUPPORTED_THING_TYPES_UIDS, 0, true);
        this.bridgeHandler = bridge;
        System.out.println("DEBUG2 - in DiscoveryService");
        // TODO Auto-generated constructor stub
    }

    /**
     * this function is called to start background discovery
     *
     */
    @Override
    protected void startBackgroundDiscovery() {
        logger.debug("Start WeMo device background discovery");
        System.out.println("DEBUG2 - starting background discovery service");
        if (pollingJob == null || pollingJob.isCancelled()) {
            pollingJob = scheduler.scheduleWithFixedDelay(pollingRunnable, 1, 5, TimeUnit.SECONDS);
        }

    }

    @Override
    protected void stopBackgroundDiscovery() {
        logger.debug("Stop WeMo device background discovery");
        System.out.println("DEBUG2 - stopping background discovery service");
        if (pollingJob != null && !pollingJob.isCancelled()) {
            pollingJob.cancel(true);
            pollingJob = null;
        }

    }

    @Override
    public void startScan() {
        System.out.println("DEBUG2 - starting scan");
        onDataFetched();
        System.out.println("DEBUG2 - Scan completed thing added");
    }

    @Override
    public void onDataFetched() {
        NorthNetwork n = NorthQConfig.NETWORK;
        if (n != null) {
            ArrayList<NGateway> g = n.getGateways();
            for (int i = 0; i < g.size(); i++) {
                ArrayList<Thing> things = g.get(i).getThings();
                for (int j = 0; j < things.size(); j++) {
                    Thing thing = things.get(j);
                    if (thing instanceof Qplug) {
                        System.out.println("Discovered thing type Q plug");
                        String thingID = thing.getNodeID();
                        ThingUID newThing = new ThingUID(NorthQBindingConstants.THING_TYPE_QPLUG, thingID);
                        Map<String, Object> properties = new HashMap<>(1);
                        properties.put("thingID", thingID);
                        DiscoveryResult dr = DiscoveryResultBuilder.create(newThing).withProperties(properties)
                                .withLabel(((Qplug) thing).getBs().name)
                                .withThingType(NorthQBindingConstants.THING_TYPE_QPLUG).build();
                        thingDiscovered(dr);
                    } else if (thing instanceof Qmotion) {
                        System.out.println("Discovered thing type Q motion");
                        String thingID = thing.getNodeID();
                        ThingUID newThing = new ThingUID(NorthQBindingConstants.THING_TYPE_QMOTION, thingID);
                        Map<String, Object> properties = new HashMap<>(1);
                        properties.put("thingID", thingID);
                        DiscoveryResult dr = DiscoveryResultBuilder.create(newThing).withProperties(properties)
                                .withLabel(((Qmotion) thing).getBs().name)
                                .withThingType(NorthQBindingConstants.THING_TYPE_QMOTION).build();
                        thingDiscovered(dr);
                    } else if (thing instanceof Qthermostat) {
                        System.out.println("Discovered thing type Q thermostat");
                    }
                }
            }
        }

    }

}
