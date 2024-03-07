package us.mkaulfers.hardcoreseasons.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.interfaceimpl.PlayerDAOImpl;
import us.mkaulfers.hardcoreseasons.interfaces.PlayerDAO;
import us.mkaulfers.hardcoreseasons.models.Player;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class PlayerJoin implements Listener {
    HardcoreSeasons plugin;

    public PlayerJoin(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();

        PlayerDAO playerDAO = new PlayerDAOImpl(plugin.database);

        playerDAO.get(playerId, plugin.activeSeason)
                .thenAccept(p -> {
                    if (p == null) {
                        p = new Player(
                                0,
                                playerId,
                                plugin.activeSeason,
                                new Timestamp(new Date().getTime()),
                                new Timestamp(new Date().getTime()),
                                false
                        );

                        playerDAO.save(p);

                    } else if (p.isDead) {
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            String result = ChatColor.DARK_RED + "You have died and must wait until"+ ChatColor.GOLD +" Season " + ChatColor.AQUA + (plugin.activeSeason + 1) + ChatColor.DARK_RED + ".";
                            event.getPlayer().kickPlayer(result);
                        });
                    } else {
                        p.lastOnline = new Timestamp(new Date().getTime());
                        playerDAO.update(p);
                    }
                });
    }
}