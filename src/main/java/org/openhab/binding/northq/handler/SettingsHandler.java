package org.openhab.binding.northq.handler;

import static org.openhab.binding.northq.NorthQBindingConstants.*;

import java.util.ArrayList;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.northq.internal.common.NorthQConfig;
import org.openhab.binding.northq.internal.common.ReadWriteLock;
import org.openhab.binding.northq.internal.model.NGateway;
import org.openhab.binding.northq.internal.model.Qsettings;
import org.openhab.binding.northq.internal.model.Thing;

public class SettingsHandler extends BaseThingHandler {

    public SettingsHandler(org.eclipse.smarthome.core.thing.Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        try {
            ReadWriteLock.getInstance().lockRead();
            Qsettings qSet = getQsettings();
            String temperature = command.toString();

            if (qSet == null) {
                updateStatus(ThingStatus.OFFLINE);
                return;
            } else {
                updateStatus(ThingStatus.ONLINE);
            }

            if (channelUID.getId().equals(CHANNEL_SETTINGS_TOGGLEHEATLOCATION)) {
                if (command.toString().equals("ON")) {
                    qSet.getQsettings().toggleHeatOnLocation = true;

                } else if (command.toString().equals("OFF")) {
                    qSet.getQsettings().toggleHeatOnLocation = false;
                }
            }
            if (channelUID.getId().equals(CHANNEL_SETTINGS_ISHOMETEMP)) {
                if (command.toString() != null) {
                    qSet.getQsettings().isHomeTemp = Float.valueOf(temperature);
                }
            }
            if (channelUID.getId().equals(CHANNEL_SETTINGS_NOTHOMETEMP)) {
                if (command.toString() != null) {
                    qSet.getQsettings().notHomeTemp = Float.valueOf(temperature);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ReadWriteLock.getInstance().unlockWrite();
        }
    }

    /**
     * Abstract method overwritten
     * Requires:
     * Returns: Scheduled jobs and removes thing
     */
    @Override
    public void handleRemoval() {
        updateStatus(ThingStatus.REMOVED);
    }

    /**
     * Abstract method overwritten
     * Requires:
     * Returns: Initialiser
     */
    @Override
    public void initialize() {
        updateStatus(ThingStatus.ONLINE);
    }

    public @Nullable Qsettings getQsettings() {
        ArrayList<NGateway> gateways = NorthQConfig.NETWORK.getGateways();
        for (NGateway gw : gateways) {
            ArrayList<Thing> things = gw.getThings();
            for (int i = 0; i < things.size(); i++) {
                if (things.get(i) instanceof Qsettings) {
                    Qsettings qSettings = (Qsettings) things.get(i);
                    return qSettings;
                }
            }
        }
        return null;
    }

}
