package us.mkaulfers.hardcoreseasons.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.interfaceimpl.ChestDAOImpl;
import us.mkaulfers.hardcoreseasons.interfaces.ChestDAO;
import us.mkaulfers.hardcoreseasons.models.TrackedChest;
import us.mkaulfers.hardcoreseasons.utils.InventoryUtils;

import java.sql.SQLException;
import java.util.List;

public class InventoryClose implements Listener {
    HardcoreSeasons plugin;

    public InventoryClose(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if(event.getInventory().getHolder() instanceof BlockState) {
            int x = event.getInventory().getLocation().getBlockX();
            int y = event.getInventory().getLocation().getBlockY();
            int z = event.getInventory().getLocation().getBlockZ();
            String world = event.getInventory().getLocation().getWorld().getName();
            String type = event.getInventory().getType().name();
            String contentsString = InventoryUtils.itemStackArrayToBase64(event.getInventory().getContents());

            try {
                ChestDAO chestDAO = new ChestDAOImpl(plugin.database);
                TrackedChest dbChest = chestDAO.get(x, y, z, world, type);
                TrackedChest updated = new TrackedChest(dbChest.id, 1, x, y, z, world, type, contentsString);
                chestDAO.update(updated);
            } catch (SQLException e) {
                Bukkit.getLogger().severe("Failed to get chest from database: " + e.getMessage());
            }
        }
    }
}
