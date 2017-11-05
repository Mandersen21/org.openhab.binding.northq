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
import java.util.Base64;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
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
    private boolean status;

    private final byte[] keyValue = "Beercalc12DTU123".getBytes(); // TODO: move to password file

    private ScheduledFuture<?> pollingJob;
    private Runnable pollingRunnable = new Runnable() {

        @Override
        public void run() {
            System.out.println("Polling data for phone");

            Form form = new Form();
            form.param("getGPS", NorthQConfig.USERNAME);
            System.out.println(getThing().getConfiguration().get("name").toString());
            form.param("name", getThing().getConfiguration().get("name").toString());
            NetworkUtils nu = new NetworkUtils();

            try {
                if (status) {
                    Response res = nu.getHttpPostResponse(NorthQBindingConstants.GPS_SERVICE_ADDRESS, form);
                    String raw = res.readEntity(String.class).replaceAll("\\r|\\n", "");
                    // System.out.println("Raw: " + raw);
                    // String realraw = "0MfPkY+OLuFwVdlTsF+rEA";
                    // System.out.println("Expected: 0MfPkY+OLuFwVdlTsF+rEA");
                    // System.out.println("Matches: " + raw.equals(realraw));
                    String decrypted = decrypt(raw);

                    // System.out.println("decrypting string: " + raw);
                    // System.out.println("decrypted to: " + decrypted);

                    // String result = String.valueOf(res.readEntity(String.class).charAt(0));
                    String result = String.valueOf(decrypted);
                    System.out.println("Result =" + result);

                    res.close();
                    System.out.println("Boolean.parseBoolean(result) = " + Boolean.parseBoolean(result));
                    boolean resBol;
                    if (result.equals("0")) {
                        resBol = false;
                    } else {
                        resBol = true;
                    }
                    Boolean bres = new Boolean(resBol);
                    NorthQConfig.PHONE_MAP.put(getThing().getConfiguration().get("name").toString(), bres);

                    // If a new update comes in and the ishome is to be switched
                    // If not home
                    // System.out.println("away is set: " + result.equals("0"));
                    // System.out.println("away is set: " + result.equals("1"));

                    Boolean[] phoneHome = new Boolean[NorthQConfig.PHONE_MAP.values().toArray().length];

                    NorthQConfig.PHONE_MAP.values().toArray(phoneHome);
                    boolean allAway = true;
                    for (Boolean b : phoneHome) {
                        boolean bol = b.booleanValue();
                        System.out.println(bol);
                        if (bol) {
                            allAway = false;
                        }
                    }
                    System.out.println("All people are out: " + allAway);
                    if (status && allAway) {
                        // turn off device
                        NorthQConfig.setISHOME(false);
                        System.out.println("Set config to: " + NorthQConfig.ISHOME);
                    } // If home
                    else if (status && !allAway) {
                        NorthQConfig.setISHOME(true);
                        System.out.println("Set config to: " + NorthQConfig.ISHOME);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    public NorthQPhoneHandler(Thing thing) {
        super(thing);

        status = false;
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
    }

    /**
     * Abstract method overwritten
     * Requires: a channelId and a command
     * Returns: Updates the state of the device
     */
    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (channelUID.getId().equals(NorthQBindingConstants.CHANNEL_QPHONE)) {
            System.out.println("In enable GPS services");
            if (command.toString().equals("ON")) {
                status = true;
            } else if (command.toString().equals("OFF")) {
                NorthQConfig.setISHOME(true);
                status = false;
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
        // remove thing
        updateStatus(ThingStatus.REMOVED);
    }

    public @Nullable String decrypt(String cipherText) {
        try {
            Cipher AesCipher;
            AesCipher = Cipher.getInstance("AES");
            AesCipher.init(Cipher.DECRYPT_MODE, generateKey());
            System.out.println(AesCipher.doFinal(Base64.getDecoder().decode(cipherText.getBytes())).length);

            return new String(AesCipher.doFinal(Base64.getDecoder().decode(cipherText.getBytes())));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BadPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private Key generateKey() {
        Key key = new SecretKeySpec(keyValue, "AES");
        return key;
    }

}
