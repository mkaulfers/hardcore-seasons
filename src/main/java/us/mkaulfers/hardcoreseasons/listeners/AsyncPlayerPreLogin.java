package us.mkaulfers.hardcoreseasons.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;

import static us.mkaulfers.hardcoreseasons.enums.LocalizationKey.SEASON_GENERATING;

public class AsyncPlayerPreLogin implements Listener {
    HardcoreSeasons plugin;

    public AsyncPlayerPreLogin(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerPreJoin(AsyncPlayerPreLoginEvent event) {
        if (!plugin.configManager.config.trackingEnabled) {
            return;
        }

        if (plugin.isGeneratingNewSeason) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, plugin.configManager.localization.getLocalized(SEASON_GENERATING));
        }
    }
}
