package us.mkaulfers.hardcoreseasons.listeners;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;

public class PlayerPortal implements Listener {
    HardcoreSeasons plugin;

    public PlayerPortal(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        if (!plugin.configManager.config.trackingEnabled) {
            return;
        }

        Player player = event.getPlayer();

        // Handle Nether portal logic
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            World mainWorld = plugin.worldManager.seasonMainWorld;
            World netherWorld = plugin.worldManager.seasonNetherWorld;

            Location currentLocation = player.getLocation();

            if (player.getWorld().equals(mainWorld)) {
                // Calculate Nether coordinates (Overworld to Nether)
                double newX = currentLocation.getX() / 8;
                double newZ = currentLocation.getZ() / 8;
                Location netherLocation = new Location(netherWorld, newX, currentLocation.getY(), newZ, currentLocation.getYaw(), currentLocation.getPitch());
                event.setTo(netherLocation);
            } else if (player.getWorld().equals(netherWorld)) {
                // Calculate Overworld coordinates (Nether to Overworld)
                double newX = currentLocation.getX() * 8;
                double newZ = currentLocation.getZ() * 8;
                Location overworldLocation = new Location(mainWorld, newX, currentLocation.getY(), newZ, currentLocation.getYaw(), currentLocation.getPitch());
                event.setTo(overworldLocation);
            }
        }

        // Handle End portal logic (can be customized further)
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_GATEWAY) {
            World mainWorld = plugin.worldManager.seasonMainWorld;
            World endWorld = plugin.worldManager.seasonEndWorld;

            if (player.getWorld().equals(endWorld)) {
                event.setTo(mainWorld.getSpawnLocation());
            } else if (player.getWorld().equals(mainWorld)) {
                Location obsidianPlatform = new Location(endWorld, 100, 50, 0);
                event.setTo(obsidianPlatform);
            }
        }
    }
}
