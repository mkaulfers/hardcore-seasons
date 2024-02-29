package us.mkaulfers.hardcoreseasons.managers;

import org.bukkit.Bukkit;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.models.Survivor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
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

    public void loadSurvivors() {
        if (plugin.databaseManager.dataSource != null) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    Connection connection = plugin.databaseManager.dataSource.getConnection();
                    ResultSet resultset = connection.prepareStatement("SELECT * FROM survivors")
                            .executeQuery();

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
                    connection.close();
                    this.survivors = survivors;
                } catch (Exception e) {
                    Bukkit.getLogger().warning("[Hardcore Seasons]: Could not load survivors.");
                    e.printStackTrace();
                }
            });
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
                    connection.close();
                    survivors.add(survivor);
                } catch (Exception e) {
                    Bukkit.getLogger().warning("[Hardcore Seasons]: Could not save survivor.");
                }
            });
        }
    }

    public void updateSurvivorLastLogin(UUID survivorId, Timestamp timestamp) {
        if (plugin.databaseManager.dataSource != null) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    Connection connection = plugin.databaseManager.dataSource.getConnection();
                    String query = "UPDATE survivors SET last_login = ? WHERE survivor_id = ?";
                    java.sql.PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setTimestamp(1, timestamp);
                    preparedStatement.setString(2, survivorId.toString());
                    preparedStatement.execute();
                    connection.close();
                    survivors.stream()
                            .filter(survivor -> survivor.id.equals(survivorId))
                            .forEach(survivor -> survivor.lastLogin = timestamp);
                } catch (Exception e) {
                    Bukkit.getLogger().warning("[Hardcore Seasons]: Could not update survivor.");
                }
            });
        }
    }

    public void updateSurvivorIsDead(UUID survivorId, boolean isDead) {
        if (plugin.databaseManager.dataSource != null) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    Connection connection = plugin.databaseManager.dataSource.getConnection();
                    String query = "UPDATE survivors SET is_dead = ? WHERE survivor_id = ?";
                    java.sql.PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setBoolean(1, isDead);
                    preparedStatement.setString(2, survivorId.toString());
                    preparedStatement.execute();
                    connection.close();
                    survivors.stream()
                            .filter(survivor -> survivor.id.equals(survivorId))
                            .forEach(survivor -> survivor.isDead = isDead);
                } catch (Exception e) {
                    Bukkit.getLogger().warning("[Hardcore Seasons]: Could not update survivor.");
                }
            });
        }
    }
}
