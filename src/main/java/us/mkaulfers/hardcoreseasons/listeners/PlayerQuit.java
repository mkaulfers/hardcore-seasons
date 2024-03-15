package us.mkaulfers.hardcoreseasons.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.orm.HEndChest;
import us.mkaulfers.hardcoreseasons.orm.HInventory;
import us.mkaulfers.hardcoreseasons.utils.InventoryUtils;

import java.util.UUID;

public class PlayerQuit implements Listener {
    HardcoreSeasons plugin;

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!plugin.configManager.config.trackingEnabled) {
            return;
        }

        UUID playerId = event.getPlayer().getUniqueId();

        HEndChest hEndChest = plugin.hDataSource.getEndChest(playerId, plugin.currentSeasonNum);

        if (hEndChest != null) {
            // Update
            hEndChest.setSeasonId(plugin.currentSeasonNum);
            hEndChest.setPlayerId(playerId);
            hEndChest.setContents(InventoryUtils.itemStackArrayToBase64(event.getPlayer().getEnderChest().getContents()));
            plugin.hDataSource.updateEndChest(hEndChest);
        } else {
            // Insert
            hEndChest = new HEndChest();
            hEndChest.setSeasonId(plugin.currentSeasonNum);
            hEndChest.setPlayerId(playerId);
            hEndChest.setContents(InventoryUtils.itemStackArrayToBase64(event.getPlayer().getEnderChest().getContents()));
            plugin.hDataSource.setEndChest(hEndChest);
        }

        // Do the same for Inventory
        HInventory hInventory = plugin.hDataSource.getInventory(playerId, plugin.currentSeasonNum);

        if (hInventory != null) {
            // Update
            hInventory.setSeasonId(plugin.currentSeasonNum);
            hInventory.setPlayerId(playerId);
            hInventory.setContents(InventoryUtils.playerInventoryToBase64(event.getPlayer().getInventory()));
            plugin.hDataSource.updateInventory(hInventory);
        } else {
            // Insert
            hInventory = new HInventory();
            hInventory.setSeasonId(plugin.currentSeasonNum);
            hInventory.setPlayerId(playerId);
            hInventory.setContents(InventoryUtils.playerInventoryToBase64(event.getPlayer().getInventory()));
            plugin.hDataSource.setInventory(hInventory);
        }
    }

    public PlayerQuit(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }
}
