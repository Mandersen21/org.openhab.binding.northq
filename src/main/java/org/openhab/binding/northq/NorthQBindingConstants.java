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
    public static final ThingTypeUID THING_TYPE_GATEWAY = new ThingTypeUID(BINDING_ID, "gateway");
    public static final ThingTypeUID THING_TYPE_MOCKNETWORK = new ThingTypeUID(BINDING_ID, "mocknetwork");

    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = ImmutableSet.of(THING_TYPE_QPLUG,
            THING_TYPE_QMOTION, THING_TYPE_NETWORK, THING_TYPE_QPHONE, THING_TYPE_QTHERMOSTAT, THING_TYPE_GATEWAY,
            THING_TYPE_MOCKNETWORK);

    // Channel IDs
    public static final String CHANNEL_QSTICK = "channelstick";
    public static final String CHANNEL_QPLUG = "channelPlug";
    public static final String CHANNEL_QPLUG_POWER = "channelPlugPower";
    public static final String CHANNEL_QPLUG_POWER_DAY = "channelPlugPowerDay";
    public static final String CHANNEL_QPLUG_POWER_WEEK = "channelPlugPowerWeek";
    public static final String CHANNEL_QPLUG_POWER_MONTH = "channelPlugPowerMonth";
    public static final String CHANNEL_QPLUG_POWER_END_OF_DAY = "channelPlugPowerEndOfDay";
    public static final String CHANNEL_QPLUG_POWER_END_OF_WEEK = "channelPlugPowerEndOfWeek";
    public static final String CHANNEL_QPLUG_POWER_END_OF_MONTH = "channelPlugPowerEndOfMonth";
    public static final String CHANNEL_QMOTION = "channelmotion";
    public static final String CHANNEL_QMOTION_TEMP = "channeltemperature";
    public static final String CHANNEL_QMOTION_LIGHT = "channellight";
    public static final String CHANNEL_QMOTION_HUMIDITY = "channelhumidity";
    public static final String CHANNEL_QMOTION_NOTIFICATION = "channelnotification";
    public static final String CHANNEL_QMOTION_BATTERY = "channelbattery";
    public static final String CHANNEL_QPHONE = "channelgps";
    public static final String CHANNEL_QTHERMOSTAT = "channelthermostat";
    public static final String CHANNEL_QTHERMOSTAT_BATTERY = "channelthermostatbattery";
    public static final String CHANNEL_MOCKNETWORK = "channelmock";
    public static final String CHANNEL_SETTINGS_TOGGLEHEATLOCATION = "channeltoggleHeatOnLocation";
    public static final String CHANNEL_SETTINGS_ISHOMETEMP = "channelisHomeTemp";
    public static final String CHANNEL_SETTINGS_NOTHOMETEMP = "channelnotHomeTemp";
    public static final String CHANNEL_QPHONE_GPSLOCATION = "channelgpslocation";
    public static final String CHANNEL_SETTINGS_GPSPOWEROFF = "channelgpspoweroff";

    public static final String CHANNEL_QMOTION_POWER_ON_MOTION = "channelpoweronmotion";
    public static final String CHANNEL_QMOTION_LIGHT_ON_PERCENT = "channellightonpercent";

    public static final String CHANNEL_QMOTION_POWER_ON_MOTION_SWITCH = "channelpoweronmotionswitch";
    public static final String CHANNEL_QMOTION_LIGHT_ON_PERCENT_SWITCH = "channellightonpercentswitch";

    // Event Channel IDs
    public static final String CHANNEL_QSTICK_SCAN = "qStickScanning";
}
