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

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.northq.NorthQBindingConstants;
import org.openhab.binding.northq.handler.NorthQNetworkHandler;
import org.openhab.binding.northq.internal.NorthqDataListener;
import org.openhab.binding.northq.internal.common.NorthQConfig;
import org.openhab.binding.northq.internal.model.NGateway;
import org.openhab.binding.northq.internal.model.NorthNetwork;
import org.openhab.binding.northq.internal.model.Qmotion;
import org.openhab.binding.northq.internal.model.Qplug;
import org.openhab.binding.northq.internal.model.Qthermostat;
import org.openhab.binding.northq.internal.model.Thing;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link NorthQDiscoveryService} is responsible for creating things and thing
 * handlers, auto discoverable by framework (openhab).
 *
 * @author Dan - Initial contribution
 */
@Component(service = DiscoveryService.class, immediate = true, configurationPid = "discovery.northq")
public class NorthQDiscoveryService extends AbstractDiscoveryService implements NorthqDataListener {

    private final Logger logger = LoggerFactory.getLogger(NorthQDiscoveryService.class);

    private NorthQNetworkHandler bridgeHandler;

    /**
     * Constructor
     */
    public NorthQDiscoveryService() {
        super(NorthQBindingConstants.SUPPORTED_THING_TYPES_UIDS, 0, true);
    }

    /**
     * Constructor
     */
    public NorthQDiscoveryService(NorthQNetworkHandler bridge) {
        super(NorthQBindingConstants.SUPPORTED_THING_TYPES_UIDS, 0, true);
        this.bridgeHandler = bridge;
    }

    /**
     * this function is called to start background discovery
     */
    @Override
    protected void startBackgroundDiscovery() {
        System.out.println("Discovery - starting background service");
    }

    @Override
    protected void stopBackgroundDiscovery() {
        System.out.println("Discovery - stopping background service");
    }

    /**
     * Abstract method overwritten
     * Requires:
     * Returns: Informs framework of any discovered things
     */
    @Override
    public void startScan() {
        System.out.println("Discovery - starting scan");
        onDataFetched();
    }

    /**
     * Abstract method overwritten
     * Requires:
     * Returns: Discovers all things related to the northQ network
     */
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

                        String thingID = thing.getNodeID();
                        ThingUID newThing = new ThingUID(NorthQBindingConstants.THING_TYPE_QTHERMOSTAT, thingID);
                        Map<String, Object> properties = new HashMap<>(1);

                        properties.put("thingID", thingID);

                        DiscoveryResult dr = DiscoveryResultBuilder.create(newThing).withProperties(properties)
                                .withLabel("Thermostat" + ((Qthermostat) thing).getTher().node_id)
                                .withThingType(NorthQBindingConstants.THING_TYPE_QTHERMOSTAT).build();

                        thingDiscovered(dr);
                    }
                    // TODO Settings does not have a unique id
                    // else if (thing instanceof Qsettings) {
                    // System.out.println("Discovered thing type Q Settings");
                    // String thingID = ;
                    //
                    // ThingUID newThing = new ThingsUID(NorthQBindingConstants.THING_TYPE_SETTINGS, thingID);
                    // Map<String, Object> properties = new HashMap<>(1);
                    //
                    // properties.put("thingID", thingID);
                    //
                    // DiscoveryResult dr = DiscoveryResultBuilder.create(newThing).withProperties(properties)
                    // .withLabel("Settings" + ((Qsettings) thing).getQsettings().))
                    // .withThingType(NorthQBindingConstants.THING_TYPE_SETTINGS).build();
                    //
                    // thingDiscovered(dr);
                    // }
                }
            }

            // Things has been added by discovery
            System.out.println("Discovery - Scan completed thing added");
        }
    }
}
