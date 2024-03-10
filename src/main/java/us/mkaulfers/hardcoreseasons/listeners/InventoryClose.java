package us.mkaulfers.hardcoreseasons.listeners;

import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.InventoryHolder;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.interfaceimpl.ChestDAOImpl;
import us.mkaulfers.hardcoreseasons.interfaces.ChestDAO;
import us.mkaulfers.hardcoreseasons.models.TrackedChest;
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
            int x = event.getInventory().getLocation().getBlockX();
            int y = event.getInventory().getLocation().getBlockY();
            int z = event.getInventory().getLocation().getBlockZ();
            String world = event.getInventory().getLocation().getWorld().getName();
            String type = event.getInventory().getType().name();
            String contentsString = InventoryUtils.itemStackArrayToBase64(event.getInventory().getContents());

            ChestDAO chestDAO = new ChestDAOImpl(plugin.database);

            chestDAO.get(x, y, z, world, type)
                    .thenAccept(chest -> {
                        TrackedChest updated = new TrackedChest(chest.id, plugin.currentSeasonNum, x, y, z, world, type, contentsString);
                        chestDAO.update(updated);
                    });
        }

        if (holder instanceof DoubleChest) {
            DoubleChest doubleChest = (DoubleChest) holder;
            Chest leftChest = (Chest) doubleChest.getLeftSide();
            Chest rightChest = (Chest) doubleChest.getRightSide();

            int leftX = leftChest.getLocation().getBlockX();
            int leftY = leftChest.getLocation().getBlockY();
            int leftZ = leftChest.getLocation().getBlockZ();

            int rightX = rightChest.getLocation().getBlockX();
            int rightY = rightChest.getLocation().getBlockY();
            int rightZ = rightChest.getLocation().getBlockZ();

            String world = leftChest.getLocation().getWorld().getName();
            String type = leftChest.getType().name();

            String leftContentsString = InventoryUtils.itemStackArrayToBase64(leftChest.getInventory().getContents());
            String rightContentsString = InventoryUtils.itemStackArrayToBase64(rightChest.getInventory().getContents());

            ChestDAO chestDAO = new ChestDAOImpl(plugin.database);

            chestDAO.get(leftX, leftY, leftZ, world, type)
                    .thenAccept(chest -> {
                        TrackedChest updated = new TrackedChest(chest.id, plugin.currentSeasonNum, leftX, leftY, leftZ, world, type, leftContentsString);
                        chestDAO.update(updated);
                    });

            chestDAO.get(rightX, rightY, rightZ, world, type)
                    .thenAccept(chest -> {
                        TrackedChest updated = new TrackedChest(chest.id, plugin.currentSeasonNum, rightX, rightY, rightZ, world, type, rightContentsString);
                        chestDAO.update(updated);
                    });
        }
    }
}
