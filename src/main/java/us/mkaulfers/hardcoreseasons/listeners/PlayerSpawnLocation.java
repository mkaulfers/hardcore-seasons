package us.mkaulfers.hardcoreseasons.listeners;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;

public class PlayerSpawnLocation implements Listener {
    HardcoreSeasons plugin;

    public PlayerSpawnLocation(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerSpawnLocation(PlayerSpawnLocationEvent event) {
        if (!plugin.configManager.config.trackingEnabled) {
            return;
        }

        World mainWorld = plugin.worldManager.seasonMainWorld;
        event.setSpawnLocation(mainWorld.getSpawnLocation());
    }
}
