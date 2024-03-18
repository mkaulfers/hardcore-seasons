package us.mkaulfers.hardcoreseasons.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.utils.InventoryUtils;

public class PlayerDropItem implements Listener {
    HardcoreSeasons plugin;

    public PlayerDropItem(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (!plugin.configManager.config.trackingEnabled) {
            return;
        }

        InventoryUtils.updatePlayerInventories(event.getPlayer(), plugin);
    }
}
