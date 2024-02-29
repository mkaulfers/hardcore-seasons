package us.mkaulfers.hardcoreseasons.managers;

import org.bukkit.Bukkit;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.models.SurvivorContainer;
import us.mkaulfers.hardcoreseasons.storage.DatabaseManager;
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
}
