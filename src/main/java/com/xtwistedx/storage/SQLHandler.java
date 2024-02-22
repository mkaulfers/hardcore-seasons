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

    public void initializeDatabase() {
        if(!isConnected()) {
            throw new IllegalStateException("Database is not connected");
        }
    }

    /**
     * Create the user table in the database
     * @throws IllegalStateException if the database is not connected
     * User table should have the following columns:
     * - user_id (int, auto increment, primary key)
     * - username (varchar, 255)
     * - last_login (datetime)
     */
    private void createUserTable() {
        if(!isConnected()) {
            throw new IllegalStateException("Database is not connected");
        }
    }

    /**
     * Create the season table in the database
     * @throws IllegalStateException if the database is not connected
     * Season table should have the following columns:
     * - season_id (int, auto increment, primary key)
     * - start_date (datetime)
     * - end_date (datetime)
     * - players_alive (text)
     * - players_banned (text)
     */
    private void createSeasonTable() {
        if(!isConnected()) {
            throw new IllegalStateException("Database is not connected");
        }
    }

    /**
     * Create the container table in the database
     * @throws IllegalStateException if the database is not connected
     * Container table should have the following columns:
     * - container_id (int, auto increment, primary key)
     * - x (int)
     * - y (int)
     * - z (int)
     * - season_id (int, foreign key to season.season_id)
     * - container_contents (text)
     */
    private void createContainerTable() {
        if(!isConnected()) {
            throw new IllegalStateException("Database is not connected");
        }
    }

    public SQLHandler(Config config) {
        this.config = config;
    }
}
