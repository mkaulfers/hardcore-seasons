package us.mkaulfers.hardcoreseasons.managers;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.managers.*;

import java.sql.Connection;

public class DatabaseManager {
    public ContainersManager containersManager;
    public EndChestsManager endChestsManager;
    public InventoriesManager inventoriesManager;
    public SeasonsManager seasonsManager;
    public SurvivorsManager survivorsManager;

    public HikariDataSource dataSource;
    public HardcoreSeasons plugin;

    public DatabaseManager(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }

    public void connect() {
        if (dataSource != null) {
            return;
        }

        String host = plugin.pluginConfig.mySQLConfig.host;
        int port = plugin.pluginConfig.mySQLConfig.port;
        String database = plugin.pluginConfig.mySQLConfig.database;

        String username = plugin.pluginConfig.mySQLConfig.username;
        String password = plugin.pluginConfig.mySQLConfig.password;

        dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        dataSource.addDataSourceProperty("user", username);
        dataSource.addDataSourceProperty("password", password);
    }

    public void initTables() {
        if (dataSource != null) {
            try {
                Connection connection = dataSource.getConnection();

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

                String CREATE_SURVIVORS_TABLE = """
                        CREATE TABLE IF NOT EXISTS survivors (
                            id INT AUTO_INCREMENT,
                            survivor_id VARCHAR(36),
                            season_id INT,
                            join_date DATETIME,
                            last_online DATETIME,
                            is_dead BOOLEAN,
                            PRIMARY KEY (id)
                        );
                        """;
                connection.prepareStatement(CREATE_SURVIVORS_TABLE).execute();

                String CREATE_SURVIVORS_CONTAINERS_TABLE = """
                        CREATE TABLE IF NOT EXISTS `survivors_containers` (
                          id INT AUTO_INCREMENT,
                          season_id INT,
                          container_x INT,
                          container_y INT,
                          container_z INT,
                          world VARCHAR(255),
                          type VARCHAR(255),
                          contents MEDIUMTEXT,
                          PRIMARY KEY (id)
                        );
                        """;
                connection.prepareStatement(CREATE_SURVIVORS_CONTAINERS_TABLE).execute();

                String CREATE_SURVIVORS_END_CHESTS_TABLE = """
                        CREATE TABLE IF NOT EXISTS `survivors_end_chests` (
                          id INT AUTO_INCREMENT,
                          season_id INT,
                          survivor_id VARCHAR(36),
                          contents MEDIUMTEXT,
                          PRIMARY KEY (id)
                        );
                        """;
                connection.prepareStatement(CREATE_SURVIVORS_END_CHESTS_TABLE).execute();

                String CREATE_SURVIVORS_INVENTORIES_TABLE = """
                        CREATE TABLE IF NOT EXISTS `survivors_inventories` (
                          id INT AUTO_INCREMENT,
                          season_id INT,
                          survivor_id VARCHAR(36),
                          contents MEDIUMTEXT,
                          PRIMARY KEY (id)
                        );
                        """;
                connection.prepareStatement(CREATE_SURVIVORS_INVENTORIES_TABLE).execute();
            } catch (Exception e) {
                Bukkit.getLogger().warning("[Hardcore Seasons]: Could not create tables.\n" + e.getMessage());
            }
            return;
        }
        connect();
    }

    public void scheduleIntervalUpdate() {
        int updateInterval = plugin.pluginConfig.mySQLConfig.updateInterval;
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            if (dataSource != null) {
                this.seasonsManager.loadSeasons();
                this.survivorsManager.loadSurvivors();
                this.containersManager.loadContainers();
                this.endChestsManager.loadEndChests();
                this.inventoriesManager.loadInventories();
            }
        }, (updateInterval * 20L) * 60L, (updateInterval * 20L) * 60L);
    }

    public void initManagers() {
        this.seasonsManager = new SeasonsManager(plugin);
        this.survivorsManager = new SurvivorsManager(plugin);
        this.containersManager = new ContainersManager(plugin);
        this.endChestsManager = new EndChestsManager(plugin);
        this.inventoriesManager = new InventoriesManager(plugin);
    }

    public void disconnect() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
