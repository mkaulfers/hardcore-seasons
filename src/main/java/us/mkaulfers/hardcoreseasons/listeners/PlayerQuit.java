package us.mkaulfers.hardcoreseasons.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.PlayerInventory;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
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

        try {
            int activeSeason = plugin.databaseManager.seasonsManager.getActiveSeason().seasonId;
            PlayerInventory inventory = event.getPlayer().getInventory();
            String serializedInventory = InventoryUtils.playerInventoryToBase64(inventory);

            SurvivorInventory survivorInventory = new SurvivorInventory(playerId, activeSeason, serializedInventory);
            if (plugin.databaseManager.inventoriesManager.doesInventoryExist(playerId, activeSeason)) {
                plugin.databaseManager.inventoriesManager.updateInventory(survivorInventory);
            } else {
                plugin.databaseManager.inventoriesManager.saveInventory(survivorInventory);
            }

        } catch (Exception e) {
            Bukkit.getLogger().warning("[Hardcore Seasons]: Could not serialize inventory.\n" + e.getMessage());
        }
    }

    public PlayerQuit(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }
}
