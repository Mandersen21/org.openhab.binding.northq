/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openhab.binding.northq.internal.model;

/**
 * The {@link IThing} is the interface of northQ things
 *
 * @author Dan / Nicolaj - Initial contribution (from standalone java)
 */

public interface IThing {

    public String getNodeID();

    @Override
    public String toString();

    public boolean equals();
}
