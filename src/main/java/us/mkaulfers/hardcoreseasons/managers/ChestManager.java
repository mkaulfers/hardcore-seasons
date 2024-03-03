package us.mkaulfers.hardcoreseasons.managers;

import org.bukkit.Bukkit;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.models.SurvivorContainer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListSet;

public class ChestManager {
    public ConcurrentSkipListSet<SurvivorContainer> containers;
    private final HardcoreSeasons plugin;

    public ChestManager(HardcoreSeasons plugin) {
        this.plugin = plugin;
        loadContainers();
    }

    public boolean doesContainerExist(int seasonId, int x, int y, int z, String world, String type) {
        return containers
                .stream()
                .anyMatch(container -> container.seasonId == seasonId &&
                        container.x == x &&
                        container.y == y &&
                        container.z == z &&
                        container.world.equals(world) &&
                        container.type.equals(type));
    }

    public SurvivorContainer getContainer(int seasonId, int x, int y, int z, String world, String type) {
        for (SurvivorContainer container : containers) {
            if (container.seasonId == seasonId &&
                    container.x == x &&
                    container.y == y &&
                    container.z == z &&
                    container.world.equals(world) &&
                    container.type.equals(type)) {
                return container;
            }
        }
        return null;
    }

    public void loadContainers() {
        CompletableFuture.runAsync(() -> {
            try {
                Connection connection = plugin.databaseManager.dataSource.getConnection();
                ResultSet resultset = connection.prepareStatement("SELECT * FROM survivors_containers").executeQuery();

                ConcurrentSkipListSet<SurvivorContainer> containers = new ConcurrentSkipListSet<>();

                while (resultset.next()) {
                    SurvivorContainer container = new SurvivorContainer(
                            resultset.getInt("season_id"),
                            resultset.getInt("container_x"),
                            resultset.getInt("container_y"),
                            resultset.getInt("container_z"),
                            resultset.getString("world"),
                            resultset.getString("type"),
                            resultset.getString("contents")
                    );
                    containers.add(container);
                }
                connection.close();
                this.containers = containers;
            } catch (Exception e) {
                Bukkit.getLogger().warning("[Hardcore Seasons]: Could not load containers.");
            }
        });
    }

    public void saveContainer(SurvivorContainer container) {
        CompletableFuture.runAsync(() -> {
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

    public void updateContainer(SurvivorContainer container) {
        CompletableFuture.runAsync(() -> {
            try {
                Connection connection = plugin.databaseManager.dataSource.getConnection();
                String query = "UPDATE survivors_containers SET contents = ? WHERE season_id = ? AND container_x = ? AND container_y = ? AND container_z = ? AND world = ? AND type = ?";
                java.sql.PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, container.contents);
                preparedStatement.setInt(2, container.seasonId);
                preparedStatement.setInt(3, container.x);
                preparedStatement.setInt(4, container.y);
                preparedStatement.setInt(5, container.z);
                preparedStatement.setString(6, container.world);
                preparedStatement.setString(7, container.type);
                preparedStatement.execute();
                connection.close();
                containers.removeIf(c -> c.seasonId == container.seasonId &&
                        c.x == container.x &&
                        c.y == container.y &&
                        c.z == container.z &&
                        c.world.equals(container.world) &&
                        c.type.equals(container.type));
                containers.add(container);
            } catch (Exception e) {
                Bukkit.getLogger().warning("[Hardcore Seasons]: Could not update container.");
            }
        });
    }

    public void deleteContainer(SurvivorContainer container) {
        CompletableFuture.runAsync(() -> {
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
