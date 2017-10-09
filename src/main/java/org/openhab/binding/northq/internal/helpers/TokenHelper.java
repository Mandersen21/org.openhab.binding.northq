/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.northq.internal.helpers;

import static org.openhab.binding.northq.internal.common.NorthQConfig.TOKEN;

import model.json.User;
import services.NorthqServices;

/**
 * The {@link TokenHelper} is responsible for getting token.
 *
 * @author DTU_02162_group03 - Initial contribution
 */

public class TokenHelper {

    private NorthqServices services;

    public TokenHelper() {
        services = new NorthqServices();
    }

    // TODO: Implement timing when token should be changed, like update TOKEN every 2 day etc
    public String getToken(String username, String password) {
        if (TOKEN.isEmpty()) {
            User user = null;
            try {
                user = services.postLogin("dtu3", "dtu3");
                TOKEN = user.token;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return TOKEN;
    }
}
