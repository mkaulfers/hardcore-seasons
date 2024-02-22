package com.xtwistedx.storage;

import com.xtwistedx.models.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLHandler {
    private Config config;
    private Connection connection;

    public boolean isConnected() {
        return connection != null;
    }

    public void connect() throws SQLException {
        String host = config.mySQLConfig.host;
        int port = config.mySQLConfig.port;
        String database = config.mySQLConfig.database;

        String username = config.mySQLConfig.username;
        String password = config.mySQLConfig.password;

        String JDBC_URL = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false";

        connection = DriverManager.getConnection(JDBC_URL, username, password);
    }

    public void disconnect() {
        if(isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public SQLHandler(Config config) {
        this.config = config;
    }
}
