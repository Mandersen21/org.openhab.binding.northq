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
import org.openhab.binding.northq.internal.model.json.Room;
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
        NorthNetwork n = NorthQConfig.getNETWORK();
        if (n != null) {
            discoverAlldevices(n);
        }
    }

    public void discoverAlldevices(NorthNetwork n) {
        ArrayList<NGateway> g = n.getGateways();

        // for each gateway get things
        for (int i = 0; i < g.size(); i++) {
            ArrayList<Thing> things = g.get(i).getThings();
            if (g != null) {
                System.out.println("Discovered a gateway");
                String thingID = g.get(i).getGatewayId();
                ThingUID newThing = new ThingUID(NorthQBindingConstants.THING_TYPE_QPLUG, thingID);
                Map<String, Object> properties = new HashMap<>(1);

                properties.put("thingID", thingID);

                DiscoveryResult dr = DiscoveryResultBuilder.create(newThing).withProperties(properties)
                        .withLabel("Gateway: " + thingID).withThingType(NorthQBindingConstants.THING_TYPE_GATEWAY)
                        .build();

                thingDiscovered(dr);
            }
            // for each thing found in gateway
            for (int j = 0; j < things.size(); j++) {
                Thing thing = things.get(j);

                if (thing instanceof Qplug) {
                    System.out.println("Discovered thing type Q plug");

                    String thingID = thing.getNodeID();
                    ThingUID newThing = new ThingUID(NorthQBindingConstants.THING_TYPE_QPLUG, thingID);
                    Map<String, Object> properties = new HashMap<>(2);

                    properties.put("thingID", thingID);
                    properties.put("room", getRoomName(n, ((Qplug) thing).getBs().room));

                    DiscoveryResult dr = DiscoveryResultBuilder.create(newThing).withProperties(properties)
                            .withLabel(((Qplug) thing).getBs().name)
                            .withThingType(NorthQBindingConstants.THING_TYPE_QPLUG).build();

                    thingDiscovered(dr);

                } else if (thing instanceof Qmotion) {
                    System.out.println("Discovered thing type Q motion");

                    String thingID = thing.getNodeID();
                    ThingUID newThing = new ThingUID(NorthQBindingConstants.THING_TYPE_QMOTION, thingID);
                    Map<String, Object> properties = new HashMap<>(2);

                    properties.put("thingID", thingID);
                    properties.put("room", getRoomName(n, ((Qmotion) thing).getBs().room));

                    DiscoveryResult dr = DiscoveryResultBuilder.create(newThing).withProperties(properties)
                            .withLabel(((Qmotion) thing).getBs().name)
                            .withThingType(NorthQBindingConstants.THING_TYPE_QMOTION).build();

                    thingDiscovered(dr);
                } else if (thing instanceof Qthermostat) {
                    System.out.println("Discovered thing type Q thermostat");

                    String thingID = thing.getNodeID();
                    ThingUID newThing = new ThingUID(NorthQBindingConstants.THING_TYPE_QTHERMOSTAT, thingID);
                    Map<String, Object> properties = new HashMap<>(2);

                    properties.put("thingID", thingID);
                    properties.put("room", getRoomName(n, ((Qthermostat) thing).getTher().room));

                    DiscoveryResult dr = DiscoveryResultBuilder.create(newThing).withProperties(properties)
                            .withLabel("Thermostat" + ((Qthermostat) thing).getTher().node_id)
                            .withThingType(NorthQBindingConstants.THING_TYPE_QTHERMOSTAT).build();

                    thingDiscovered(dr);

                }
            }
        }

        // Things has been added by discovery
        System.out.println("Discovery - Scan completed thing added");
    }

    public String getRoomName(NorthNetwork n, int roomid) {
        String res = "";
        ArrayList<NGateway> g = n.getGateways();
        // for each gateway get things
        for (int i = 0; i < g.size(); i++) {
            ArrayList<Room> rooms = g.get(i).getRooms();
            if (rooms != null) {
                for (int j = 0; j < rooms.size(); j++) {
                    Room r = rooms.get(j);
                    System.out.println(r.name);
                    System.out.println(r.uploaded_id);
                    System.out.println(roomid);
                    if (r.uploaded_id == roomid) {
                        return r.name;
                    }
                }
            }
        }
        return res;

    }
}
