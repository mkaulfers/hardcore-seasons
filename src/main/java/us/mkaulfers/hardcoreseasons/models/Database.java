package us.mkaulfers.hardcoreseasons.models;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.SQLException;

public class Database {
    private static String host;
    private static int port;
    private static String database;
    private static String username;
    private static String password;
    private static final HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;
    private final PluginConfig pluginConfig;

    public Database(PluginConfig pluginConfig) {
        Database.host = pluginConfig.mySQLConfig.host;
        Database.port = pluginConfig.mySQLConfig.port;
        Database.database = pluginConfig.mySQLConfig.database;
        Database.username = pluginConfig.mySQLConfig.username;
        Database.password = pluginConfig.mySQLConfig.password;
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        config.setUsername(username);
        config.setPassword(password);
        ds = new HikariDataSource(config);

        this.pluginConfig = pluginConfig;

        try {
            initTables();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Failed to initialize tables: " + e.getMessage());
        }
    }

    public void initTables() throws SQLException {
        try (Connection connection = ds.getConnection()) {
            String CREATE_SEASONS_TABLE = """
                        CREATE TABLE IF NOT EXISTS seasons (
                            id INT AUTO_INCREMENT,
                            season_id INT,
                            start_date DATETIME,
                            soft_end_date DATETIME,
                            hard_end_date DATETIME,
                            PRIMARY KEY (id)
                        );
                        """;
            connection.prepareStatement(CREATE_SEASONS_TABLE).execute();

            int minimumLength = pluginConfig.minSeasonLength;
            int maximumLength = pluginConfig.maxSeasonLength;

            if (minimumLength < 1) {
                minimumLength = 1;
            }

            if (minimumLength > maximumLength) {
                maximumLength = minimumLength;
            }

            String INIT_FIRST_SEASON = String.format(
                    "INSERT INTO seasons (season_id, start_date, soft_end_date, hard_end_date)\n"  +
                            "SELECT 1, NOW(), DATE_ADD(NOW(), INTERVAL %d DAY),\n" +
                            "CASE WHEN %d = -1 THEN NULL ELSE DATE_ADD(NOW(), INTERVAL %d DAY) END\n" +
                            "FROM (SELECT 1 AS a) tempTable\n" +
                            "WHERE NOT EXISTS (SELECT 1 FROM seasons WHERE season_id = 1);",
                    minimumLength, maximumLength, maximumLength
            );
            connection.prepareStatement(INIT_FIRST_SEASON).execute();

            String INIT_PARTICIPANTS = """
                        CREATE TABLE IF NOT EXISTS participants (
                            id INT AUTO_INCREMENT,
                            player_id VARCHAR(36),
                            season_id INT,
                            join_date DATETIME,
                            last_online DATETIME,
                            is_dead BOOLEAN,
                            PRIMARY KEY (id)
                        );
                        """;
            connection.prepareStatement(INIT_PARTICIPANTS).execute();

            String INIT_TRACKED_CHESTS = """
                        CREATE TABLE IF NOT EXISTS `tracked_chests` (
                          id INT AUTO_INCREMENT,
                          season_id INT,
                          x INT,
                          y INT,
                          z INT,
                          world VARCHAR(255),
                          type VARCHAR(255),
                          contents MEDIUMTEXT,
                          PRIMARY KEY (id)
                        );
                        """;
            connection.prepareStatement(INIT_TRACKED_CHESTS).execute();

            String INIT_END_CHESTS = """
                        CREATE TABLE IF NOT EXISTS `end_chests` (
                          id INT AUTO_INCREMENT,
                          season_id INT,
                          player_id VARCHAR(36),
                          contents MEDIUMTEXT,
                          PRIMARY KEY (id)
                        );
                        """;
            connection.prepareStatement(INIT_END_CHESTS).execute();

            String INIT_INVENTORIES = """
                        CREATE TABLE IF NOT EXISTS `inventories` (
                          id INT AUTO_INCREMENT,
                          season_id INT,
                          player_id VARCHAR(36),
                          contents MEDIUMTEXT,
                          PRIMARY KEY (id)
                        );
                        """;
            connection.prepareStatement(INIT_INVENTORIES).execute();

            String INIT_REWARDS = """
                        CREATE TABLE IF NOT EXISTS `season_rewards` (
                          id INT AUTO_INCREMENT,
                          player_id VARCHAR(36),
                          season_id INT,
                          contents LONGTEXT,
                          redeemed BOOLEAN,
                          PRIMARY KEY (id)
                        );
                        """;
            connection.prepareStatement(INIT_REWARDS).execute();

            String INIT_VOTES = """
                        CREATE TABLE IF NOT EXISTS `votes` (
                          id INT AUTO_INCREMENT,
                          season_id INT,
                          player_id VARCHAR(36),
                          date_last_voted DATETIME,
                          should_end_season BOOLEAN,
                          PRIMARY KEY (id)
                        );
                        """;
            connection.prepareStatement(INIT_VOTES).execute();
        }
    }

    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
