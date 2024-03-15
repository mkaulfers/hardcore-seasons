package us.mkaulfers.hardcoreseasons.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.orm.HParticipant;

import java.sql.Timestamp;

import static us.mkaulfers.hardcoreseasons.enums.LocalizationKey.DEATH_MESSAGE;

public class PlayerDeath implements Listener {
    HardcoreSeasons plugin;

    public PlayerDeath(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!plugin.configManager.config.trackingEnabled) {
            return;
        }

        event.getEntity().spigot().respawn();
        String result = plugin.configManager.localization.getLocalized(DEATH_MESSAGE);
        event.getEntity().kickPlayer(result);

        HParticipant participant = plugin.hDataSource.getParticipant(
                event.getEntity().getUniqueId(),
                plugin.currentSeasonNum
        );

        if (participant != null) {
            participant.setDead(true);
            participant.setLastOnline(new Timestamp(System.currentTimeMillis()));
            plugin.hDataSource.updateParticipant(participant);
        }
    }
}
