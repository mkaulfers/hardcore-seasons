package us.mkaulfers.hardcoreseasons.listeners;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;

import static org.bukkit.Bukkit.getWorld;

public class PlayerPortal implements Listener {
    HardcoreSeasons plugin;

    public PlayerPortal(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();

        if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            int seasonNum = plugin.currentSeasonNum;
            World mainWorld = getMainWorld(seasonNum);
            World netherWorld = getNetherWorld(seasonNum);

            if (player.getWorld() == mainWorld) {
                event.setTo(netherWorld.getSpawnLocation());
            } else if (player.getWorld() == netherWorld) {
                event.setTo(mainWorld.getSpawnLocation());
            }
        }

        if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_GATEWAY) {
            int seasonNum = plugin.currentSeasonNum;
            World mainWorld = getMainWorld(seasonNum);
            World endWorld = getEndWorld(seasonNum);

            if (player.getWorld() == endWorld) {
                event.setTo(mainWorld.getSpawnLocation());
            }else if (player.getWorld() == mainWorld) {
                event.setTo(endWorld.getSpawnLocation());
            }
        }
    }

    private World getMainWorld(int seasonNum) {
        return getWorld("season_" + seasonNum);
    }

    private World getNetherWorld(int seasonNum) {
        return getWorld("season_" + seasonNum + "_nether");
    }

    private World getEndWorld(int seasonNum) {
        return getWorld("season_" + seasonNum + "_the_end");
    }
}
