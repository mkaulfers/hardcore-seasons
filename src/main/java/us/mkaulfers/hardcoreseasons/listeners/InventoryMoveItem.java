package us.mkaulfers.hardcoreseasons.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.Inventory;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.models.TrackedContainer;
import us.mkaulfers.hardcoreseasons.utils.InventoryUtils;

public class InventoryMoveItem implements Listener {
    HardcoreSeasons plugin;

    public InventoryMoveItem(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void inventoryMoveItemEvent(InventoryMoveItemEvent event) {
        if (!plugin.configManager.config.trackingEnabled) {
            return;
        }

        Inventory source = event.getSource();
        Inventory destination = event.getDestination();

        if (source.getHolder() instanceof BlockInventoryHolder) {
            TrackedContainer trackedContainer = plugin.db.containers.getTrackedContainer(
                    plugin.activeSeason.getSeasonId(),
                    source.getLocation().getBlockX(),
                    source.getLocation().getBlockY(),
                    source.getLocation().getBlockZ(),
                    source.getLocation().getWorld().getName(),
                    source.getType().name()
            );

            if (trackedContainer != null) {
                String contents = InventoryUtils.itemStackArrayToBase64(source.getContents());
                trackedContainer.setContents(contents);
                plugin.db.containers.updateTrackedContainer(trackedContainer);
            }
        }

        if (destination.getHolder() instanceof BlockInventoryHolder) {
            TrackedContainer trackedContainer = plugin.db.containers.getTrackedContainer(
                    plugin.activeSeason.getSeasonId(),
                    destination.getLocation().getBlockX(),
                    destination.getLocation().getBlockY(),
                    destination.getLocation().getBlockZ(),
                    destination.getLocation().getWorld().getName(),
                    destination.getType().name()
            );

            if (trackedContainer != null) {
                String contents = InventoryUtils.itemStackArrayToBase64(destination.getContents());
                trackedContainer.setContents(contents);
                plugin.db.containers.updateTrackedContainer(trackedContainer);
            }
        }
    }
}
