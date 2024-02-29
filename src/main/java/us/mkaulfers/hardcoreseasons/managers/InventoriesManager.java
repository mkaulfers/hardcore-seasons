package us.mkaulfers.hardcoreseasons.managers;

import org.bukkit.Bukkit;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.models.SurvivorInventory;
import us.mkaulfers.hardcoreseasons.storage.DatabaseManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InventoriesManager {
    public List<SurvivorInventory> inventories;
    private final HardcoreSeasons plugin;

    public InventoriesManager(HardcoreSeasons plugin) {
        this.plugin = plugin;
        loadInventories();
    }

    public void loadInventories() {
        if (plugin.databaseManager.dataSource != null) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    Connection connection = plugin.databaseManager.dataSource.getConnection();
                    ResultSet resultset = connection.prepareStatement("SELECT * FROM survivors_inventories").executeQuery();

                    List<SurvivorInventory> inventories = new ArrayList<>();

                    while (resultset.next()) {
                        SurvivorInventory inventory = new SurvivorInventory();
                        inventory.playerUUID = UUID.fromString(resultset.getString("survivor_id"));
                        inventory.seasonId = resultset.getInt("season_id");
                        inventory.contents = resultset.getString("contents");
                        inventories.add(inventory);
                    }
                    connection.close();
                    this.inventories = inventories;
                } catch (Exception e) {
                    Bukkit.getLogger().warning("[Hardcore Seasons]: Could not load inventories.");
                }
            });
        }
        plugin.databaseManager.connect();
    }
}
