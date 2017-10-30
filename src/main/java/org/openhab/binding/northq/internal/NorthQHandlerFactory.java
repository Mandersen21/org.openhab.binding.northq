/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.northq.internal;

import static org.openhab.binding.northq.NorthQBindingConstants.SUPPORTED_THING_TYPES_UIDS;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerFactory;
import org.openhab.binding.northq.NorthQBindingConstants;
import org.openhab.binding.northq.handler.MockNetworkHandler;
import org.openhab.binding.northq.handler.NorthQMotionHandler;
import org.openhab.binding.northq.handler.NorthQNetworkHandler;
import org.openhab.binding.northq.handler.NorthQPhoneHandler;
import org.openhab.binding.northq.handler.NorthQPlugHandler;
import org.openhab.binding.northq.handler.NorthQThermostatHandler;
import org.openhab.binding.northq.internal.discovery.NorthQDiscoveryService;
import org.osgi.service.component.annotations.Component;

/**
 * The {@link NorthQHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author DTU_02162_group03 - Initial contribution
 */
@Component(service = ThingHandlerFactory.class, immediate = true, configurationPid = "binding.northq")
@NonNullByDefault
public class NorthQHandlerFactory extends BaseThingHandlerFactory {

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    public @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        // Setup discovery
        if (thingTypeUID.equals(NorthQBindingConstants.THING_TYPE_NETWORK)) {
            NorthQNetworkHandler handler = new NorthQNetworkHandler((Bridge) thing);
            registerDiscoveryService(handler);
            return handler;
        }
        // New plug
        if (thingTypeUID.equals(NorthQBindingConstants.THING_TYPE_QPLUG)) {
            return new NorthQPlugHandler(thing);
        }
        // New motion
        if (thingTypeUID.equals(NorthQBindingConstants.THING_TYPE_QMOTION)) {
            return new NorthQMotionHandler(thing);
        }
        // New phone
        if (thingTypeUID.equals(NorthQBindingConstants.THING_TYPE_QPHONE)) {
            return new NorthQPhoneHandler(thing);
        }
        // New thermostat
        if (thingTypeUID.equals(NorthQBindingConstants.THING_TYPE_QTHERMOSTAT)) {
            return new NorthQThermostatHandler(thing);
        }
        // New Mock thing
        if (thingTypeUID.equals(NorthQBindingConstants.THING_TYPE_MOCKNETWORK)) {
            return new MockNetworkHandler(thing);
        }
        return null;
    }

    private void registerDiscoveryService(NorthQNetworkHandler bridgeHandler) {
        NorthQDiscoveryService discoveryService = new NorthQDiscoveryService(bridgeHandler);
        discoveryService.startScan();
    }

}
