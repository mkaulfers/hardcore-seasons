package us.mkaulfers.hardcoreseasons.interfaceimpl;

import org.bukkit.Bukkit;
import us.mkaulfers.hardcoreseasons.interfaces.SeasonDAO;
import us.mkaulfers.hardcoreseasons.models.Database;
import us.mkaulfers.hardcoreseasons.models.Season;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SeasonDAOImpl implements SeasonDAO {
    Database database;

    public SeasonDAOImpl(Database database) {
        this.database = database;
    }

    @Override
    public CompletableFuture<Integer> getActiveSeasonId() {
        return CompletableFuture.supplyAsync(() -> {
            try(Connection connection = database.getConnection()) {
                int seasonId = 0;

                // Get the active season, with the highest season_id
                String query = "SELECT season_id FROM seasons ORDER BY season_id DESC LIMIT 1";
                PreparedStatement ps = connection.prepareStatement(query);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    seasonId = rs.getInt("season_id");
                }

                return seasonId;
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to get activeSeasonId." +e.getMessage());
                return 0;
            }
        });
    }

    @Override
    public CompletableFuture<Season> get(int id) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = database.getConnection()) {
                Season season = null;

                String query = "SELECT * FROM seasons WHERE id = ?";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setInt(1, id);

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    season = new Season(
                            rs.getInt("id"),
                            rs.getInt("season_id"),
                            rs.getDate("start_date"),
                            rs.getDate("end_date")
                    );
                }

                return season;
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to get season." +e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<List<Season>> getAll(){
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = database.getConnection()) {
                List<Season> seasons = new ArrayList<>();

                String query = "SELECT * FROM seasons";
                PreparedStatement ps = connection.prepareStatement(query);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    Season season = new Season(
                            rs.getInt("id"),
                            rs.getInt("season_id"),
                            rs.getDate("start_date"),
                            rs.getDate("end_date")
                    );
                    seasons.add(season);
                }

                return seasons;
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to get all seasons." +e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Integer> save(Season season) {
        return CompletableFuture.supplyAsync(() -> {
            try(Connection connection = database.getConnection()) {
                String query = "INSERT INTO seasons (id, start_date, end_date) VALUES (?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE start_date = ?, end_date = ?";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setInt(1, season.seasonId);
                ps.setDate(2, (Date) season.startDate);
                ps.setDate(3, (Date) season.endDate);
                ps.setDate(4, (Date) season.startDate);
                ps.setDate(5, (Date) season.endDate);

                return ps.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to save season." +e.getMessage());
                return 0;
            }
        });
    }

    @Override
    public CompletableFuture<Integer> insert(Season season) {
        return CompletableFuture.supplyAsync(() -> {
            try(Connection connection = database.getConnection()) {
                String query = "INSERT INTO seasons (id, start_date, end_date) VALUES (?, ?, ?)";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setInt(1, season.seasonId);
                ps.setDate(2, (Date) season.startDate);
                ps.setDate(3, (Date) season.endDate);

                return ps.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to insert season." +e.getMessage());
                return 0;
            }
        });
    }

    @Override
    public CompletableFuture<Integer> update(Season season) {
        return CompletableFuture.supplyAsync(() -> {
            try(Connection connection = database.getConnection()) {
                String query = "UPDATE seasons SET start_date = ?, end_date = ? WHERE id = ?";

                PreparedStatement ps = connection.prepareStatement(query);
                ps.setDate(1, (Date) season.startDate);
                ps.setDate(2, (Date) season.endDate);
                ps.setInt(3, season.seasonId);

                return ps.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to update season." +e.getMessage());
                return 0;
            }
        });
    }

    @Override
    public CompletableFuture<Integer> delete(Season season) {
        return CompletableFuture.supplyAsync(() -> {
            try(Connection connection = database.getConnection()) {
                String query = "DELETE FROM seasons WHERE id = ?";

                PreparedStatement ps = connection.prepareStatement(query);
                ps.setInt(1, season.seasonId);

                return ps.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to delete season." +e.getMessage());
                return 0;
            }
        });
    }
}
