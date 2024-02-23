package usa.mkaulfers.hardcoreseasons.storage;

import com.zaxxer.hikari.HikariDataSource;
import usa.mkaulfers.hardcoreseasons.models.Config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLHandler {
    private Config config;
    private HikariDataSource dataSource;

    public boolean isConnected() {
        return dataSource != null;
    }

    public void connect() {
        String host = config.mySQLConfig.host;
        int port = config.mySQLConfig.port;
        String database = config.mySQLConfig.database;

        String username = config.mySQLConfig.username;
        String password = config.mySQLConfig.password;

        dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        dataSource.addDataSourceProperty("user", username);
        dataSource.addDataSourceProperty("password", password);

        initializeDatabase();
    }

    public void disconnect() {
        if (isConnected()) {
            dataSource.close();
        }
    }

    public void initializeDatabase() {
        if (!isConnected()) {
            throw new IllegalStateException("Database is not connected");
        }

        try (Connection connection = dataSource.getConnection()) {
            Statement stmt = connection.createStatement();

            String USE_SCHEMA_QUERY = "USE `" + config.mySQLConfig.database + "`";
            stmt.execute(USE_SCHEMA_QUERY);

            // Separate strings to create each table independently
            String CREATE_SEASONS_TABLE_QUERY = """
                CREATE TABLE IF NOT EXISTS  `seasons` (
                  `season_id` INT AUTO_INCREMENT,
                  `start_date` DATETIME,
                  `end_date` DATETIME,
                  `active` BOOLEAN,
                  PRIMARY KEY (`season_id`)
                );
                """;
            stmt.execute(CREATE_SEASONS_TABLE_QUERY);

            String CREATE_PLAYERS_TABLE_QUERY = """
                CREATE TABLE IF NOT EXISTS  `player_details` (
                  `player_id` INT AUTO_INCREMENT,
                  `first_joined_date` DATETIME,
                  `last_joined_date` DATETIME,
                  PRIMARY KEY (`player_id`)
                );
                """;
            stmt.execute(CREATE_PLAYERS_TABLE_QUERY);

            String CREATE_SEASONS_PLAYERS_TABLE_QUERY = """
                CREATE TABLE IF NOT EXISTS  `season_participants` (
                  `season_player_id` INT AUTO_INCREMENT,
                  `season_id` INT,
                  `player_id` INT,
                  PRIMARY KEY (`season_player_id`),
                  FOREIGN KEY (`season_id`) REFERENCES `seasons`(`season_id`),
                  FOREIGN KEY (`player_id`) REFERENCES `player_details`(`player_id`)
                );
                """;
            stmt.execute(CREATE_SEASONS_PLAYERS_TABLE_QUERY);

            String CREATE_CONTAINERS_TABLE_QUERY = """
                CREATE TABLE IF NOT EXISTS `tracked_containers` (
                  `container_id` INT AUTO_INCREMENT,
                  `player_id` INT,
                  `season_id` INT,
                  `inventory_data` BLOB,
                  PRIMARY KEY (`container_id`),
                  FOREIGN KEY (`player_id`) REFERENCES `player_details`(`player_id`),
                  FOREIGN KEY (`season_id`) REFERENCES `seasons`(`season_id`)
                );
                """;
            stmt.execute(CREATE_CONTAINERS_TABLE_QUERY);

            String CREATE_PLAYER_INVENTORIES_TABLE_QUERY = """
                CREATE TABLE IF NOT EXISTS `player_inventories` (
                  `inventory_id` INT AUTO_INCREMENT,
                  `player_id` INT,
                  `inventory_data` BLOB,
                  PRIMARY KEY (`inventory_id`),
                  FOREIGN KEY (`player_id`) REFERENCES `player_details`(`player_id`)
                );
                """;
            stmt.execute(CREATE_PLAYER_INVENTORIES_TABLE_QUERY);

            String CREATE_PLAYER_END_CHEST_INVENTORIES_TABLE_QUERY = """
                CREATE TABLE IF NOT EXISTS `player_end_chest_inventories` (
                  `inventory_id` INT AUTO_INCREMENT,
                  `player_id` INT,
                  `inventory_data` BLOB,
                  PRIMARY KEY (`inventory_id`),
                  FOREIGN KEY (`player_id`) REFERENCES `player_details`(`player_id`)
                );
                """;
            stmt.execute(CREATE_PLAYER_END_CHEST_INVENTORIES_TABLE_QUERY);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public SQLHandler(Config config) {
        this.config = config;
    }
}
