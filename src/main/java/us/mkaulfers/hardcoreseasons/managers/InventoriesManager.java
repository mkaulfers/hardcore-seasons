package us.mkaulfers.hardcoreseasons.managers;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.models.SurvivorInventory;
import us.mkaulfers.hardcoreseasons.utils.InventoryUtils;

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

    public boolean doesInventoryExist(UUID playerId, int seasonId) {
        return inventories
                .stream()
                .anyMatch(inventory -> inventory.playerUUID.equals(playerId) &&
                        inventory.seasonId == seasonId);
    }

    public ItemStack[] getInventory(UUID playerId, int seasonId) {
        for (SurvivorInventory inventory : inventories) {
            if (inventory.playerUUID.equals(playerId) && inventory.seasonId == seasonId) {
                try {
                    return InventoryUtils.itemStackArrayFromBase64(inventory.contents);
                } catch (Exception e) {
                    Bukkit.getLogger().warning("[Hardcore Seasons]: Could not deserialize inventory.\n" + e.getMessage());
                }
            }
        }
        return null;
    }

    public void loadInventories() {
        if (plugin.databaseManager.dataSource != null) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    Connection connection = plugin.databaseManager.dataSource.getConnection();
                    ResultSet resultset = connection.prepareStatement("SELECT * FROM survivors_inventories").executeQuery();

                    List<SurvivorInventory> inventories = new ArrayList<>();

                    while (resultset.next()) {
                        UUID playerId = UUID.fromString(resultset.getString("survivor_id"));
                        int seasonId = resultset.getInt("season_id");
                        String contents = resultset.getString("contents");
                        SurvivorInventory inventory = new SurvivorInventory(playerId, seasonId, contents);
                        inventories.add(inventory);
                    }
                    connection.close();
                    this.inventories = inventories;
                } catch (Exception e) {
                    Bukkit.getLogger().warning("[Hardcore Seasons]: Could not load inventories.\n" + e.getMessage());
                }
            });
        }
        plugin.databaseManager.connect();
    }

    public void saveInventory(SurvivorInventory survivorInventory) {
        if (plugin.databaseManager.dataSource != null) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    Connection connection = plugin.databaseManager.dataSource.getConnection();
                    String query = "INSERT INTO survivors_inventories (survivor_id, season_id, contents) VALUES (?, ?, ?)";
                    java.sql.PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, survivorInventory.playerUUID.toString());
                    preparedStatement.setInt(2, survivorInventory.seasonId);
                    preparedStatement.setString(3, survivorInventory.contents);
                    preparedStatement.execute();
                    connection.close();
                    inventories.add(survivorInventory);
                } catch (Exception e) {
                    Bukkit.getLogger().warning("[Hardcore Seasons]: Could not save inventory.\n" + e.getMessage());
                }
            });
        }
        plugin.databaseManager.connect();
    }

    public void updateInventory(SurvivorInventory survivorInventory) {
        if (plugin.databaseManager.dataSource != null) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    Connection connection = plugin.databaseManager.dataSource.getConnection();
                    String query = "UPDATE survivors_inventories SET contents = ? WHERE survivor_id = ? AND season_id = ?";
                    java.sql.PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, survivorInventory.contents);
                    preparedStatement.setString(2, survivorInventory.playerUUID.toString());
                    preparedStatement.setInt(3, survivorInventory.seasonId);
                    preparedStatement.execute();
                    connection.close();
                    inventories.removeIf(inventory -> inventory.playerUUID.equals(survivorInventory.playerUUID) &&
                            inventory.seasonId == survivorInventory.seasonId);
                    inventories.add(survivorInventory);
                } catch (Exception e) {
                    Bukkit.getLogger().warning("[Hardcore Seasons]: Could not update inventory.\n" + e.getMessage());
                }
            });
        }
        plugin.databaseManager.connect();
    }
}
