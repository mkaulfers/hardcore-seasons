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

        try {
            PlayerDAO playerDAO = new PlayerDAOImpl(plugin.database);
            Player player = playerDAO.get(event.getEntity().getUniqueId(), 1);
            player.isDead = true;
            playerDAO.update(player);
        } catch (Exception e) {
            Bukkit.getLogger().severe("Failed to get player from database: " + e.getMessage());
        }
        String result = String.format("You have died, join back in season %d.", 1);
        event.getEntity().kickPlayer(result);
    }
}
