package us.mkaulfers.hardcoreseasons.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.interfaceimpl.PlayerDAOImpl;
import us.mkaulfers.hardcoreseasons.interfaces.PlayerDAO;
import us.mkaulfers.hardcoreseasons.models.Participant;

import java.sql.Timestamp;
import java.util.UUID;

import static us.mkaulfers.hardcoreseasons.models.LocalizationKey.DEATH_MESSAGE;

public class PlayerJoin implements Listener {
    HardcoreSeasons plugin;

    public PlayerJoin(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!plugin.configManager.config.trackingEnabled) {
            return;
        }

        UUID playerId = event.getPlayer().getUniqueId();

        PlayerDAO playerDAO = new PlayerDAOImpl(plugin.database);

        playerDAO.get(playerId, plugin.currentSeasonNum)
                .thenAccept(p -> {
                    if (p == null) {
                        p = new Participant(
                                0,
                                playerId,
                                plugin.currentSeasonNum,
                                new Timestamp(System.currentTimeMillis()),
                                new Timestamp(System.currentTimeMillis()),
                                false
                        );

                        playerDAO.save(p);

                    } else if (p.isDead) {
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            String result = plugin.configManager.localization.getLocalized(DEATH_MESSAGE) + " " + (plugin.currentSeasonNum + 1);
                            event.getPlayer().kickPlayer(result);
                        });
                    } else {
                        p.lastOnline = new Timestamp(System.currentTimeMillis());
                        playerDAO.update(p);
                    }
                });
    }
}
