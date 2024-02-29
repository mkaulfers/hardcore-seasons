package us.mkaulfers.hardcoreseasons.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;

public class OnPlayerDeath implements Listener {
    HardcoreSeasons plugin;
    public OnPlayerDeath(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }

    /// Ban the player from the server, and kick them.
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.getEntity().spigot().respawn();
        int seasonId = plugin.databaseManager.seasonsManager.getActiveSeason().seasonId;
        plugin.databaseManager.survivorsManager.updateSurvivorIsDead(event.getEntity().getUniqueId(), true);
        String result = String.format("You have died, join back in season %d.", seasonId + 1);
        event.getEntity().kickPlayer(result);
    }
}
