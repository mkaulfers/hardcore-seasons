package us.mkaulfers.hardcoreseasons.managers;

import org.bukkit.Bukkit;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.models.Season;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SeasonsManager {
    public List<Season> seasons;
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
                    List<Season> seasons = new ArrayList<>();

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

    private void processSeasonResultSet(ResultSet resultset, List<Season> seasons) throws SQLException {
        while (resultset.next()) {
            Season season = new Season();
            season.seasonId = resultset.getInt("season_id");
            season.startDate = resultset.getTimestamp("start_date");
            season.endDate = resultset.getTimestamp("end_date");
            seasons.add(season);
        }
    }
}
