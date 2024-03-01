package us.mkaulfers.hardcoreseasons.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.models.Survivor;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class PreLogin implements Listener {
    HardcoreSeasons plugin;

    public PreLogin(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID playerId = event.getUniqueId();
        int activeSeason = plugin.databaseManager.seasonsManager.getActiveSeason().seasonId;

        if (!plugin.databaseManager.survivorsManager.doesSurvivorExist(playerId, activeSeason)) {
            Survivor survivor = new Survivor();
            survivor.id = playerId;
            survivor.seasonId = activeSeason;
            survivor.joinDate = new Timestamp(new Date().getTime());
            survivor.lastLogin = new Timestamp(new Date().getTime());
            survivor.isDead = false;
            plugin.databaseManager.survivorsManager.saveSurvivor(survivor);
        } else {
            if (plugin.databaseManager.survivorsManager.isSurvivorDead(playerId, activeSeason)) {
                // TODO: Replace this with a config message.
                // TODO: Make this check the database, not local memory.
                // Temporary fix implemented by updating at set intervals from config.
                String result = String.format("You are dead and cannot join the server until season %d.", activeSeason + 1);
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, result);
            } else {
                plugin.databaseManager.survivorsManager.updateSurvivorLastLogin(playerId, new Timestamp(new Date().getTime()));
            }
        }
    }
}
