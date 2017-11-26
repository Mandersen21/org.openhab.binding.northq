/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.northq.internal.mock.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import org.openhab.binding.northq.internal.common.NorthQConfig;
import org.openhab.binding.northq.internal.mock.MockFactory;
import org.openhab.binding.northq.internal.mock.NorthQMockNetwork;
import org.openhab.binding.northq.internal.model.NGateway;
import org.openhab.binding.northq.internal.model.NorthNetwork;
import org.openhab.binding.northq.internal.model.Thing;

/**
 * The {@link NorthQMockNetwork} is responsible generating a mock network
 * that allows us to simulate multiple scenarios.
 *
 * @author Nicolaj - Initial contribution
 */
public class MockGui extends JFrame {
    // MainPanel
    private JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
    // OverviewPanel
    private JPanel overviewPanel = new JPanel(new BorderLayout());
    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> overviewList = new JList<String>(listModel);

    // ConfigPanels
    private JPanel configPanel = new JPanel(new BorderLayout());
    private JPanel addPanel = new JPanel(new GridBagLayout());
    private JPanel settingsPanel = new JPanel(new GridBagLayout());

    private String[] deviceNames = { "Q Plug", "Q Motion", "Q Thermostat", "Gateway" };
    private JComboBox<String> addDropDown = new JComboBox<String>(deviceNames);
    private String[] gatewaysChoice = NorthQConfig.getMOCK_NETWORK().getNetwork().toStringArray();
    private JComboBox<String> chooseGateway = new JComboBox<String>(gatewaysChoice);
    private JButton addButton = new JButton("Add Selection");

    public MockGui() {

        super();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(500, 500));
        setResizable(true);

        overviewList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        overviewList.setLayoutOrientation(JList.VERTICAL);
        overviewList.setVisible(true);
        addNetworkToOverview();

        // OverviewPanel Adds
        overviewPanel.setPreferredSize(new Dimension(250, 500));
        overviewPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        overviewPanel.add(overviewList);

        // AddPanel
        addPanel.setPreferredSize(new Dimension(250, 250));
        addPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = 2;
        constraints.gridx = 0;
        constraints.gridy = 0;
        addPanel.add(addDropDown, constraints);

        addButton.addActionListener(new mockAddButtonListener());
        constraints.gridwidth = 1;
        constraints.gridx = 2;
        constraints.gridy = 0;
        addPanel.add(addButton, constraints);

        constraints.gridwidth = 1;
        constraints.gridx = 1;
        constraints.gridy = 1;
        addPanel.add(chooseGateway, constraints);

        // SettingsPanel
        settingsPanel.setPreferredSize(new Dimension(250, 250));
        settingsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Config Panel
        configPanel.setPreferredSize(new Dimension(250, 500));
        configPanel.add(addPanel, BorderLayout.NORTH);
        configPanel.add(settingsPanel, BorderLayout.SOUTH);

        // MainPanel Adds
        mainPanel.add(overviewPanel, BorderLayout.EAST);
        mainPanel.add(configPanel, BorderLayout.WEST);
        add(mainPanel);

    }

    private void addNetworkToOverview() {
        NorthNetwork network = NorthQConfig.getMOCK_NETWORK().getNetwork();
        ArrayList<NGateway> gateways = network.getGateways();
        if (network != null) {
            for (int i = 0; i < gateways.size(); i++) {
                listModel.addElement(
                        gateways.get(i).toString().substring(42, gateways.get(i).toString().length() - 1).split("@")[0]
                                + " " + (i + 1));

                ArrayList<Thing> things = gateways.get(i).getThings();

                for (int j = 0; j < things.size(); j++) {
                    listModel.addElement(
                            things.get(j).toString().substring(42, things.get(j).toString().length() - 1).split("@")[0]
                                    + " " + (i + 1) + "." + (j + 1));
                }
            }
        }
    }

    // ActionListener to add things to the mock network
    class mockAddButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            NorthNetwork network = NorthQConfig.getMOCK_NETWORK().getNetwork();
            String thingSelection = addDropDown.getSelectedItem().toString();
            String gatewaySelection = chooseGateway.getSelectedItem().toString();

            NGateway gateway = network.getGateways().get(Integer.valueOf(gatewaySelection.split(" ")[1]) - 1);
            if (network != null) {
                if (thingSelection.equals("Q Plug")) {
                    gateway.addThing(MockFactory.createQplug());

                } else if (thingSelection.equals("Q Motion")) {
                    gateway.addThing(MockFactory.createQmotion());

                } else if (thingSelection.equals("Q Thermostat")) {
                    gateway.addThing(MockFactory.createQthermostat());

                } else if (thingSelection.equals("Gateway")) {
                    int newGatewayId = Integer.valueOf(gateway.getGatewayId()) + 1111;
                    MockFactory.createGateway("000000" + newGatewayId + "");
                }
            }
            overviewPanel.removeAll();
            listModel.clear();
            addNetworkToOverview();
            overviewPanel.add(overviewList);
            overviewPanel.revalidate();
            overviewPanel.repaint();

        }

    }
}
