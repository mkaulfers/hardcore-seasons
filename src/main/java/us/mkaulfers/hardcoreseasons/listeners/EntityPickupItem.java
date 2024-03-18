package us.mkaulfers.hardcoreseasons.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.utils.InventoryUtils;

public class EntityPickupItem implements Listener {
    HardcoreSeasons plugin;

    public EntityPickupItem(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        if (!plugin.configManager.config.trackingEnabled) {
            return;
        }

        Player player = (Player) event.getEntity();
        InventoryUtils.updatePlayerInventories(player, plugin);
    }
}
