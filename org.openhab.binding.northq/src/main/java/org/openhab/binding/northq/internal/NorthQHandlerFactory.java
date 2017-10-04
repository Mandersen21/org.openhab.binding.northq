/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.northq.internal;

import java.util.Collection;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerFactory;
import org.openhab.binding.northq.handler.NorthQMotionHandler;
import org.openhab.binding.northq.handler.NorthQPlugHandler;
import org.osgi.service.component.annotations.Component;

import com.google.common.collect.Lists;

/**
 * The {@link NorthQHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author DTU_02162_group03 - Initial contribution
 */
@Component(service = ThingHandlerFactory.class, immediate = true, configurationPid = "binding.northq")
@NonNullByDefault
public class NorthQHandlerFactory extends BaseThingHandlerFactory {

    // private static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections.singleton(THING_TYPE_SAMPLE);

    // private static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = ImmutableSet
    // .of(NorthQPlugHandler.SUPPORTED_THING_TYPE, NorthQMotionHandler.SUPPORTED_THING_TYPE);

    private static final Collection<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Lists
            .newArrayList(NorthQPlugHandler.SUPPORTED_THING_TYPE, NorthQMotionHandler.SUPPORTED_THING_TYPE);

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (NorthQPlugHandler.SUPPORTED_THING_TYPE.equals(thing.getThingTypeUID())) {
            return new NorthQPlugHandler(thing);
        }

        if (NorthQMotionHandler.SUPPORTED_THING_TYPE.equals(thing.getThingTypeUID())) {
            return new NorthQMotionHandler(thing);
        }

        return null;
    }
}
