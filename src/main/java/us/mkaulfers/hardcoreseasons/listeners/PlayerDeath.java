package us.mkaulfers.hardcoreseasons.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.interfaceimpl.PlayerDAOImpl;
import us.mkaulfers.hardcoreseasons.interfaces.PlayerDAO;
import us.mkaulfers.hardcoreseasons.models.Player;

public class PlayerDeath implements Listener {
    HardcoreSeasons plugin;

    public PlayerDeath(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.getEntity().spigot().respawn();

        PlayerDAO playerDAO = new PlayerDAOImpl(plugin.database);
        playerDAO.get(event.getEntity().getUniqueId(), 1)
                .thenAccept(player -> {
                    player.isDead = true;
                    playerDAO.update(player);
                    String result = String.format("You have died, join back in season %d.", 1);
                    event.getEntity().kickPlayer(result);
                });
    }
}
