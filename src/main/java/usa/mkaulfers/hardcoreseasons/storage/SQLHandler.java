package usa.mkaulfers.hardcoreseasons.storage;

import usa.mkaulfers.hardcoreseasons.models.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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
        initializeDatabase();
    }

    public void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void initializeDatabase() throws SQLException {
        if (!isConnected()) {
            throw new IllegalStateException("Database is not connected");
        }

        // Create schema
        String CREATE_SCHEMA_QUERY = "CREATE SCHEMA IF NOT EXISTS `" + config.mySQLConfig.database + "`";
        Statement stmt = connection.createStatement();
        stmt.execute(CREATE_SCHEMA_QUERY);

        // Switch to created schema
        String USE_SCHEMA_QUERY = "USE `" + config.mySQLConfig.database + "`";
        stmt.execute(USE_SCHEMA_QUERY);

        // Separate strings to create each table independently
        String CREATE_SEASONS_TABLE_QUERY = """
                CREATE TABLE `Seasons` (
                  `season_id` INT AUTO_INCREMENT,
                  `start_date` DATETIME,
                  `end_date` DATETIME,
                  `active` BOOLEAN,
                  PRIMARY KEY (`season_id`)
                );
                """;
        stmt.execute(CREATE_SEASONS_TABLE_QUERY);

        String CREATE_PLAYERS_TABLE_QUERY = """
                CREATE TABLE `Players` (
                  `player_id` INT AUTO_INCREMENT,
                  `first_joined_date` DATETIME,
                  `last_joined_date` DATETIME,
                  PRIMARY KEY (`player_id`)
                );
                """;
        stmt.execute(CREATE_PLAYERS_TABLE_QUERY);

        String CREATE_SEASONS_PLAYERS_TABLE_QUERY = """
                CREATE TABLE `Seasons_Players` (
                  `season_player_id` INT AUTO_INCREMENT,
                  `season_id` INT,
                  `player_id` INT,
                  PRIMARY KEY (`season_player_id`),
                  FOREIGN KEY (`season_id`) REFERENCES `Seasons`(`season_id`),
                  FOREIGN KEY (`player_id`) REFERENCES `Players`(`player_id`)
                );
                """;
        stmt.execute(CREATE_SEASONS_PLAYERS_TABLE_QUERY);

        String CREATE_CONTAINERS_TABLE_QUERY = """
                CREATE TABLE `Containers` (
                  `container_id` INT AUTO_INCREMENT,
                  `player_id` INT,
                  `season_id` INT,
                  `inventory_data` BLOB,
                  PRIMARY KEY (`container_id`),
                  FOREIGN KEY (`player_id`) REFERENCES `Players`(`player_id`),
                  FOREIGN KEY (`season_id`) REFERENCES `Seasons`(`season_id`)
                );
                """;
        stmt.execute(CREATE_CONTAINERS_TABLE_QUERY);
    }

    public SQLHandler(Config config) {
        this.config = config;
    }
}
