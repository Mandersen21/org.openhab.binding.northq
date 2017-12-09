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
 * The {@link Thing} is a pojo using the interface Ithing, implementing default thing methods
 *
 * @author Dan / Nicolaj - Initial contribution (from standalone java)
 */

public abstract class Thing implements IThing {

    @Override
    public boolean equals() {
        return false;
    }

}
