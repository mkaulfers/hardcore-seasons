package us.mkaulfers.hardcoreseasons.managers;

import org.bukkit.Bukkit;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.models.Season;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListSet;

public class SeasonsManager {
    public ConcurrentSkipListSet<Season> seasons;
    private final HardcoreSeasons plugin;

    public Season getActiveSeason() {
        // Return the season with the highest seasonId
        return seasons
                .stream()
                .max(Comparator.comparingInt(s -> s.seasonId))
                .orElse(null);
    }

    public SeasonsManager(HardcoreSeasons plugin) {
        this.plugin = plugin;
        loadSeasons();
    }

    public void loadSeasons() {
            CompletableFuture.runAsync(() -> {
                try {
                    Connection connection = plugin.databaseManager.dataSource.getConnection();
                    ConcurrentSkipListSet<Season> seasons = new ConcurrentSkipListSet<>();

                    ResultSet resultset = connection.prepareStatement("SELECT * FROM seasons").executeQuery();
                    processSeasonResultSet(resultset, seasons);

                    // If there are no seasons, create one. Then reload the seasons.
                    if (seasons.isEmpty()) {
                        connection.prepareStatement("INSERT INTO seasons (season_id, start_date, end_date) VALUES (1, NOW(), null)").execute();
                        ResultSet resultSetAfterInsert = connection.prepareStatement("SELECT * FROM seasons").executeQuery();
                        processSeasonResultSet(resultSetAfterInsert, seasons);
                    }
                    connection.close();
                    this.seasons = seasons;
                } catch (Exception e) {
                    Bukkit.getLogger().warning("[Hardcore Seasons]: Could not load seasons.");
                }
            });
    }

    private void processSeasonResultSet(ResultSet resultset, ConcurrentSkipListSet<Season> seasons) throws SQLException {
        while (resultset.next()) {
            Season season = new Season(
                    resultset.getInt("season_id"),
                    resultset.getTimestamp("start_date"),
                    resultset.getTimestamp("end_date")
            );
            seasons.add(season);
        }
    }
}
