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

    public static final String BINDING_ID = "northq";

    // Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_NETWORK = new ThingTypeUID(BINDING_ID, "northqnetwork");
    public static final ThingTypeUID THING_TYPE_QPLUG = new ThingTypeUID(BINDING_ID, "qPlug");
    public static final ThingTypeUID THING_TYPE_QMOTION = new ThingTypeUID(BINDING_ID, "qMotion");
    public static final ThingTypeUID THING_TYPE_QPHONE = new ThingTypeUID(BINDING_ID, "qPhone");
    public static final ThingTypeUID THING_TYPE_QTHERMOSTAT = new ThingTypeUID(BINDING_ID, "qThermostat");
    public static final ThingTypeUID THING_TYPE_MOCKNETWORK = new ThingTypeUID(BINDING_ID, "mocknetwork");

    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = ImmutableSet.of(THING_TYPE_QPLUG,
            THING_TYPE_QMOTION, THING_TYPE_NETWORK, THING_TYPE_QPHONE, THING_TYPE_QTHERMOSTAT, THING_TYPE_MOCKNETWORK);

    // Channel IDs
    public static final String CHANNEL_QSTICK = "channelstick";
    public static final String CHANNEL_QPLUG = "channelplug";
    public static final String CHANNEL_QPLUGPOWER = "channelplugpower";
    public static final String CHANNEL_QMOTION = "channelmotion";
    public static final String CHANNEL_QMOTION_TEMP = "channelmotiontemp";
    public static final String CHANNEL_QMOTION_LIGHT = "channelmotionlight";
    public static final String CHANNEL_QMOTION_HUMIDITY = "channelmotionhumidity";
    public static final String CHANNEL_QMOTION_NOTIFICATION = "channelnotification";
    public static final String CHANNEL_QMOTION_BATTERY = "channelbattery";
    public static final String CHANNEL_QPHONE = "channelgps";
    public static final String CHANNEL_QTHERMOSTAT = "channelthermostat";
    public static final String CHANNEL_QTHERMOSTAT_BATTERY = "channelthermostatbattery";
    public static final String CHANNEL_MOCKNETWORK = "channelmock";

    // Event Channel IDs
    public static final String CHANNEL_QSTICK_SCAN = "qStickScanning";
}
