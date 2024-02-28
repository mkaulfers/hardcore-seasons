package us.mkaulfers.hardcoreseasons.listeners;

import org.bukkit.Bukkit;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.models.Survivor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class PlayerJoined implements Listener {
    HardcoreSeasons plugin;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("Welcome to the server!");

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            /// Create new survivor, for the active season.
            UUID playerId = event.getPlayer().getUniqueId();

            if (!doesSurvivorExist(playerId)) {
                Survivor survivor = new Survivor();
                survivor.id = playerId;
                survivor.seasonId = plugin.databaseManager.seasonsManager.getActiveSeason().seasonId;
                survivor.joinDate = new Timestamp(new Date().getTime());
                survivor.lastLogin = new Timestamp(new Date().getTime());
                survivor.isDead = false;
                plugin.databaseManager.survivorsManager.saveSurvivor(survivor);
            } else {
                plugin.databaseManager.survivorsManager.updateSurvivorLastLogin(playerId, new Timestamp(new Date().getTime()));
            }
        });
    }

    private boolean doesSurvivorExist(UUID playerId) {
        return plugin
                .databaseManager
                .survivorsManager
                .survivors
                .stream()
                .anyMatch(survivor -> survivor.id.equals(playerId));
    }

    private boolean isPlayerDead(UUID playerId) {
        return plugin
                .databaseManager
                .survivorsManager
                .survivors
                .stream()
                .anyMatch(survivor -> survivor.id.equals(playerId) && survivor.isDead);
    }

    public PlayerJoined(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }
}
