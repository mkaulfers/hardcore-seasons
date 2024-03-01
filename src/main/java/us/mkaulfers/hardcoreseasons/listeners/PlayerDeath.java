package us.mkaulfers.hardcoreseasons.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;

public class PlayerDeath implements Listener {
    HardcoreSeasons plugin;
    public PlayerDeath(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.getEntity().spigot().respawn();
        int activeSeason = plugin.databaseManager.seasonsManager.getActiveSeason().seasonId;
        plugin.databaseManager.survivorsManager.updateSurvivorIsDead(event.getEntity().getUniqueId(), true);
        String result = String.format("You have died, join back in season %d.", activeSeason + 1);
        event.getEntity().kickPlayer(result);
    }
}
