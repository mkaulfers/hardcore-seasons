package usa.mkaulfers.hardcoreseasons.listeners;

import usa.mkaulfers.hardcoreseasons.storage.DBManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoined implements Listener {
    DBManager dbManager;
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("Welcome to the server!");
//        sqlHandler.handlePlayerJoin(event.getPlayer().getUniqueId());
    }

    public PlayerJoined(DBManager dbManager) {
        this.dbManager = dbManager;
    }
}
