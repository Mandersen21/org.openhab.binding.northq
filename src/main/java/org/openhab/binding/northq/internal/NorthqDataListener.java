/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.northq.internal;

/**
 * The {@link NorthqDataListener} is notified by the bridge thing handler
 *
 * @author DTU team C
 */
public interface NorthqDataListener {

    /**
     * When data is fetched check for devices
     */
    public void onDataFetched();
}
