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
        // Has the player ever joined?
        // If not, add them to the database with the current season.

        // If the player has joined, and is alive, and current season matches their season let them join.
        // If their last season joined is lower than the current season, update them.
        //     create a new survivor entry for the current season.

        // If the player has joined, and is dead, kick them.


        /// Create new survivor, for the active season.


        // First time joining.
        if (!plugin.databaseManager.survivorsManager.doesSurvivorExist(playerId, activeSeason)) {
            Survivor survivor = new Survivor();
            survivor.id = playerId;
            survivor.seasonId = activeSeason;
            survivor.joinDate = new Timestamp(new Date().getTime());
            survivor.lastLogin = new Timestamp(new Date().getTime());
            survivor.isDead = false;
            plugin.databaseManager.survivorsManager.saveSurvivor(survivor);
        } else {
            // Not first time joining.
            // If the player is dead, kick them.
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
