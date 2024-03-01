package us.mkaulfers.hardcoreseasons.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;

import java.util.UUID;

public class PlayerJoin implements Listener {
    HardcoreSeasons plugin;

    public PlayerJoin(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();
        int activeSeason = plugin.databaseManager.seasonsManager.getActiveSeason().seasonId;
        ItemStack[] savedInventories = plugin.databaseManager.inventoriesManager.getInventory(playerUUID, activeSeason);
        event.getPlayer().getInventory().setContents(savedInventories);

    }
}
