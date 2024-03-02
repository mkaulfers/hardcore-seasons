package us.mkaulfers.hardcoreseasons.managers;

import org.bukkit.Bukkit;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.models.Survivor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListSet;

public class SurvivorsManager {
    public ConcurrentSkipListSet<Survivor> survivors;
    private final HardcoreSeasons plugin;

    public SurvivorsManager(HardcoreSeasons plugin) {
        this.plugin = plugin;
        loadSurvivors();
    }

    public boolean doesSurvivorExist(UUID playerId, int seasonId) {
        return survivors
                .stream()
                .anyMatch(survivor -> survivor.id.equals(playerId) &&
                        survivor.seasonId == seasonId);
    }

    public boolean isSurvivorDead(UUID playerId, int seasonId) {
        return survivors
                .stream()
                .anyMatch(survivor -> survivor.id.equals(playerId) &&
                        survivor.isDead && survivor.seasonId == seasonId);
    }

    public void loadSurvivors() {
        CompletableFuture.runAsync(() -> {
            try {
                Connection connection = plugin.databaseManager.dataSource.getConnection();
                ResultSet resultset = connection.prepareStatement("SELECT * FROM survivors")
                        .executeQuery();

                ConcurrentSkipListSet<Survivor> survivors = new ConcurrentSkipListSet<>();

                while (resultset.next()) {
                    Survivor survivor = new Survivor(
                            UUID.fromString(resultset.getString("survivor_id")),
                            resultset.getInt("season_id"),
                            resultset.getTimestamp("join_date"),
                            resultset.getTimestamp("last_online"),
                            resultset.getBoolean("is_dead")
                    );
                    survivors.add(survivor);
                }
                connection.close();
                this.survivors = survivors;
            } catch (Exception e) {
                Bukkit.getLogger().warning("[Hardcore Seasons]: Could not load survivors.\n" + e.getMessage());
            }
        });
    }

    public void saveSurvivor(Survivor survivor) {
        CompletableFuture.runAsync(() -> {
            try {
                Connection connection = plugin.databaseManager.dataSource.getConnection();
                String query = "INSERT INTO survivors (survivor_id, season_id, join_date, last_online, is_dead) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, survivor.id.toString());
                preparedStatement.setInt(2, survivor.seasonId);
                preparedStatement.setTimestamp(3, survivor.joinDate);
                preparedStatement.setTimestamp(4, survivor.lastOnline);
                preparedStatement.setBoolean(5, survivor.isDead);
                preparedStatement.execute();
                connection.close();
                survivors.add(survivor);
            } catch (Exception e) {
                Bukkit.getLogger().warning("[Hardcore Seasons]: Could not save survivor.\n" + e.getMessage());
            }
        });
    }

    public void updateSurvivorLastOnline(UUID survivorId, Timestamp timestamp) {
        CompletableFuture.runAsync(() -> {
            try {
                Connection connection = plugin.databaseManager.dataSource.getConnection();
                int activeSeason = plugin.databaseManager.seasonsManager.getActiveSeason().seasonId;

                String query = "UPDATE survivors SET last_online = ? WHERE survivor_id = ? AND season_id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setTimestamp(1, timestamp);
                preparedStatement.setString(2, survivorId.toString());
                preparedStatement.setInt(3, activeSeason);
                preparedStatement.execute();
                connection.close();
                survivors.stream()
                        .filter(survivor -> survivor.id.equals(survivorId) && survivor.seasonId == activeSeason)
                        .forEach(survivor -> survivor.lastOnline = timestamp);

            } catch (Exception e) {
                Bukkit.getLogger().warning("[Hardcore Seasons]: Could not update survivor.\n" + e.getMessage());
            }
        });
    }

    public void updateSurvivorIsDead(UUID survivorId, boolean isDead) {
        CompletableFuture.runAsync(() -> {
            try {
                Connection connection = plugin.databaseManager.dataSource.getConnection();
                int activeSeason = plugin.databaseManager.seasonsManager.getActiveSeason().seasonId;

                String query = "UPDATE survivors SET is_dead = ? WHERE survivor_id = ? AND season_id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setBoolean(1, isDead);
                preparedStatement.setString(2, survivorId.toString());
                preparedStatement.setInt(3, activeSeason);
                preparedStatement.execute();
                connection.close();
                survivors.stream()
                        .filter(survivor -> survivor.id.equals(survivorId) && survivor.seasonId == activeSeason)
                        .forEach(survivor -> survivor.isDead = isDead);
            } catch (Exception e) {
                Bukkit.getLogger().warning("[Hardcore Seasons]: Could not update survivor.\n" + e.getMessage());
            }
        });
    }
}
