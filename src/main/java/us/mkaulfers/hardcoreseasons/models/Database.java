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

    public Database(MySQLConfig mySQLConfig) {
        Database.host = mySQLConfig.host;
        Database.port = mySQLConfig.port;
        Database.database = mySQLConfig.database;
        Database.username = mySQLConfig.username;
        Database.password = mySQLConfig.password;
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        config.setUsername(username);
        config.setPassword(password);
        ds = new HikariDataSource(config);

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
                            end_date DATETIME,
                            PRIMARY KEY (id)
                        );
                        """;
            connection.prepareStatement(CREATE_SEASONS_TABLE).execute();

            String INIT_PLAYERS = """
                        CREATE TABLE IF NOT EXISTS players (
                            id INT AUTO_INCREMENT,
                            player_id VARCHAR(36),
                            season_id INT,
                            join_date DATETIME,
                            last_online DATETIME,
                            is_dead BOOLEAN,
                            PRIMARY KEY (id)
                        );
                        """;
            connection.prepareStatement(INIT_PLAYERS).execute();

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
        }
    }

    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
