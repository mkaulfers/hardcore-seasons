package us.mkaulfers.hardcoreseasons.interfaceimpl;

import org.bukkit.Bukkit;
import us.mkaulfers.hardcoreseasons.interfaces.SeasonRewardDAO;
import us.mkaulfers.hardcoreseasons.models.Database;
import us.mkaulfers.hardcoreseasons.models.SeasonReward;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SeasonRewardDAOImpl implements SeasonRewardDAO {
    Database database;

    public SeasonRewardDAOImpl(Database database) {
        this.database = database;
    }

    @Override
    public CompletableFuture<List<SeasonReward>> getPlayerRewards(String playerId) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = database.getConnection()) {
                List<SeasonReward> seasonRewards = new ArrayList<>();

                String query = "SELECT * FROM seasonRewards WHERE player_id = ?";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setString(1, playerId);

                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    seasonRewards.add(new SeasonReward(
                            rs.getInt("id"),
                            rs.getInt("season_id"),
                            UUID.fromString(rs.getString("player_id")),
                            rs.getString("contents"),
                            rs.getBoolean("redeemed")
                            ));
                }

                return seasonRewards;
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to get player rewards." + e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<SeasonReward> get(int id) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = database.getConnection()) {
                SeasonReward seasonReward = null;

                String query = "SELECT * FROM rewards WHERE id = ?";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setInt(1, id);

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    seasonReward = new SeasonReward(
                            rs.getInt("id"),
                            rs.getInt("season_id"),
                            UUID.fromString(rs.getString("player_id")),
                            rs.getString("contents"),
                            rs.getBoolean("redeemed")
                    );
                }

                return seasonReward;
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to get reward." + e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<List<SeasonReward>> getAll() {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = database.getConnection()) {
                List<SeasonReward> seasonRewards = null;

                String query = "SELECT * FROM seasonRewards";
                PreparedStatement ps = connection.prepareStatement(query);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    seasonRewards.add(new SeasonReward(
                            rs.getInt("id"),
                            rs.getInt("season_id"),
                            UUID.fromString(rs.getString("player_id")),
                            rs.getString("contents"),
                            rs.getBoolean("redeemed")
                    ));
                }

                return seasonRewards;
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to get all rewards." + e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Integer> save(SeasonReward seasonReward) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = database.getConnection()) {
                String searchQuery = "SELECT * FROM rewards WHERE player_id = ? AND season_id = ?";
                PreparedStatement searchPs = connection.prepareStatement(searchQuery);
                searchPs.setString(1, seasonReward.getPlayerId().toString());
                searchPs.setInt(2, seasonReward.getSeasonId());
                ResultSet rs = searchPs.executeQuery();
                if(!rs.next()) { //insert
                    String insertQuery = "INSERT INTO rewards (season_id, player_id, contents) VALUES (?, ?, ?)";
                    PreparedStatement insertPs = connection.prepareStatement(insertQuery);
                    insertPs.setInt(1, seasonReward.getSeasonId());
                    insertPs.setString(2, seasonReward.getPlayerId().toString());
                    insertPs.setString(3, seasonReward.getContents());
                    return insertPs.executeUpdate();
                } else { //update
                    String updateQuery = "UPDATE rewards SET contents = ? WHERE player_id = ? AND season_id = ?";
                    PreparedStatement updatePs = connection.prepareStatement(updateQuery);
                    updatePs.setString(1, seasonReward.getContents());
                    updatePs.setString(2, seasonReward.getPlayerId().toString());
                    updatePs.setInt(3, seasonReward.getSeasonId());
                    return updatePs.executeUpdate();
                }
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to save seasonReward." + e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Integer> insert(SeasonReward seasonReward) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = database.getConnection()) {
                String query = "INSERT INTO rewards (season_id, player_id, contents) VALUES (?, ?, ?)";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setInt(1, seasonReward.getSeasonId());
                ps.setString(2, seasonReward.getPlayerId().toString());
                ps.setString(3, seasonReward.getContents());
                return ps.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to insert seasonReward." + e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Integer> update(SeasonReward seasonReward) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = database.getConnection()) {
                String query = "UPDATE rewards SET season_id = ?, player_id = ?, contents = ? WHERE id = ?";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setInt(1, seasonReward.getSeasonId());
                ps.setString(2, seasonReward.getPlayerId().toString());
                ps.setString(3, seasonReward.getContents());
                ps.setInt(4, seasonReward.getId());
                return ps.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to update seasonReward." + e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Integer> delete(SeasonReward seasonReward) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = database.getConnection()) {
                String query = "DELETE FROM rewards WHERE id = ?";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setInt(1, seasonReward.getId());
                return ps.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to delete seasonReward." + e.getMessage());
                return null;
            }
        });
    }
}
