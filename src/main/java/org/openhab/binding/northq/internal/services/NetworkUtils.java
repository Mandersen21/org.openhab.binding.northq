/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openhab.binding.northq.internal.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openhab.binding.northq.handler.NorthQNetworkHandler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * The {@link NorthQNetworkHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author DTU_02162_group03 - Initial contribution
 */

public class NetworkUtils {

    // Requires: A URL target for a post http(s) service and a Form object containing all parameters
    // Returns: A JSON string containing the json response
    public Response getHttpPostResponse(String target, Form parameters) throws IOException, Exception {
        Client client = ClientBuilder.newBuilder().build();
        WebTarget webResource = client.target(target);
        Response clientResponse = webResource.request()
                .post(Entity.entity(parameters, MediaType.APPLICATION_FORM_URLENCODED));
        return clientResponse;
    }

    // Requires: A URL target for a post http(s) service and a Form object containing all parameters
    // Returns: A JSON string containing the json response
    public Response getHttpGetResponse(String target) throws IOException, Exception {
        Client client = ClientBuilder.newBuilder().build();
        WebTarget webResource = client.target(target);
        Response clientResponse = webResource.request().get();
        return clientResponse;
    }

    // Requires: a JSON formatted string
    // Returns: A map consisting of objects translated from JSON
    public Map<String, Object> getJsonMap(String jsonString) throws IOException {
        return new Gson().fromJson(jsonString, new TypeToken<HashMap<String, Object>>() {
        }.getType());
    }

}
