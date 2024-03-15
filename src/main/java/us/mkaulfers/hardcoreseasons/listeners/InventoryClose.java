package us.mkaulfers.hardcoreseasons.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.InventoryHolder;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.orm.HTrackedContainer;
import us.mkaulfers.hardcoreseasons.utils.InventoryUtils;

public class InventoryClose implements Listener {
    HardcoreSeasons plugin;

    public InventoryClose(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!plugin.configManager.config.trackingEnabled) {
            return;
        }

        InventoryHolder holder = event.getInventory().getHolder();

        if (holder instanceof BlockInventoryHolder) {
            HTrackedContainer trackedContainer = plugin.hDataSource.getTrackedContainer(
                    plugin.currentSeasonNum,
                    event.getInventory().getLocation().getBlockX(),
                    event.getInventory().getLocation().getBlockY(),
                    event.getInventory().getLocation().getBlockZ(),
                    event.getInventory().getLocation().getWorld().getName(),
                    event.getInventory().getType().name()
            );

            if (trackedContainer != null) {
                String contents = InventoryUtils.itemStackArrayToBase64(event.getInventory().getContents());
                trackedContainer.setContents(contents);
                plugin.hDataSource.updateTrackedContainer(trackedContainer);
            }
        }
    }
}
