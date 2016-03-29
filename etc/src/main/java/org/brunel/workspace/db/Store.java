/*
 * Copyright (c) 2016 IBM Corporation and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.brunel.workspace.db;

import org.brunel.workspace.BrunelWorkspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Keeps track of all stored data
 */
public class Store {

    private static final Logger logger = LoggerFactory.getLogger(BrunelWorkspace.class);

    private final Connection connection;

    public Store() {

        File base = new File(System.getProperty("user.home"), "brunelWorkshop.db");

        try {
            Class.forName("org.sqlite.JDBC");
            String url = base.toURI().toASCIIString();
            url = url.replace("file:", "jdbc:sqlite:");
            connection = DriverManager.getConnection(url);
        } catch (ClassNotFoundException e) {
            logger.error("No class found for SQL connection", e);
            throw new RuntimeException(e);
        } catch (SQLException e) {
            logger.error("Could not open connection to database", e);
            throw new RuntimeException(e);

        }
        logger.info("Opened database " + base.getName());

    }

    public void addToTable(String tableName, Storable storable) {
        Object[] values = storable.toStorableObjects();
        String sql = makePreparedCommand(tableName, values);
        logger.debug("Adding to table with command: " + sql);
        try {
            PreparedStatement s = connection.prepareStatement(sql);
            for (int i = 0; i < values.length; i++)
                setFieldValue(s, i + 1, values[i]);
            s.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getTableSize(String tableName) {
        try {
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM " + tableName);
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeFromTable(String tableName, Storable item) {
        Object itemID = item.toStorableObjects()[0];
        execute("DELETE FROM " + tableName + " WHERE ID='" + itemID + "'");
    }

    public List<Storable> retrieve(String tableName, Storable representative) {
        ArrayList<Storable> list = new ArrayList<>();
        try {
            logger.debug("Reading items from: " + tableName);
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * from " + tableName + ";");

            while (rs.next()) {
                Storable item = representative.retrieve(rs);
                logger.trace("Read " + item);
                list.add(item);
            }
            rs.close();
            stmt.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    private String makePreparedCommand(String tableName, Object[] values) {
        StringBuilder b = new StringBuilder();
        b.append("INSERT INTO ").append(tableName).append(" VALUES (");
        for (int i = 0; i < values.length; i++) {
            if (i > 0) b.append(',');
            b.append('?');
        }
        b.append(");");
        return b.toString();
    }

    public void execute(String command) {
        try {
            logger.debug("Executing SQL: " + command);
            Statement s = connection.createStatement();
            s.executeUpdate(command);
            s.close();
        } catch (SQLException e) {
            logger.error("Failed to execute SQL update command", e);
        }
    }

    private void setFieldValue(PreparedStatement statement, int index, Object o) throws SQLException {
        if (o instanceof String) statement.setString(index, (String) o);
        else if (o instanceof Integer) statement.setInt(index, (Integer) o);
        else if (o instanceof Double) statement.setDouble(index, (Double) o);
        else if (o instanceof Float) statement.setFloat(index, (Float) o);
        else if (o instanceof URL) statement.setURL(index, (URL) o);
        else if (o instanceof byte[]) statement.setBytes(index, (byte[]) o);
        else throw new IllegalArgumentException("SQL cannot store item: " + o);

    }
}
