package us.mkaulfers.hardcoreseasons.managers;

import org.bukkit.Bukkit;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.models.SurvivorEndChest;
import us.mkaulfers.hardcoreseasons.storage.DatabaseManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EndChestsManager {
    public List<SurvivorEndChest> endChests;
    private final HardcoreSeasons plugin;

    public EndChestsManager(HardcoreSeasons plugin) {
        this.plugin = plugin;
        loadEndChests();
    }

    public void loadEndChests() {
        if (plugin.databaseManager.dataSource != null) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    Connection connection = plugin.databaseManager.dataSource.getConnection();
                    ResultSet resultset = connection.prepareStatement("SELECT * FROM survivors_end_chests").executeQuery();

                    List<SurvivorEndChest> endChests = new ArrayList<>(resultset.getFetchSize());

                    while (resultset.next()) {
                        SurvivorEndChest endChest = new SurvivorEndChest();
                        endChest.playerUUID = UUID.fromString(resultset.getString("survivor_id"));
                        endChest.seasonId = resultset.getInt("season_id");
                        endChest.contents = resultset.getString("contents");
                        endChests.add(endChest);
                    }
                    connection.close();
                    this.endChests = endChests;
                } catch (Exception e) {
                    Bukkit.getLogger().warning("[Hardcore Seasons]: Could not load end chests.");
                }
            });
        }
        plugin.databaseManager.connect();
    }
}
