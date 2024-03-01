package us.mkaulfers.hardcoreseasons.managers;

import org.bukkit.Bukkit;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.models.SurvivorContainer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ContainersManager {
    public List<SurvivorContainer> containers;
    private final HardcoreSeasons plugin;

    public ContainersManager(HardcoreSeasons plugin) {
        this.plugin = plugin;
        loadContainers();
    }

    public void loadContainers() {
        if (plugin.databaseManager.dataSource != null) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    Connection connection = plugin.databaseManager.dataSource.getConnection();
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
                    connection.close();
                    this.containers = containers;
                } catch (Exception e) {
                    Bukkit.getLogger().warning("[Hardcore Seasons]: Could not load containers.");
                }
            });
        }
        plugin.databaseManager.connect();
    }

    public void saveContainer(SurvivorContainer container) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                Connection connection = plugin.databaseManager.dataSource.getConnection();
                String query = "INSERT INTO survivors_containers (season_id, container_x, container_y, container_z, world, type, contents) VALUES (?, ?, ?, ?, ?, ?, ?)";
                java.sql.PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, container.seasonId);
                preparedStatement.setInt(2, container.x);
                preparedStatement.setInt(3, container.y);
                preparedStatement.setInt(4, container.z);
                preparedStatement.setString(5, container.world);
                preparedStatement.setString(6, container.type);
                preparedStatement.setString(7, container.contents);
                preparedStatement.execute();
                connection.close();
                containers.add(container);
            } catch (Exception e) {
                Bukkit.getLogger().warning("[Hardcore Seasons]: Could not save container.");
            }
        });
    }

    public void deleteContainer(SurvivorContainer container) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                Connection connection = plugin.databaseManager.dataSource.getConnection();
                String query = "DELETE FROM survivors_containers WHERE season_id = ? AND container_x = ? AND container_y = ? AND container_z = ? AND world = ? AND type = ?";
                java.sql.PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, container.seasonId);
                preparedStatement.setInt(2, container.x);
                preparedStatement.setInt(3, container.y);
                preparedStatement.setInt(4, container.z);
                preparedStatement.setString(5, container.world);
                preparedStatement.setString(6, container.type);
                preparedStatement.execute();
                connection.close();
                containers.remove(container);
            } catch (Exception e) {
                Bukkit.getLogger().warning("[Hardcore Seasons]: Could not delete container.");
            }
        });
    }
}
