package us.mkaulfers.hardcoreseasons.managers;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.models.SurvivorInventory;
import us.mkaulfers.hardcoreseasons.utils.InventoryUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListSet;

public class InventoriesManager {
    public ConcurrentSkipListSet<SurvivorInventory> inventories;
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
        CompletableFuture.runAsync(() -> {
            try {
                Connection connection = plugin.databaseManager.dataSource.getConnection();
                ResultSet resultset = connection.prepareStatement("SELECT * FROM survivors_inventories").executeQuery();

                ConcurrentSkipListSet<SurvivorInventory> inventories = new ConcurrentSkipListSet<>();

                while (resultset.next()) {
                    SurvivorInventory inventory = new SurvivorInventory(
                            UUID.fromString(resultset.getString("survivor_id")),
                            resultset.getInt("season_id"),
                            resultset.getString("contents")
                    );
                    inventories.add(inventory);
                }
                connection.close();
                this.inventories = inventories;
            } catch (Exception e) {
                Bukkit.getLogger().warning("[Hardcore Seasons]: Could not load inventories.\n" + e.getMessage());
            }
        });
    }

    public void saveInventory(SurvivorInventory survivorInventory) {
        CompletableFuture.runAsync(() -> {
            try {
                Connection connection = plugin.databaseManager.dataSource.getConnection();
                String query = "INSERT INTO survivors_inventories (survivor_id, season_id, contents) VALUES (?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
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

    public void updateInventory(SurvivorInventory survivorInventory) {
        CompletableFuture.runAsync(() -> {
            try {
                Connection connection = plugin.databaseManager.dataSource.getConnection();
                String query = "UPDATE survivors_inventories SET contents = ? WHERE survivor_id = ? AND season_id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
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
}
