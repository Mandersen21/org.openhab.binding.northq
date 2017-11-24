/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.northq.handler;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.core.Form;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.northq.NorthQBindingConstants;
import org.openhab.binding.northq.internal.common.NorthQConfig;
import org.openhab.binding.northq.internal.services.NetworkUtils;
import org.openhab.binding.northq.internal.services.NorthqServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link NorthQPhoneHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author DTU_02162_group03 - Initial contribution
 */

@NonNullByDefault
public class NorthQPhoneHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(NorthQPhoneHandler.class);

    private NorthqServices services;
    private boolean phoneEnabledStatus;
    public String location = "0";
    public String locationStatus = "home";

    private String sqlUser = "root";
    private String sqlPassword = "changeme";

    private final byte[] keyValue = "Beercalc12DTU123".getBytes(); // TODO: move to password file

    private ScheduledFuture<?> pollingJob;
    private Runnable pollingRunnable = new Runnable() {

        @Override
        public void run() {
            scheduleCode();

        }
    };

    public NorthQPhoneHandler(Thing thing) {
        super(thing);

        phoneEnabledStatus = false;
        services = new NorthqServices();
        pollingJob = scheduler.scheduleWithFixedDelay(pollingRunnable, 1, 5, TimeUnit.SECONDS);

    }

    /**
     * Abstract method overwritten
     * Requires:
     * Returns: Initialisation method
     */
    @Override
    public void initialize() {
        updateStatus(ThingStatus.ONLINE);

        // New code for later release, uncomment when tested
        // register database tracking
        Connection conn;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:Mysql://localhost:3306", sqlUser, sqlPassword);
            PreparedStatement createStatement = null;
            createStatement = conn.prepareStatement(
                    "insert ignore into gpsapp.registeredgpsusers (`username`, `homelocation`) values (?,?);");
            createStatement.setString(1, getThing().getConfiguration().get("name").toString());
            createStatement.setString(2, getThing().getConfiguration().get("homelocation").toString());
            createStatement.executeQuery();
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

    }

    /**
     * Abstract method overwritten
     * Requires: a channelId and a command
     * Returns: Updates the state of the device
     */
    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (channelUID.getId().equals(NorthQBindingConstants.CHANNEL_QPHONE)) {
            if (command.toString().equals("ON")) {
                phoneEnabledStatus = true;
            } else if (command.toString().equals("OFF")) {
                NorthQConfig.setISHOME(true);
                phoneEnabledStatus = false;
            }
        }
    }

    /**
     * Abstract method overwritten
     * Requires:
     * Returns: Scheduled jobs and removes thing
     */
    @Override
    public void handleRemoval() {
        if (pollingJob != null && !pollingJob.isCancelled()) {
            pollingJob.cancel(true);
        }
        // New code for later release, uncomment when tested
        // unregister database tracking
        Connection conn;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:Mysql://localhost:3306", sqlUser, sqlPassword);
            PreparedStatement createStatement = null;
            createStatement = conn
                    .prepareStatement("delete from gpsapp.registeredgpsusers where registeredgpsusers.Username = ?;");
            createStatement.setString(1, getThing().getConfiguration().get("name").toString());
            createStatement.executeQuery();
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // remove thing
        updateStatus(ThingStatus.REMOVED);
    }

    // Requires: A cipher text string
    // Returns: The decrypted plain text of text string
    public @Nullable String decrypt(String cipherText) {
        try {
            Cipher AesCipher;
            AesCipher = Cipher.getInstance("AES");
            AesCipher.init(Cipher.DECRYPT_MODE, generateKey());

            return new String(AesCipher.doFinal(Base64.getDecoder().decode(cipherText.getBytes())));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
                | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Requires:
    // Returns: generates the AES key
    private Key generateKey() {
        Key key = new SecretKeySpec(keyValue, "AES");
        return key;
    }

    /**
     * Requires:
     * Returns: updates the thing, when run
     */
    private void scheduleCode() {
        System.out.println("Polling data for phone");

        Form form = new Form();
        form.param("getGPS", NorthQConfig.getUSERNAME());
        form.param("name", getThing().getConfiguration().get("name").toString());
        NetworkUtils nu = new NetworkUtils();

        try {
            if (phoneEnabledStatus) {
                String raw = "";
                // New code for later release, uncomment when tested
                Connection conn;
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    conn = DriverManager.getConnection("jdbc:Mysql://localhost:3306", sqlUser, sqlPassword);
                    PreparedStatement createStatement = null;
                    createStatement = conn.prepareStatement(
                            "select * from `gpsapp`.`gpsdata` where  `user` = ? ORDER BY stamp DESC LIMIT 1;");
                    createStatement.setString(1, getThing().getConfiguration().get("name").toString());
                    ResultSet rs = null;
                    rs = createStatement.executeQuery();
                    while (rs.next()) {
                        raw = rs.getString("gpscords");
                        // if older then 30 set offline and update to being home as no data is avaliable and we assume
                        // home /
                        // phone power out
                        if (rs.getTimestamp("stamp").after(Timestamp.valueOf(LocalDateTime.now().minusMinutes(30)))) {
                            updateStatus(ThingStatus.ONLINE);
                        } else {
                            updateStatus(ThingStatus.OFFLINE);
                            NorthQConfig.getPHONE_MAP().put(getThing().getConfiguration().get("name").toString(), true);
                            return;

                        }

                    }
                } catch (Exception e) {
                    System.out.println(e.getClass().getName() + ": " + e.getMessage());
                }

                // Response res = nu.getHttpPostResponse(NorthQBindingConstants.GPS_SERVICE_ADDRESS, form);
                // raw = res.readEntity(String.class).replaceAll("\\r|\\n", "");
                if (raw.equals("")) {
                    return;
                }
                String decrypted = decrypt(raw);
                // 1 or 0 ; home | work |
                String[] data = decrypted.split(";");
                String locationStatus = data[0];
                String location = data[1];

                String result = String.valueOf(decrypted);

                // res.close();
                boolean resBol;
                if (!NorthQPhoneHandler.this.location.equals("Home")
                        && NorthQPhoneHandler.this.locationStatus.equals("1") && !location.equals("home")
                        && locationStatus.equals("0")) {
                    resBol = true;
                } else if (location.equals("Home")) {
                    resBol = true;
                } else {
                    resBol = false;
                }

                NorthQPhoneHandler.this.location = location;
                NorthQPhoneHandler.this.locationStatus = locationStatus;

                Boolean isHome = new Boolean(resBol);
                // Updated displayed name
                updateState(NorthQBindingConstants.CHANNEL_QPHONE_GPSLOCATION,
                        StringType.valueOf(isHome ? "Home" : "Out"));

                NorthQConfig.getPHONE_MAP().put(getThing().getConfiguration().get("name").toString(), isHome);

                Boolean[] phoneHome = new Boolean[NorthQConfig.getPHONE_MAP().values().toArray().length];

                NorthQConfig.getPHONE_MAP().values().toArray(phoneHome);
                boolean allAway = true;
                for (Boolean b : phoneHome) {
                    boolean bol = b.booleanValue();
                    if (bol) {
                        allAway = false;
                    }
                }
                if (phoneEnabledStatus && allAway) {
                    // turn off device
                    NorthQConfig.setISHOME(false);
                } // If home
                else if (phoneEnabledStatus && !allAway) {
                    NorthQConfig.setISHOME(true);
                }
            } else {
                updateState(NorthQBindingConstants.CHANNEL_QPHONE_GPSLOCATION, StringType.valueOf("Inactive"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
