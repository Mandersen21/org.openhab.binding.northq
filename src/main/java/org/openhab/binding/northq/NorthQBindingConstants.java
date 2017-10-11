/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.northq;

import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.ThingTypeUID;

import com.google.common.collect.ImmutableSet;

/**
 * The {@link NorthQBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author DTU_02162_group03 - Initial contribution
 */
@NonNullByDefault
public class NorthQBindingConstants {

    private static final String BINDING_ID = "northq";

    // Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_QSTICK = new ThingTypeUID(BINDING_ID, "qStick");
    public static final ThingTypeUID THING_TYPE_QPLUG = new ThingTypeUID(BINDING_ID, "qPlug");
    public static final ThingTypeUID THING_TYPE_QMOTION = new ThingTypeUID(BINDING_ID, "qMotion");

    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = ImmutableSet.of(THING_TYPE_QPLUG,
            THING_TYPE_QMOTION, THING_TYPE_QSTICK);

    // Channel IDs
    public static final String CHANNEL_QSTICK = "channelstick";
    public static final String CHANNEL_QPLUG = "channelplug";
    public static final String CHANNEL_QMOTION = "channelmotion";

    // Event Channel IDs
    public static final String CHANNEL_QSTICK_SCAN = "qStickScanning";
}
