package usa.mkaulfers.hardcoreseasons.storage;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.block.Container;
import usa.mkaulfers.hardcoreseasons.models.Config;
import usa.mkaulfers.hardcoreseasons.models.Season;

import java.sql.*;
import java.util.Base64;

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

            String CREATE_SEASONS_TABLE_QUERY = """
                    CREATE TABLE IF NOT EXISTS `seasons` (
                        `season_id` INT AUTO_INCREMENT,
                        `start_date` DATETIME DEFAULT CURRENT_TIMESTAMP,
                        `end_date` DATETIME DEFAULT NULL,
                        `active` BOOLEAN DEFAULT true,
                        PRIMARY KEY (`season_id`)
                    );
                    """;
            stmt.execute(CREATE_SEASONS_TABLE_QUERY);

            String CHECK_IF_SEASON_EXISTS_QUERY = "SELECT * FROM `seasons`";
            ResultSet resultSet = stmt.executeQuery(CHECK_IF_SEASON_EXISTS_QUERY);
            if (!resultSet.next()) {
                String INSERT_FIRST_SEASON = "INSERT INTO `seasons`(`start_date`, `active`) VALUES(CURRENT_TIMESTAMP, true)";
                stmt.execute(INSERT_FIRST_SEASON);
            }

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

            String CREATE_TRACKED_CONTAINERS_TABLE_QUERY = """
                    CREATE TABLE IF NOT EXISTS `tracked_containers` (
                      `container_id` INT AUTO_INCREMENT,
                      `season_id` INT,
                      `container_x` INT,
                      `container_y` INT,
                      `container_z` INT,
                      `container_contents` TEXT,
                      PRIMARY KEY (`container_id`),
                      FOREIGN KEY (`season_id`) REFERENCES `seasons`(`season_id`)
                    );
                    """;
            stmt.execute(CREATE_TRACKED_CONTAINERS_TABLE_QUERY);

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

    public Season getActiveSeason() {
        if (!isConnected()) {
            throw new IllegalStateException("Database is not connected");
        }

        Season activeSeason = null;

        try (Connection connection = dataSource.getConnection()) {
            Statement stmt = connection.createStatement();
            String GET_ACTIVE_SEASON_QUERY = "SELECT * FROM `seasons` WHERE `active` = true";
            ResultSet resultSet = stmt.executeQuery(GET_ACTIVE_SEASON_QUERY);

            if (resultSet.next()) {
                activeSeason = new Season(
                        resultSet.getInt("season_id"),
                        resultSet.getTimestamp("start_date"),
                        resultSet.getTimestamp("end_date"),
                        resultSet.getBoolean("active")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return activeSeason;
    }

    public void makeSeasonActive(int seasonId) throws SQLException {
        if (!isConnected()) {
            throw new IllegalStateException("Database is not connected");
        }

        // Set all seasons to inactive
        try (Connection connection = dataSource.getConnection()) {
            Statement stmt = connection.createStatement();
            String SET_ALL_SEASONS_INACTIVE_QUERY = "UPDATE `seasons` SET `active` = false";
            stmt.execute(SET_ALL_SEASONS_INACTIVE_QUERY);
        }

        // Set the desired season to active
        try (Connection connection = dataSource.getConnection()) {
            Statement stmt = connection.createStatement();
            String SET_SEASON_ACTIVE_QUERY = "UPDATE `seasons` SET `active` = true WHERE `season_id` = " + seasonId;
            stmt.execute(SET_SEASON_ACTIVE_QUERY);
        }
    }

    public boolean isContainerTracked(int seasonId, int x, int y, int z) {
        if (!isConnected()) {
            throw new IllegalStateException("Database is not connected");
        }

        try (Connection connection = dataSource.getConnection()) {
            Statement stmt = connection.createStatement();
            String IS_CONTAINER_TRACKED_QUERY = "SELECT * FROM `tracked_containers` WHERE `season_id` = " + seasonId +
                    " AND `container_x` = " + x + " AND `container_y` = " + y + " AND `container_z` = " + z;
            ResultSet resultSet = stmt.executeQuery(IS_CONTAINER_TRACKED_QUERY);

            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    ;

    public void insertContainer(int seasonID, int x, int y, int z, String contents) {
        if (!isConnected()) {
            throw new IllegalStateException("Database is not connected");
        }

        try (Connection connection = dataSource.getConnection()) {
            Statement stmt = connection.createStatement();
            String INSERT_CONTAINER_QUERY = "INSERT INTO `tracked_containers` (`season_id`, `container_x`, `container_y`, `container_z`, `container_contents`) " +
                    "VALUES (" + seasonID + ", " + x + ", " + y + ", " + z + ", '" + contents + "')";
            stmt.execute(INSERT_CONTAINER_QUERY);

            getActiveSeason();
        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public void deleteContainer(int seasonID, int x, int y, int z) {
        if (!isConnected()) {
            throw new IllegalStateException("Database is not connected");
        }

        try (Connection connection = dataSource.getConnection()) {
            Statement stmt = connection.createStatement();
            String DELETE_CONTAINER_QUERY = "DELETE FROM `tracked_containers` WHERE `season_id` = " + seasonID +
                    " AND `container_x` = " + x + " AND `container_y` = " + y + " AND `container_z` = " + z;
            stmt.execute(DELETE_CONTAINER_QUERY);

            getActiveSeason();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public SQLHandler(Config config) {
        this.config = config;
    }
}
