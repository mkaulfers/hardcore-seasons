package usa.mkaulfers.hardcoreseasons.listeners;

import usa.mkaulfers.hardcoreseasons.storage.DatabaseManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoined implements Listener {
    DatabaseManager databaseManager;
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("Welcome to the server!");
//        sqlHandler.handlePlayerJoin(event.getPlayer().getUniqueId());
    }

    public PlayerJoined(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }
}
