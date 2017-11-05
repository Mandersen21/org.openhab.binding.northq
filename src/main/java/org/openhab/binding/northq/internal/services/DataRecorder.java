/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.northq.internal.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.openhab.binding.northq.handler.NorthQNetworkHandler;

/**
 * The {@link NorthQNetworkHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author DTU_02162_group03 - Initial contribution
 */
public class DataRecorder {
    private Connection con;
    private String sqlUser = "root";
    private String sqlPass = "changeme";

    public DataRecorder() {
    }

    public boolean open() {
        boolean isConOpen = false;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:Mysql://localhost:3306", sqlUser, sqlPass);

            con.setAutoCommit(false);
            isConOpen = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isConOpen;
    }

    public void close() {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Add Events to database table MotionEvents
    public boolean addEvent(int nodeId) {
        boolean result = false;
        PreparedStatement statement = null;

        try {
            statement = con
                    .prepareStatement("INSERT INTO 'dtu3db'.'MotionEvents' ('node_id','Event') VALUES (?,NOW());");
            statement.setInt(1, nodeId);

            con.commit();

            result = true;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                statement.close();
            } catch (Exception e) {
            }
        }
        return result;

    }

    // Add power consumption to database table PowerConsumption
    public boolean addPowerCon(int nodeId, float power) {
        boolean result = false;
        PreparedStatement statement = null;

        try {
            statement = con
                    .prepareStatement("INSERT INTO 'dtu3db'.'PowerConsumption' ('node_id','PowerCon') VALUES (?,?);");
            statement.setInt(1, nodeId);
            statement.setFloat(2, power);

            con.commit();

            result = true;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                statement.close();
            } catch (Exception e) {
            }
        }
        return result;
    }

    // Add temperature to database table Temperature
    public boolean addTemp(int nodeId, float temp) {
        boolean result = false;
        PreparedStatement statement = null;

        try {
            statement = con // TODO
                    .prepareStatement("INSERT INTO 'dtu3db'.'Temperature' ('node_id','Temp') VALUES (?,?);");
            statement.setInt(1, nodeId);
            statement.setFloat(2, temp);

            con.commit();

            result = true;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                statement.close();
            } catch (Exception e) {
            }
        }
        return result;
    }

    // Add Humidity to database table Humidity
    public boolean addHumidity(int nodeId, float humidity) {
        boolean result = false;
        PreparedStatement statement = null;

        try {
            statement = con // TODO
                    .prepareStatement("INSERT INTO 'dtu3db'.'Humidity' ('node_id','Humid') VALUES (?,?);");
            statement.setInt(1, nodeId);
            statement.setFloat(2, humidity);

            con.commit();

            result = true;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                statement.close();
            } catch (Exception e) {
            }
        }
        return result;
    }

    // Add light intensity to database table Light
    public boolean addLight(int nodeId, int light) {
        boolean result = false;
        PreparedStatement statement = null;

        try {
            statement = con.prepareStatement("INSERT INTO 'dtu3db'.'LightIntensity' ('node_id','Light') VALUES (?,?);");
            statement.setInt(1, nodeId);
            statement.setInt(2, light);

            con.commit();

            result = true;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                statement.close();
            } catch (Exception e) {
            }
        }
        return result;
    }
}
