package us.mkaulfers.hardcoreseasons.listeners;

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
import us.mkaulfers.hardcoreseasons.models.SurvivorInventory;
import us.mkaulfers.hardcoreseasons.models.TrackedEndChest;
import us.mkaulfers.hardcoreseasons.utils.InventoryUtils;

import java.util.UUID;

public class PlayerQuit implements Listener {
    HardcoreSeasons plugin;

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();

        EndChestDAO endChestDAO = new EndChestDAOImpl(plugin.database);
        Inventory inventory = event.getPlayer().getEnderChest();
        ItemStack[] endChestContents = inventory.getContents();
        String serializedEndChest = InventoryUtils.itemStackArrayToBase64(endChestContents);
        TrackedEndChest survivorEndChest = new TrackedEndChest(0, playerId, 1, serializedEndChest);
        endChestDAO.save(survivorEndChest);

        InventoryDAO inventoryDAO = new InventoryDAOImpl(plugin.database);
        PlayerInventory playerInventory = event.getPlayer().getInventory();
        String serializedInventory = InventoryUtils.playerInventoryToBase64(playerInventory);
        SurvivorInventory survivorInventory = new SurvivorInventory(0, playerId, 1, serializedInventory);
        inventoryDAO.save(survivorInventory);
    }

    public PlayerQuit(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }
}
