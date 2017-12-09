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
import org.openhab.binding.northq.NorthQStringConstants;
import org.openhab.binding.northq.handler.NorthQNetworkHandler;
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
 * The {@link NorthQDiscoveryService} is responsible for discovering things, auto discoverable by framework.
 *
 * @author Dan / Nicolaj - Initial contribution
 */
@Component(service = DiscoveryService.class, immediate = true, configurationPid = "discovery.northq")
public class NorthQDiscoveryService extends AbstractDiscoveryService {

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
        logger.debug("Discovery - starting scan");
        NorthNetwork n = NorthQConfig.getNETWORK();
        if (n != null) {
            discoverAlldevices(n);
        }
    }

    /**
     * Requires:The network to which discovery is to take place
     * Returns: Discovers all devices in network
     */
    public void discoverAlldevices(NorthNetwork n) {
        ArrayList<NGateway> g = n.getGateways();
        // for each gateway get things
        for (int i = 0; i < g.size(); i++) {
            ArrayList<Thing> things = g.get(i).getThings();
            if (g != null) {
                discoverGateway(g, i);
            }
            // for each thing found in gateway
            for (int j = 0; j < things.size(); j++) {
                Thing thing = things.get(j);

                if (thing instanceof Qplug) {
                    discoverQplug(n, thing);
                } else if (thing instanceof Qmotion) {
                    discoverQmotion(n, thing);
                } else if (thing instanceof Qthermostat) {
                    discoveryQthermostat(n, thing);

                }
            }
        }
    }

    /**
     * Requires: the network context and a found thing
     * Returns: Informs framework of a discovered gateway
     */
    private void discoverGateway(ArrayList<NGateway> g, int i) {
        logger.debug("Discovered a gateway");
        String thingID = g.get(i).getGatewayId();
        ThingUID newThing = new ThingUID(NorthQBindingConstants.THING_TYPE_QPLUG, thingID);
        Map<String, Object> properties = new HashMap<>(1);

        properties.put(NorthQStringConstants.THING_ID, thingID);

        DiscoveryResult dr = DiscoveryResultBuilder.create(newThing).withProperties(properties)
                .withLabel("Gateway: " + thingID.substring(5)).withThingType(NorthQBindingConstants.THING_TYPE_GATEWAY)
                .build();

        thingDiscovered(dr);
    }

    /**
     * Requires: the network context and a found thing
     * Returns: Informs framework of a discovered Qplug
     */
    private void discoverQplug(NorthNetwork n, Thing thing) {
        logger.debug("Discovered thing type Q plug");

        String thingID = thing.getNodeID();
        ThingUID newThing = new ThingUID(NorthQBindingConstants.THING_TYPE_QPLUG, thingID);
        Map<String, Object> properties = new HashMap<>(2);

        properties.put(NorthQStringConstants.THING_ID, thingID);
        properties.put(NorthQStringConstants.ROOM, getRoomName(n, ((Qplug) thing).getBs().room));

        DiscoveryResult dr = DiscoveryResultBuilder.create(newThing).withProperties(properties)
                .withLabel(((Qplug) thing).getBs().name).withThingType(NorthQBindingConstants.THING_TYPE_QPLUG).build();

        thingDiscovered(dr);
    }

    /**
     * Requires: the network context and a found thing
     * Returns: Informs framework of a discovered Qmotion
     */
    private void discoverQmotion(NorthNetwork n, Thing thing) {
        logger.debug("Discovered thing type Q motion");

        String thingID = thing.getNodeID();
        ThingUID newThing = new ThingUID(NorthQBindingConstants.THING_TYPE_QMOTION, thingID);
        Map<String, Object> properties = new HashMap<>(2);

        properties.put(NorthQStringConstants.THING_ID, thingID);
        properties.put(NorthQStringConstants.ROOM, getRoomName(n, ((Qmotion) thing).getBs().room));

        DiscoveryResult dr = DiscoveryResultBuilder.create(newThing).withProperties(properties)
                .withLabel(((Qmotion) thing).getBs().name).withThingType(NorthQBindingConstants.THING_TYPE_QMOTION)
                .build();

        thingDiscovered(dr);
    }

    /**
     * Requires: the network context and a found thing
     * Returns: Informs framework of a discovered Qthermostat
     */
    private void discoveryQthermostat(NorthNetwork n, Thing thing) {
        logger.debug("Discovered thing type Q thermostat");

        String thingID = thing.getNodeID();
        ThingUID newThing = new ThingUID(NorthQBindingConstants.THING_TYPE_QTHERMOSTAT, thingID);
        Map<String, Object> properties = new HashMap<>(2);

        properties.put(NorthQStringConstants.THING_ID, thingID);
        properties.put(NorthQStringConstants.ROOM, getRoomName(n, ((Qthermostat) thing).getTher().room));

        DiscoveryResult dr = DiscoveryResultBuilder.create(newThing).withProperties(properties)
                .withLabel("Thermostat" + ((Qthermostat) thing).getTher().node_id)
                .withThingType(NorthQBindingConstants.THING_TYPE_QTHERMOSTAT).build();

        thingDiscovered(dr);
    }

    /**
     * Requires: the network context and a room id
     * Returns: extracts the room name from network given the Id
     */
    public String getRoomName(NorthNetwork n, int roomid) {
        String res = "";
        ArrayList<NGateway> g = n.getGateways();
        // for each gateway get things
        for (int i = 0; i < g.size(); i++) {
            ArrayList<Room> rooms = g.get(i).getRooms();
            if (rooms != null) {
                for (int j = 0; j < rooms.size(); j++) {
                    Room r = rooms.get(j);
                    if (r.uploaded_id == roomid) {
                        return r.name;
                    }
                }
            }
        }
        return res;

    }
}
