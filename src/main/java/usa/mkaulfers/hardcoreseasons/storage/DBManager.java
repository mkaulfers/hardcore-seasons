package usa.mkaulfers.hardcoreseasons.storage;

import com.zaxxer.hikari.HikariDataSource;
import usa.mkaulfers.hardcoreseasons.interfaces.SQLManageable;
import usa.mkaulfers.hardcoreseasons.models.PluginConfig;

import java.sql.Connection;

/// Singleton access
public class DBManager {
    private HikariDataSource dataSource;
    private final PluginConfig pluginConfig;

    public DBManager(PluginConfig pluginConfig) {
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
                String CREATE_TRACKED_CONTAINERS_TABLE = """
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
                connection.prepareStatement(CREATE_TRACKED_CONTAINERS_TABLE).execute();
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
