package us.mkaulfers.hardcoreseasons.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.interfaceimpl.ChestDAOImpl;
import us.mkaulfers.hardcoreseasons.interfaces.ChestDAO;
import us.mkaulfers.hardcoreseasons.models.TrackedChest;
import us.mkaulfers.hardcoreseasons.utils.InventoryUtils;

public class InventoryMoveItem implements Listener {
    HardcoreSeasons plugin;

    public InventoryMoveItem(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void inventoryMoveItemEvent(InventoryMoveItemEvent event) {
        Inventory source = event.getSource();
        Inventory destination = event.getDestination();

        ChestDAO chestDAO = new ChestDAOImpl(plugin.database);

        chestDAO.getAllForSeasonMap(plugin.currentSeasonNum).thenAccept(trackedChests -> {
            String world = source.getLocation().getWorld().getName();
            String sourceType = source.getType().name();
            String destinationType = destination.getType().name();

            int sourceX = source.getLocation().getBlockX();
            int sourceY = source.getLocation().getBlockY();
            int sourceZ = source.getLocation().getBlockZ();

            int destinationX = destination.getLocation().getBlockX();
            int destinationY = destination.getLocation().getBlockY();
            int destinationZ = destination.getLocation().getBlockZ();

            String sourceKey = world + ":" + sourceX + ":" + sourceY + ":" + sourceZ;
            String destinationKey = world + ":" + destinationX + ":" + destinationY + ":" + destinationZ;

            if (trackedChests.containsKey(sourceKey)) {
                TrackedChest oldChest = trackedChests.get(sourceKey);
                TrackedChest updatedChest = new TrackedChest(
                        oldChest.id,
                        oldChest.seasonId,
                        oldChest.x,
                        oldChest.y,
                        oldChest.z,
                        world,
                        sourceType,
                        InventoryUtils.itemStackArrayToBase64(source.getContents())
                );
                chestDAO.update(updatedChest);
            }

            if (trackedChests.containsKey(destinationKey)) {
                TrackedChest oldChest = trackedChests.get(destinationKey);
                TrackedChest updatedChest = new TrackedChest(
                        oldChest.id,
                        oldChest.seasonId,
                        oldChest.x,
                        oldChest.y,
                        oldChest.z,
                        world,
                        destinationType,
                        InventoryUtils.itemStackArrayToBase64(destination.getContents())
                );
                chestDAO.update(updatedChest);
            }
        });
    }
}
