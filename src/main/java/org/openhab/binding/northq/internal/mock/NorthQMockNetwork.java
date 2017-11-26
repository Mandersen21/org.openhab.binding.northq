/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.northq.internal.mock;

import java.util.ArrayList;

import org.openhab.binding.northq.internal.model.NGateway;
import org.openhab.binding.northq.internal.model.NorthNetwork;
import org.openhab.binding.northq.internal.model.json.House;

/**
 * The {@link NorthQMockNetwork} is responsible generating a mock network
 * that allows us to simulate multiple scenarios.
 *
 * @author Nicolaj - Initial contribution
 */
public class NorthQMockNetwork {

    private NorthNetwork mockNetwork;

    public NorthQMockNetwork() {
        String mockToken = "mock_token";
        String muckUserId = "0000";
        House[] mockHouses = null;
        ArrayList<NGateway> mockGates = new ArrayList<NGateway>();

        mockNetwork = new NorthNetwork(mockToken, muckUserId, mockHouses, mockGates);

        NGateway gate1 = MockFactory.createGateway("0000001111");
        gate1.addThing(MockFactory.createQplug());
        gate1.addThing(MockFactory.createQplug());
        gate1.addThing(MockFactory.createQmotion());
        gate1.addThing(MockFactory.createQthermostat());
        gate1.addThing(MockFactory.createQplug());
        gate1.addThing(MockFactory.createQplug());

        mockGates.add(gate1);

        NGateway gate2 = MockFactory.createGateway("0000002222");
        gate2.addThing(MockFactory.createQmotion());
        gate2.addThing(MockFactory.createQthermostat());
        gate2.addThing(MockFactory.createQplug());
        gate2.addThing(MockFactory.createQplug());

        mockGates.add(gate2);

    }

    public NorthNetwork getNetwork() {
        return mockNetwork;
    }

}
