package us.mkaulfers.hardcoreseasons.managers;

import org.bukkit.Bukkit;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.models.Survivor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SurvivorsManager {
    public List<Survivor> survivors;
    private final HardcoreSeasons plugin;

    public SurvivorsManager(HardcoreSeasons plugin) {
        this.plugin = plugin;
        loadSurvivors();
    }

    void loadSurvivors() {
        if (plugin.databaseManager.dataSource != null) {
            try {
                Connection connection = plugin.databaseManager.dataSource.getConnection();
                ResultSet resultset = connection.prepareStatement("SELECT * FROM survivors").executeQuery();

                List<Survivor> survivors = new ArrayList<>();

                while (resultset.next()) {
                    Survivor survivor = new Survivor();
                    survivor.id = UUID.fromString(resultset.getString("survivor_id"));
                    survivor.seasonId = resultset.getInt("season_id");
                    survivor.joinDate = resultset.getTimestamp("join_date");
                    survivor.lastLogin = resultset.getTimestamp("last_login");
                    survivor.isDead = resultset.getBoolean("is_dead");
                    survivors.add(survivor);
                }

                this.survivors = survivors;
            } catch (Exception e) {
                Bukkit.getLogger().warning("[Hardcore Seasons]: Could not load survivors.");
            }
        }
        plugin.databaseManager.connect();
    }

    public void saveSurvivor(Survivor survivor) {
        if (plugin.databaseManager.dataSource != null) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    Connection connection = plugin.databaseManager.dataSource.getConnection();
                    String query = "INSERT INTO survivors (survivor_id, season_id, join_date, last_login, is_dead) VALUES (?, ?, ?, ?, ?)";
                    java.sql.PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, survivor.id.toString());
                    preparedStatement.setInt(2, survivor.seasonId);
                    preparedStatement.setTimestamp(3, survivor.joinDate);
                    preparedStatement.setTimestamp(4, survivor.lastLogin);
                    preparedStatement.setBoolean(5, survivor.isDead);
                    preparedStatement.execute();
                    survivors.add(survivor);
                } catch (Exception e) {
                    Bukkit.getLogger().warning("[Hardcore Seasons]: Could not save survivor.");
                }
            });
        }
    }
}
