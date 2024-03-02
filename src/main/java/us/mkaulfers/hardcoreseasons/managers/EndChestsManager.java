package us.mkaulfers.hardcoreseasons.managers;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.models.SurvivorEndChest;
import us.mkaulfers.hardcoreseasons.utils.InventoryUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class EndChestsManager {
    public List<SurvivorEndChest> endChests;
    private final HardcoreSeasons plugin;

    public EndChestsManager(HardcoreSeasons plugin) {
        this.plugin = plugin;
        loadEndChests();
    }

    public boolean doesEndChestExist(UUID playerId, int seasonId) {
        return endChests
                .stream()
                .anyMatch(endChest -> endChest.playerUUID.equals(playerId) &&
                        endChest.seasonId == seasonId);
    }

    public ItemStack[] getEndChest(UUID playerId, int seasonId) {
        for (SurvivorEndChest endChest : endChests) {
            if (endChest.playerUUID.equals(playerId) && endChest.seasonId == seasonId) {
                try {
                    return InventoryUtils.itemStackArrayFromBase64(endChest.contents);
                } catch (Exception e) {
                    Bukkit.getLogger().warning("[Hardcore Seasons]: Could not deserialize end chest.\n" + e.getMessage());
                }
            }
        }
        return null;
    }

    public void loadEndChests() {
        if (plugin.databaseManager.dataSource != null) {
            CompletableFuture.runAsync(() -> {
                try {
                    Connection connection = plugin.databaseManager.dataSource.getConnection();
                    ResultSet resultset = connection.prepareStatement("SELECT * FROM survivors_end_chests").executeQuery();

                    List<SurvivorEndChest> endChests = new ArrayList<>(resultset.getFetchSize());

                    while (resultset.next()) {
                        UUID playerId = UUID.fromString(resultset.getString("survivor_id"));
                        int seasonId = resultset.getInt("season_id");
                        String contents = resultset.getString("contents");
                        SurvivorEndChest endChest = new SurvivorEndChest(playerId, seasonId, contents);
                        endChests.add(endChest);
                    }
                    connection.close();
                    this.endChests = endChests;
                } catch (Exception e) {
                    Bukkit.getLogger().warning("[Hardcore Seasons]: Could not load end chests.");
                }
            });
        }
        plugin.databaseManager.connect();
    }

    public void saveEndChest(SurvivorEndChest endChest) {
        if (plugin.databaseManager.dataSource != null) {
            CompletableFuture.runAsync(() -> {
                try {
                    Connection connection = plugin.databaseManager.dataSource.getConnection();
                    String query = "INSERT INTO survivors_end_chests (season_id, survivor_id, contents) VALUES (?, ?, ?)";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setInt(1, endChest.seasonId);
                    statement.setString(2, endChest.playerUUID.toString());
                    statement.setString(3, endChest.contents);
                    statement.execute();
                    connection.close();
                    endChests.add(endChest);
                } catch (Exception e) {
                    Bukkit.getLogger().warning("[Hardcore Seasons]: Could not save end chest.\n" + e.getMessage());
                }
            });
        }
        plugin.databaseManager.connect();
    }

    public void updateEndChest(SurvivorEndChest endChest) {
        if (plugin.databaseManager.dataSource != null) {
            CompletableFuture.runAsync(() -> {
                try {
                    Connection connection = plugin.databaseManager.dataSource.getConnection();
                    String query = "UPDATE survivors_end_chests SET contents = ? WHERE season_id = ? AND survivor_id = ?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, endChest.contents);
                    statement.setInt(2, endChest.seasonId);
                    statement.setString(3, endChest.playerUUID.toString());
                    statement.execute();
                    connection.close();
                    endChests.removeIf(chest -> chest.playerUUID.equals(endChest.playerUUID) && chest.seasonId == endChest.seasonId);
                    endChests.add(endChest);
                } catch (Exception e) {
                    Bukkit.getLogger().warning("[Hardcore Seasons]: Could not update end chest.\n" + e.getMessage());
                }
            });
        }
        plugin.databaseManager.connect();
    }
}
