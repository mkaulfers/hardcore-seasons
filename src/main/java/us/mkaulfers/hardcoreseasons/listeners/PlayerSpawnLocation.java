package us.mkaulfers.hardcoreseasons.listeners;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.models.Participant;

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

        Participant participant = plugin.db.participants.getParticipant(
                event.getPlayer().getUniqueId(),
                plugin.currentSeasonNum
        );

        if (participant == null || participant.getLastWorld() == null) {
            if (!event.getSpawnLocation().getWorld().getName().contains("season_" + plugin.currentSeasonNum)) {
                World mainWorld = plugin.worldManager.seasonMainWorld;
                event.setSpawnLocation(mainWorld.getSpawnLocation());
            }
        } else {
            if (participant.getLastWorld().contains("_nether")) {
                World netherWorld = plugin.worldManager.seasonNetherWorld;
                Location playerLocation = new Location(
                        netherWorld,
                        participant.getLastX(),
                        participant.getLastY(),
                        participant.getLastZ()
                );

                event.setSpawnLocation(playerLocation);
            } else if (participant.getLastWorld().contains("_end")) {
                World endWorld = plugin.worldManager.seasonEndWorld;
                Location playerLocation = new Location(
                        endWorld,
                        participant.getLastX(),
                        participant.getLastY(),
                        participant.getLastZ()
                );

                event.setSpawnLocation(playerLocation);
            } else {
                World mainWorld = plugin.worldManager.seasonMainWorld;
                Location playerLocation = new Location(
                        mainWorld,
                        participant.getLastX(),
                        participant.getLastY(),
                        participant.getLastZ()
                );

                event.setSpawnLocation(playerLocation);
            }
        }
    }
}
