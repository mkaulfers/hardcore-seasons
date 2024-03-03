package us.mkaulfers.hardcoreseasons.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.models.SurvivorEndChest;
import us.mkaulfers.hardcoreseasons.models.SurvivorInventory;
import us.mkaulfers.hardcoreseasons.utils.InventoryUtils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class PlayerQuit implements Listener {
    HardcoreSeasons plugin;

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        plugin.databaseManager.survivorsManager.updateSurvivorLastOnline(playerId, new Timestamp(new Date().getTime()));
        saveInventory(event, playerId);
        saveEndChest(event, playerId);
    }

    public PlayerQuit(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }

    private void saveInventory(PlayerQuitEvent event, UUID playerId) {
        try {
            int activeSeason = plugin.databaseManager.seasonsManager.getActiveSeason().seasonId;
            PlayerInventory inventory = event.getPlayer().getInventory();
            String serializedInventory = InventoryUtils.playerInventoryToBase64(inventory);

            SurvivorInventory survivorInventory = new SurvivorInventory(playerId, activeSeason, serializedInventory);
            if (plugin.databaseManager.inventoryManager.doesInventoryExist(playerId, activeSeason)) {
                plugin.databaseManager.inventoryManager.updateInventory(survivorInventory);
            } else {
                plugin.databaseManager.inventoryManager.saveInventory(survivorInventory);
            }

        } catch (Exception e) {
            Bukkit.getLogger().warning("[Hardcore Seasons]: Could not serialize inventory.\n" + e.getMessage());
        }
    }

    private void saveEndChest(PlayerQuitEvent event, UUID playerId) {
        try {
            int activeSeason = plugin.databaseManager.seasonsManager.getActiveSeason().seasonId;
            Inventory endChest = event.getPlayer().getEnderChest();
            ItemStack[] items = endChest.getContents();
            String serializedEndChest = InventoryUtils.itemStackArrayToBase64(items);

            SurvivorEndChest survivorEndChest = new SurvivorEndChest(playerId, activeSeason, serializedEndChest);
            if (plugin.databaseManager.endChestManager.doesEndChestExist(playerId, activeSeason)) {
                plugin.databaseManager.endChestManager.updateEndChest(survivorEndChest);
            } else {
                plugin.databaseManager.endChestManager.saveEndChest(survivorEndChest);
            }

        } catch (Exception e) {
            Bukkit.getLogger().warning("[Hardcore Seasons]: Could not serialize end chest.\n" + e.getMessage());
        }
    }
}
