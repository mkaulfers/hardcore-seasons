package us.mkaulfers.hardcoreseasons.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.models.SurvivorContainer;
import us.mkaulfers.hardcoreseasons.utils.InventoryUtils;

public class InventoryClose implements Listener {
    HardcoreSeasons plugin;

    public InventoryClose(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        int seasonId = plugin.databaseManager.seasonsManager.getActiveSeason().seasonId;
        int x = event.getInventory().getLocation().getBlockX();
        int y = event.getInventory().getLocation().getBlockY();
        int z = event.getInventory().getLocation().getBlockZ();
        String world = event.getInventory().getLocation().getWorld().getName();
        String type = event.getInventory().getType().name();

        Inventory inventory = event.getInventory();
        ItemStack[] contents = inventory.getContents();
        String contentsString = InventoryUtils.itemStackArrayToBase64(contents);

        if (plugin.databaseManager.containersManager.doesContainerExist(seasonId, x, y, z, world, type)) {
            SurvivorContainer container = new SurvivorContainer(seasonId, x, y, z, world, type, contentsString);
            plugin.databaseManager.containersManager.updateContainer(container);
        }
    }
}
