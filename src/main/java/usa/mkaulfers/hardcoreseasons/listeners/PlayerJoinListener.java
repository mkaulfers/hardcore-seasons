package usa.mkaulfers.hardcoreseasons.listeners;

import usa.mkaulfers.hardcoreseasons.storage.SQLHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    SQLHandler sqlHandler;
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("Welcome to the server!");
    }

    public PlayerJoinListener(SQLHandler sqlHandler) {
        this.sqlHandler = sqlHandler;
    }
}
