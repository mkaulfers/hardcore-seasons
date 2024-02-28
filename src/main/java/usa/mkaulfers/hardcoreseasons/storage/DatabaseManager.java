package usa.mkaulfers.hardcoreseasons.storage;

import com.zaxxer.hikari.HikariDataSource;
import usa.mkaulfers.hardcoreseasons.models.*;

import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/// Singleton access
public class DatabaseManager {
    public List<Season> seasons;
    public List<Survivor> survivors;
    public List<SurvivorContainer> containers;
    public List<SurvivorEndChest> endChests;
    public List<SurvivorInventory> inventories;

    private HikariDataSource dataSource;
    private final PluginConfig pluginConfig;

    public DatabaseManager(PluginConfig pluginConfig) {
        this.pluginConfig = pluginConfig;
    }

    public void connect() {
        if (dataSource != null) {
            return;
        }

        String host = pluginConfig.mySQLConfig.host;
        int port = pluginConfig.mySQLConfig.port;
        String database = pluginConfig.mySQLConfig.database;

        String username = pluginConfig.mySQLConfig.username;
        String password = pluginConfig.mySQLConfig.password;

        dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        dataSource.addDataSourceProperty("user", username);
        dataSource.addDataSourceProperty("password", password);

        if (dataSource != null) {
            try {
                Connection connection = dataSource.getConnection();
                String USE_SCHEMA_QUERY = "USE " + pluginConfig.mySQLConfig.database;
                connection.prepareStatement(USE_SCHEMA_QUERY).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void initTables() {
        if (dataSource != null) {
            try {
                Connection connection = dataSource.getConnection();

                String CREATE_SEASONS_TABLE = """
                        CREATE TABLE IF NOT EXISTS seasons (
                            season_id INT AUTO_INCREMENT,
                            start_date DATE,
                            end_date DATE,
                            PRIMARY KEY (season_id)
                        );
                        """;
                connection.prepareStatement(CREATE_SEASONS_TABLE).execute();

                String CREATE_SURVIVORS_TABLE = """
                        CREATE TABLE IF NOT EXISTS survivors (
                            survivor_id INT,
                            season_id INT,
                            join_date DATE,
                            last_login DATE,
                            is_dead BOOLEAN,
                            PRIMARY KEY (survivor_id),
                            FOREIGN KEY (season_id) REFERENCES seasons (season_id)
                        );
                        """;
                connection.prepareStatement(CREATE_SURVIVORS_TABLE).execute();

                String CREATE_SURVIVORS_CONTAINERS_TABLE = """
                        CREATE TABLE IF NOT EXISTS `survivors_containers` (
                          container_id INT AUTO_INCREMENT,
                          season_id INT,
                          container_x INT,
                          container_y INT,
                          container_z INT,
                          world VARCHAR(255),
                          type VARCHAR(255),
                          contents VARCHAR(255),
                          PRIMARY KEY (container_id),
                          FOREIGN KEY (season_id) REFERENCES seasons (season_id)
                        );
                        """;
                connection.prepareStatement(CREATE_SURVIVORS_CONTAINERS_TABLE).execute();

                String CREATE_SURVIVORS_END_CHESTS_TABLE = """
                        CREATE TABLE IF NOT EXISTS `survivors_end_chests` (
                          season_id INT,
                          survivor_id INT,
                          contents VARCHAR(255),
                          PRIMARY KEY (season_id, survivor_id),
                          FOREIGN KEY (season_id) REFERENCES seasons (season_id),
                          FOREIGN KEY (survivor_id) REFERENCES survivors (survivor_id)
                        );
                        """;
                connection.prepareStatement(CREATE_SURVIVORS_END_CHESTS_TABLE).execute();

                String CREATE_SURVIVORS_INVENTORIES_TABLE = """
                        CREATE TABLE IF NOT EXISTS `survivors_inventories` (
                          season_id INT,
                          survivor_id INT,
                          contents VARCHAR(255),
                          PRIMARY KEY (season_id, survivor_id),
                          FOREIGN KEY (season_id) REFERENCES seasons (season_id),
                          FOREIGN KEY (survivor_id) REFERENCES survivors (survivor_id)
                        );
                        """;
                connection.prepareStatement(CREATE_SURVIVORS_INVENTORIES_TABLE).execute();

                loadSeasons();
                loadSurvivors();
                loadContainers();
                loadEndChests();
                loadInventories();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        connect();
    }

    private void loadSeasons() {
        if (dataSource != null) {
            try {
                Connection connection = dataSource.getConnection();
                ResultSet resultset = connection.prepareStatement("SELECT * FROM seasons").executeQuery();

                List<Season> seasons = new ArrayList<>();

                while (resultset.next()) {
                    Season season = new Season();
                    season.startDate = resultset.getDate("start_date");
                    season.endDate = resultset.getDate("end_date");
                    seasons.add(season);
                }

                this.seasons = seasons;

                if (seasons.size() == 0) {
                    connection.prepareStatement("INSERT INTO seasons (season_id, start_date, end_date) VALUES (1, NOW(), null)").execute();
                    loadSeasons();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        connect();
    }

    private void loadSurvivors() {
        if (dataSource != null) {
            try {
                Connection connection = dataSource.getConnection();
                ResultSet resultset = connection.prepareStatement("SELECT * FROM survivors").executeQuery();

                List<Survivor> survivors = new ArrayList<>();

                while (resultset.next()) {
                    Survivor survivor = new Survivor();
                    survivor.id = UUID.fromString(resultset.getString("survivor_id"));
                    survivor.seasonId = resultset.getInt("season_id");
                    survivor.joinDate = resultset.getDate("join_date");
                    survivor.lastLogin = resultset.getDate("last_login");
                    survivor.isDead = resultset.getBoolean("is_dead");
                    survivors.add(survivor);
                }

                this.survivors = survivors;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        connect();
    }

    private void loadContainers() {
        if (dataSource != null) {
            try {
                Connection connection = dataSource.getConnection();
                ResultSet resultset = connection.prepareStatement("SELECT * FROM survivors_containers").executeQuery();

                List<SurvivorContainer> containers = new ArrayList<>();

                while (resultset.next()) {
                    SurvivorContainer container = new SurvivorContainer();
                    container.seasonId = resultset.getInt("season_id");
                    container.x = resultset.getInt("container_x");
                    container.y = resultset.getInt("container_y");
                    container.z = resultset.getInt("container_z");
                    container.world = resultset.getString("world");
                    container.type = resultset.getString("type");
                    container.contents = resultset.getString("contents");
                    containers.add(container);
                }

                this.containers = containers;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        connect();
    }

    private void loadEndChests() {
        if (dataSource != null) {
            try {
                Connection connection = dataSource.getConnection();
                ResultSet resultset = connection.prepareStatement("SELECT * FROM survivors_end_chests").executeQuery();

                List<SurvivorEndChest> endChests = new ArrayList<>(resultset.getFetchSize());

                while (resultset.next()) {
                    SurvivorEndChest endChest = new SurvivorEndChest();
                    endChest.playerUUID = UUID.fromString(resultset.getString("survivor_id"));
                    endChest.seasonId = resultset.getInt("season_id");
                    endChest.contents = resultset.getString("contents");
                    endChests.add(endChest);
                }

                this.endChests = endChests;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        connect();
    }

    private void loadInventories() {
        if (dataSource != null) {
            try {
                Connection connection = dataSource.getConnection();
                ResultSet resultset = connection.prepareStatement("SELECT * FROM survivors_inventories").executeQuery();

                List<SurvivorInventory> inventories = new ArrayList<>();

                while (resultset.next()) {
                    SurvivorInventory inventory = new SurvivorInventory();
                    inventory.playerUUID = UUID.fromString(resultset.getString("survivor_id"));
                    inventory.seasonId = resultset.getInt("season_id");
                    inventory.contents = resultset.getString("contents");
                    inventories.add(inventory);
                }

                this.inventories = inventories;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        connect();
    }

    public void disconnect() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
