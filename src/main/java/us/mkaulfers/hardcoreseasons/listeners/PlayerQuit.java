package us.mkaulfers.hardcoreseasons.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.PlayerInventory;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.interfaceimpl.EndChestDAOImpl;
import us.mkaulfers.hardcoreseasons.interfaces.EndChestDAO;
import us.mkaulfers.hardcoreseasons.models.SurvivorEndChest;
import us.mkaulfers.hardcoreseasons.utils.InventoryUtils;

import java.util.UUID;

public class PlayerQuit implements Listener {
    HardcoreSeasons plugin;

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        try {
            EndChestDAO endChestDAO = new EndChestDAOImpl(plugin.database);

            UUID playerId = event.getPlayer().getUniqueId();
            PlayerInventory inventory = event.getPlayer().getInventory();
            String serializedInventory = InventoryUtils.playerInventoryToBase64(inventory);

            SurvivorEndChest survivorEndChest = new SurvivorEndChest(0, playerId, 1, serializedInventory);

            endChestDAO.save(survivorEndChest);
        } catch (Exception e) {
            Bukkit.getLogger().severe("Failed to save end chest to database: " + e.getMessage());
        }

    }

    public PlayerQuit(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }
}
