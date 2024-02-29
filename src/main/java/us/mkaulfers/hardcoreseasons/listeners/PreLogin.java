package us.mkaulfers.hardcoreseasons.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.models.Survivor;

import java.util.List;
import java.util.UUID;

public class PreLogin implements Listener {
    HardcoreSeasons plugin;

    public PreLogin(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID playerId = event.getUniqueId();
        List<Survivor> survivors = plugin.databaseManager.survivorsManager.survivors;

        if (survivors.stream().anyMatch(survivor -> survivor.id.equals(playerId) && survivor.isDead)) {
            int seasonId = plugin.databaseManager.seasonsManager.getActiveSeason().seasonId;

            // TODO: Replace this with a config message.
            // TODO: Make this check the database, not local memory.
            // Temporary fix implemented by updating at set intervals from config.
            String result = String.format("You are dead and cannot join the server until season %d.", seasonId + 1);
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, result);
        }
    }
}
