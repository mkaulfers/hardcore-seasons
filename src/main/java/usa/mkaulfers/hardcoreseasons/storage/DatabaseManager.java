package usa.mkaulfers.hardcoreseasons.storage;

import com.zaxxer.hikari.HikariDataSource;
import usa.mkaulfers.hardcoreseasons.interfaces.SQLManageable;
import usa.mkaulfers.hardcoreseasons.models.PluginConfig;

import java.sql.Connection;

/// Singleton access
public class DatabaseManager {
    private HikariDataSource dataSource;
    private final PluginConfig pluginConfig;

    public DatabaseManager(PluginConfig pluginConfig) {
        this.pluginConfig = pluginConfig;
    }

    public void connect() {
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
                          container_contents VARCHAR(255),
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
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        connect();
    }

    public void save(SQLManageable managedObject) {
        if (dataSource != null) {
            try {
                Connection connection = dataSource.getConnection();
                connection.prepareStatement(managedObject.saveQuery()).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        connect();
    }

    public void delete(SQLManageable managedObject) {
        if (dataSource != null) {
            try {
                Connection connection = dataSource.getConnection();
                connection.prepareStatement(managedObject.deleteQuery()).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        connect();
    }

    public void update(SQLManageable managedObject) {
        if (dataSource != null) {
            try {
                Connection connection = dataSource.getConnection();
                connection.prepareStatement(managedObject.updateQuery()).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        connect();
    }

    public void load(SQLManageable managedObject) {
        if (dataSource != null) {
            try {
                Connection connection = dataSource.getConnection();
                connection.prepareStatement(managedObject.loadQuery()).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        connect();
    }

    public void disconnect() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
