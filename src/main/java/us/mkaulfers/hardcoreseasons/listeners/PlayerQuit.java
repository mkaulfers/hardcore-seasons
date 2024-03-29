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

        InventoryUtils.updatePlayerInventories(event.getPlayer(), plugin);
    }

    public PlayerQuit(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }
}
