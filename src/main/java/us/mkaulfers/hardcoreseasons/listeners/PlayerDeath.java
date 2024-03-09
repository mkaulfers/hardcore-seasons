package us.mkaulfers.hardcoreseasons.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.interfaceimpl.PlayerDAOImpl;
import us.mkaulfers.hardcoreseasons.interfaces.PlayerDAO;

import static us.mkaulfers.hardcoreseasons.models.LocalizationKey.DEATH_MESSAGE;

public class PlayerDeath implements Listener {
    HardcoreSeasons plugin;

    public PlayerDeath(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.getEntity().spigot().respawn();
        String result = plugin.configManager.localization.getLocalized(DEATH_MESSAGE) + " " + (plugin.currentSeasonNum + 1);
        event.getEntity().kickPlayer(result);

        PlayerDAO playerDAO = new PlayerDAOImpl(plugin.database);
        playerDAO.get(event.getEntity().getUniqueId(), plugin.currentSeasonNum)
                .thenAccept(player -> {
                    player.isDead = true;
                    playerDAO.update(player);
                });
    }
}
