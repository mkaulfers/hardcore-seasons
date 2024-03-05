package us.mkaulfers.hardcoreseasons.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.interfaceimpl.PlayerDAOImpl;
import us.mkaulfers.hardcoreseasons.interfaces.PlayerDAO;
import us.mkaulfers.hardcoreseasons.models.Player;

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
        int activeSeason = 1;

        PlayerDAO playerDAO = new PlayerDAOImpl(plugin.database);

        playerDAO.get(playerId, activeSeason)
                .thenAccept(p -> {
                    if (p == null) {
                        p = new Player(
                                0,
                                playerId,
                                activeSeason,
                                new Timestamp(new Date().getTime()),
                                new Timestamp(new Date().getTime()),
                                false
                        );

                        playerDAO.save(p);

                    } else if (p.isDead) {
                        String result = String.format("You are dead and cannot join the server until season %d.", activeSeason + 1);
                        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, result);
                    } else {
                        p.lastOnline = new Timestamp(new Date().getTime());
                        playerDAO.update(p);
                    }
                });
    }
}
