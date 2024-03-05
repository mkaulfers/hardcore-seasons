package us.mkaulfers.hardcoreseasons.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.interfaceimpl.EndChestDAOImpl;
import us.mkaulfers.hardcoreseasons.interfaceimpl.InventoryDAOImpl;
import us.mkaulfers.hardcoreseasons.interfaces.EndChestDAO;
import us.mkaulfers.hardcoreseasons.interfaces.InventoryDAO;
import us.mkaulfers.hardcoreseasons.models.Player;
import us.mkaulfers.hardcoreseasons.models.SurvivorInventory;
import us.mkaulfers.hardcoreseasons.models.TrackedEndChest;
import us.mkaulfers.hardcoreseasons.utils.InventoryUtils;

import java.util.UUID;

public class PlayerQuit implements Listener {
    HardcoreSeasons plugin;

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        try {
            EndChestDAO endChestDAO = new EndChestDAOImpl(plugin.database);

            UUID playerId = event.getPlayer().getUniqueId();
            Inventory inventory = event.getPlayer().getEnderChest();
            ItemStack[] endChestContents = inventory.getContents();

            String serializedInventory = InventoryUtils.itemStackArrayToBase64(endChestContents);
            TrackedEndChest survivorEndChest = new TrackedEndChest(0, playerId, 1, serializedInventory);
            endChestDAO.save(survivorEndChest);
        } catch (Exception e) {
            Bukkit.getLogger().severe("Failed to save end chest to database: " + e.getMessage());
        }

        try {
            InventoryDAO inventoryDAO = new InventoryDAOImpl(plugin.database);

            UUID playerId = event.getPlayer().getUniqueId();
            PlayerInventory playerInventory = event.getPlayer().getInventory();
            String serializedInventory = InventoryUtils.playerInventoryToBase64(playerInventory);

            SurvivorInventory survivorInventory = new SurvivorInventory(0, playerId, 1, serializedInventory);
            inventoryDAO.save(survivorInventory);
        } catch (Exception e) {
            Bukkit.getLogger().severe("Failed to save inventory to database: " + e.getMessage());
        }
    }

    public PlayerQuit(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }
}
