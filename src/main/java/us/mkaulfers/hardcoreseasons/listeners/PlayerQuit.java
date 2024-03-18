package us.mkaulfers.hardcoreseasons.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.models.EndChest;
import us.mkaulfers.hardcoreseasons.models.ParticipantInventory;
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

        EndChest endChest = plugin.db.endChests.getEndChest(playerId, plugin.currentSeasonNum);

        if (endChest != null) {
            // Update
            endChest.setSeasonId(plugin.currentSeasonNum);
            endChest.setPlayerId(playerId);
            endChest.setContents(InventoryUtils.itemStackArrayToBase64(event.getPlayer().getEnderChest().getContents()));
            plugin.db.endChests.updateEndChest(endChest);
        } else {
            // Insert
            endChest = new EndChest();
            endChest.setSeasonId(plugin.currentSeasonNum);
            endChest.setPlayerId(playerId);
            endChest.setContents(InventoryUtils.itemStackArrayToBase64(event.getPlayer().getEnderChest().getContents()));
            plugin.db.endChests.setEndChest(endChest);
        }

        // Do the same for Inventory
        ParticipantInventory participantInventory = plugin.db.inventories.getInventory(playerId, plugin.currentSeasonNum);

        if (participantInventory != null) {
            // Update
            participantInventory.setSeasonId(plugin.currentSeasonNum);
            participantInventory.setPlayerId(playerId);
            participantInventory.setContents(InventoryUtils.playerInventoryToBase64(event.getPlayer().getInventory()));
            plugin.db.inventories.updateInventory(participantInventory);
        } else {
            // Insert
            participantInventory = new ParticipantInventory();
            participantInventory.setSeasonId(plugin.currentSeasonNum);
            participantInventory.setPlayerId(playerId);
            participantInventory.setContents(InventoryUtils.playerInventoryToBase64(event.getPlayer().getInventory()));
            plugin.db.inventories.setInventory(participantInventory);
        }
    }

    public PlayerQuit(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }
}
