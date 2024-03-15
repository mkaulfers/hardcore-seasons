package us.mkaulfers.hardcoreseasons.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.orm.HParticipant;

import java.sql.Timestamp;
import java.util.UUID;

import static us.mkaulfers.hardcoreseasons.enums.LocalizationKey.DEATH_MESSAGE;

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

        HParticipant participant = plugin.hDataSource.getParticipant(playerId, plugin.currentSeasonNum);

        if (participant == null) {
            participant = new HParticipant();
            participant.setPlayerId(playerId);
            participant.setSeasonId(plugin.currentSeasonNum);
            participant.setJoinDate(new Timestamp(System.currentTimeMillis()));
            participant.setLastOnline(new Timestamp(System.currentTimeMillis()));
            participant.setDead(false);
            plugin.hDataSource.setParticipant(participant);
        } else if (participant.isDead()) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                String result = plugin.configManager.localization.getLocalized(DEATH_MESSAGE);
                event.getPlayer().kickPlayer(result);
            });
        } else {
            participant.setLastOnline(new Timestamp(System.currentTimeMillis()));
            plugin.hDataSource.updateParticipant(participant);
        }
    }
}
