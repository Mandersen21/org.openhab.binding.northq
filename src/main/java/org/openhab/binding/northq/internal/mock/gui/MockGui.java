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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.openhab.binding.northq.internal.common.NorthQConfig;
import org.openhab.binding.northq.internal.mock.MockFactory;
import org.openhab.binding.northq.internal.mock.NorthQMockNetwork;
import org.openhab.binding.northq.internal.model.NGateway;
import org.openhab.binding.northq.internal.model.NorthNetwork;
import org.openhab.binding.northq.internal.model.Qmotion;
import org.openhab.binding.northq.internal.model.Qplug;
import org.openhab.binding.northq.internal.model.Qthermostat;
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
    private JPanel settingsPanel = new JPanel();

    // Add Panel
    private String[] deviceNames = { "Q Plug", "Q Motion", "Q Thermostat", "NGateway" };
    private JComboBox<String> addDropDown = new JComboBox<String>(deviceNames);
    private String[] gatewaysChoice;
    private JComboBox<String> chooseGateway;
    private JButton addButton = new JButton("Add");
    private JButton deleteButton = new JButton("Delete");

    // Settings Panels

    // Plug Panel
    private JPanel plugPanel = new JPanel();
    private JTextField statusField = new JTextField();
    private JLabel statusLabel = new JLabel("Plug Status:");
    private JTextField powerconField = new JTextField();
    private JLabel powerconLabel = new JLabel("Power consumption:");
    private JButton submitPlugButton = new JButton("Submit");

    // Motion Panel
    private JPanel motionPanel = new JPanel();
    private JTextField armedField = new JTextField();
    private JLabel armedLabel = new JLabel("Armed/Disarmed:");
    private JTextField temperatureField = new JTextField();
    private JLabel temperatureLabel = new JLabel("Temperature:");
    private JTextField humidityField = new JTextField();
    private JLabel humidityLabel = new JLabel("Humidity:");
    private JTextField lightField = new JTextField();
    private JLabel lightLabel = new JLabel("Light:");
    private JTextField batteryMotionField = new JTextField();
    private JLabel batteryMotionLabel = new JLabel("Battery:");
    private JButton submitMotionButton = new JButton("Submit");

    // Thermostat Panel
    private JPanel thermostatPanel = new JPanel();
    private JTextField temperatureTherField = new JTextField();
    private JLabel temperatureTherLabel = new JLabel("Temperature:");
    private JTextField batteryTherField = new JTextField();
    private JLabel batteryTherLabel = new JLabel("Battery:");
    private JButton submitThermostatButton = new JButton("Submit");

    public MockGui() {
        super();
        // Change Mock to false when gui is closed
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                NorthQConfig.setMOCK(false);
            }

            @Override
            public void windowClosing(WindowEvent e) {
                NorthQConfig.setMOCK(false);
            }
        });

        setSize(new Dimension(500, 500));
        setResizable(false);

        // The list panel
        overviewPanel();

        // The panel to add new things
        addPanel();

        // The different thing panels for selection of a thing in the list
        plugPanel();
        motionPanel();
        thermostatPanel();

        // The two main panels of the left part of the gui
        settingsPanel();
        configPanel();

        // The main panel
        mainPanel();
    }

    // Panels configuration -- Start

    public void overviewPanel() {
        // OverviewPanel Adds
        overviewList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        overviewList.setLayoutOrientation(JList.VERTICAL);
        overviewList.setVisible(true);
        overviewList.addListSelectionListener(new mockListSelectionlistener());

        addNetworkToOverview();

        overviewPanel.setPreferredSize(new Dimension(250, 500));
        overviewPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        overviewPanel.add(overviewList);
    }

    public void addPanel() {
        // AddPanel
        addPanel.setPreferredSize(new Dimension(250, 250));
        addPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        gatewaysChoice = NorthQConfig.getMOCK_NETWORK().getNetwork().toStringArray();
        chooseGateway = new JComboBox<String>(gatewaysChoice);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = 1;
        constraints.gridx = 1;
        constraints.gridy = 0;
        addPanel.add(addDropDown, constraints);

        addButton.addActionListener(new mockAddButtonListener());
        constraints.gridx = 2;
        constraints.gridy = 0;
        addPanel.add(addButton, constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        addPanel.add(chooseGateway, constraints);

        deleteButton.addActionListener(new mockDeleteButtonListener());
        constraints.gridx = 2;
        constraints.gridy = 1;
        addPanel.add(deleteButton, constraints);
    }

    public void plugPanel() {
        // PlugPanel
        plugPanel.setLayout(new BoxLayout(plugPanel, BoxLayout.Y_AXIS));

        statusLabel.setLabelFor(statusField);
        plugPanel.add(statusLabel);
        plugPanel.add(statusField);

        powerconLabel.setLabelFor(powerconField);
        plugPanel.add(powerconLabel);
        plugPanel.add(powerconField);

        submitPlugButton.addActionListener(new mockSubmitButtonListener());
        plugPanel.add(submitPlugButton);

    }

    public void motionPanel() {
        // Motion panel
        motionPanel.setLayout(new BoxLayout(motionPanel, BoxLayout.Y_AXIS));

        armedLabel.setLabelFor(armedField);
        motionPanel.add(armedLabel);
        motionPanel.add(armedField);

        temperatureLabel.setLabelFor(temperatureField);
        motionPanel.add(temperatureLabel);
        motionPanel.add(temperatureField);

        lightLabel.setLabelFor(lightField);
        motionPanel.add(lightLabel);
        motionPanel.add(lightField);

        humidityLabel.setLabelFor(humidityField);
        motionPanel.add(humidityLabel);
        motionPanel.add(humidityField);

        batteryMotionLabel.setLabelFor(batteryMotionField);
        motionPanel.add(batteryMotionLabel);
        motionPanel.add(batteryMotionField);

        submitMotionButton.addActionListener(new mockSubmitButtonListener());
        motionPanel.add(submitMotionButton);
    }

    public void thermostatPanel() {
        // Thermostat panel
        thermostatPanel.setLayout(new BoxLayout(thermostatPanel, BoxLayout.Y_AXIS));

        temperatureTherLabel.setLabelFor(temperatureTherField);
        thermostatPanel.add(temperatureTherLabel);
        thermostatPanel.add(temperatureTherField);

        batteryTherLabel.setLabelFor(batteryTherField);
        thermostatPanel.add(batteryTherLabel);
        thermostatPanel.add(batteryTherField);

        submitThermostatButton.addActionListener(new mockSubmitButtonListener());
        thermostatPanel.add(submitThermostatButton);
    }

    public void phonePanel() {

    }

    public void settingsPanel() {
        // SettingsPanel
        settingsPanel.setPreferredSize(new Dimension(250, 250));
        settingsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
    }

    public void configPanel() {
        // Config Panel
        configPanel.setPreferredSize(new Dimension(250, 500));
        configPanel.add(addPanel, BorderLayout.NORTH);
        configPanel.add(settingsPanel, BorderLayout.SOUTH);
    }

    public void mainPanel() {
        // MainPanel Adds
        mainPanel.add(overviewPanel, BorderLayout.EAST);
        mainPanel.add(configPanel, BorderLayout.WEST);
        add(mainPanel);
    }
    // Panels configuration -- End

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

    // ActionListener to change values in the mock network
    class mockSubmitButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            NorthNetwork network = NorthQConfig.getMOCK_NETWORK().getNetwork();
            String thingSelection = overviewList.getSelectedValue();
            ArrayList<NGateway> gateways = network.getGateways();

            int gatewayNumber = -1;
            int thingNumber = -1;

            if (network != null) {
                gatewayNumber = Integer.valueOf(
                        thingSelection.substring(thingSelection.indexOf(" ") + 1, thingSelection.indexOf("."))) - 1;

                thingNumber = Integer.valueOf(thingSelection.substring(thingSelection.indexOf(".") + 1)) - 1;

                if (gateways.size() > gatewayNumber) {

                    NGateway ngate = gateways.get(gatewayNumber);
                    Thing thing = ngate.getThings().get(thingNumber);

                    String thingtype = thing.toString().substring(42, thing.toString().length() - 1).split("@")[0];

                    if (thingtype.equals("Qplug")) {
                        Qplug plug = (Qplug) ngate.getThings().get(thingNumber);

                        plug.getBs().pos = Integer.valueOf(statusField.getText());
                        plug.getBs().wattage = Float.valueOf(powerconField.getText());
                    }
                    if (thingtype.equals("Qmotion")) {
                        Qmotion motion = (Qmotion) ngate.getThings().get(thingNumber);

                        motion.getBs().pos = Integer.valueOf(armedField.getText());
                        motion.getBs().battery = Integer.valueOf(batteryMotionField.getText());
                        motion.getBs().sensors.get(0).value = Float.valueOf(temperatureField.getText());
                        motion.getBs().sensors.get(1).value = Float.valueOf(lightField.getText());
                        motion.getBs().sensors.get(2).value = Float.valueOf(humidityField.getText());
                    }
                    if (thingtype.equals("Qthermostat")) {
                        Qthermostat thermostat = (Qthermostat) ngate.getThings().get(thingNumber);

                        thermostat.getTher().temperature = Float.valueOf(temperatureTherField.getText());
                        thermostat.getTher().battery = Integer.valueOf(batteryTherField.getText());
                    }
                }
            }
        }
    }

    // Add ButtonListener for the list on the right. Each item.
    class mockListSelectionlistener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            NorthNetwork network = NorthQConfig.getMOCK_NETWORK().getNetwork();
            String thingSelection = overviewList.getSelectedValue();
            ArrayList<NGateway> gateways = network.getGateways();

            int gatewayNumber = -1;
            int thingNumber = -1;

            if (network != null && thingSelection != null) {

                if (thingSelection.substring(0, thingSelection.indexOf(" ")).equals("NGateway")) {
                    settingsPanel.removeAll();
                    settingsPanel.revalidate();
                    settingsPanel.repaint();

                } else {
                    gatewayNumber = Integer.valueOf(
                            thingSelection.substring(thingSelection.indexOf(" ") + 1, thingSelection.indexOf("."))) - 1;

                    thingNumber = Integer.valueOf(thingSelection.substring(thingSelection.indexOf(".") + 1)) - 1;

                    if (gateways.size() > gatewayNumber) {
                        NGateway ngate = gateways.get(gatewayNumber);
                        Thing thing = ngate.getThings().get(thingNumber);

                        String thingtype = thing.toString().substring(42, thing.toString().length() - 1).split("@")[0];

                        if (thingtype.equals("Qplug")) {
                            Qplug plug = (Qplug) ngate.getThings().get(thingNumber);

                            statusField.setText(String.valueOf(plug.getBs().pos));
                            powerconField.setText(String.valueOf(plug.getPowerConsumption()));

                            settingsPanel.removeAll();
                            settingsPanel.add(plugPanel);
                            settingsPanel.revalidate();
                            settingsPanel.repaint();

                        }
                        if (thingtype.equals("Qmotion")) {
                            Qmotion motion = (Qmotion) ngate.getThings().get(thingNumber);

                            armedField.setText(String.valueOf(motion.getBs().pos));
                            batteryMotionField.setText(String.valueOf(motion.getBattery()));
                            temperatureField.setText(String.valueOf(motion.getTmp()));
                            lightField.setText(String.valueOf(motion.getLight()));
                            humidityField.setText(String.valueOf(motion.getHumidity()));

                            settingsPanel.removeAll();
                            settingsPanel.add(motionPanel);
                            settingsPanel.revalidate();
                            settingsPanel.repaint();
                        }
                        if (thingtype.equals("Qthermostat")) {
                            Qthermostat thermostat = (Qthermostat) ngate.getThings().get(thingNumber);

                            batteryTherField.setText(String.valueOf(thermostat.getBattery()));
                            temperatureTherField.setText(String.valueOf(thermostat.getTemp()));

                            settingsPanel.removeAll();
                            settingsPanel.add(thermostatPanel);
                            settingsPanel.revalidate();
                            settingsPanel.repaint();
                        }
                    }
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

                } else if (thingSelection.equals("NGateway")) {
                    int newGatewayId = Integer.valueOf(gateway.getGatewayId()) + 1111;

                    NGateway gatewayAdd = MockFactory.createGateway("000000" + newGatewayId + "");
                    ArrayList<NGateway> gatewaysAdd = network.getGateways();
                    gatewaysAdd.add(gatewayAdd);

                    NorthQConfig.getMOCK_NETWORK().getNetwork().setGateways(gatewaysAdd);
                }
            }
            overviewPanel.removeAll();
            listModel.clear();
            addNetworkToOverview();
            overviewPanel.add(overviewList);
            overviewPanel.revalidate();
            overviewPanel.repaint();

            addPanel.removeAll();
            addPanel();
            addPanel.revalidate();
            addPanel.repaint();
        }
    }

    // Add ButtonListener for the list on the right. Each item.
    class mockDeleteButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            NorthNetwork network = NorthQConfig.getMOCK_NETWORK().getNetwork();
            String thingSelection = overviewList.getSelectedValue();
            ArrayList<NGateway> gateways = network.getGateways();

            int gatewayNumber = -1;
            int thingNumber = -1;

            if (network != null && thingSelection != null) {

                if (thingSelection.substring(0, thingSelection.indexOf(" ")).equals("NGateway")) {
                    gatewayNumber = Integer.valueOf(thingSelection.substring(thingSelection.indexOf(" ") + 1)) - 1;
                    network.getGateways().remove(gatewayNumber);

                } else {
                    gatewayNumber = Integer.valueOf(
                            thingSelection.substring(thingSelection.indexOf(" ") + 1, thingSelection.indexOf("."))) - 1;

                    thingNumber = Integer.valueOf(thingSelection.substring(thingSelection.indexOf(".") + 1)) - 1;

                    if (gateways.size() > gatewayNumber) {
                        NGateway ngate = gateways.get(gatewayNumber);
                        ngate.getThings().remove(thingNumber);

                    }
                }
            }
            overviewPanel.removeAll();
            listModel.clear();
            addNetworkToOverview();
            overviewPanel.add(overviewList);
            overviewPanel.revalidate();
            overviewPanel.repaint();

            addPanel.removeAll();
            addPanel();
            addPanel.revalidate();
            addPanel.repaint();
        }
    }
}
