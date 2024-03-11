package us.mkaulfers.hardcoreseasons.interfaceimpl;

import org.bukkit.Bukkit;
import us.mkaulfers.hardcoreseasons.interfaces.VoteDAO;
import us.mkaulfers.hardcoreseasons.models.Database;
import us.mkaulfers.hardcoreseasons.models.Vote;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class VoteDAOImpl implements VoteDAO {
    Database database;

    public VoteDAOImpl(Database database) {
        this.database = database;
    }

    @Override
    public CompletableFuture<Vote> get(int id) {
        return null;
    }

    @Override
    public CompletableFuture<List<Vote>> getAll() {
        return null;
    }

    @Override
    public CompletableFuture<List<Vote>> getAllForSeason(int seasonId) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = database.getConnection()) {
                List<Vote> votes = new ArrayList<>();

                String query = "SELECT * FROM votes WHERE season_id = ?";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setInt(1, seasonId);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    int id = rs.getInt("id");
                    int seasonId1 = rs.getInt("season_id");
                    String playerId = rs.getString("player_id");
                    Timestamp lastNotification = rs.getTimestamp("date_last_voted");
                    boolean shouldEndSeason = rs.getBoolean("should_end_season");

                    votes.add(new Vote(id, seasonId1, UUID.fromString(playerId), lastNotification, shouldEndSeason));
                }

                return votes;
            } catch (SQLException e) {
                Bukkit.getLogger().severe("Failed to get all votes for season: \n" + e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Vote> getPlayerVote(UUID playerId, int seasonId) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = database.getConnection()) {
                String query = "SELECT * FROM votes WHERE player_id = ? AND season_id = ?";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setString(1, playerId.toString());
                ps.setInt(2, seasonId);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    int id = rs.getInt("id");
                    int seasonId1 = rs.getInt("season_id");
                    String playerId1 = rs.getString("player_id");
                    Timestamp lastNotification = rs.getTimestamp("date_last_voted");
                    boolean shouldEndSeason = rs.getBoolean("should_end_season");

                    return new Vote(id, seasonId1, UUID.fromString(playerId1), lastNotification, shouldEndSeason);
                }

                return null;
            } catch (SQLException e) {
                Bukkit.getLogger().severe("Failed to get player vote: \n" + e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Integer> save(Vote vote) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = database.getConnection()) {
                // Attempt to update the existing vote
                String updateQuery = "UPDATE votes SET date_last_voted = ?, should_end_season = ? WHERE season_id = ? AND player_id = ?";
                PreparedStatement updatePs = connection.prepareStatement(updateQuery);
                updatePs.setTimestamp(1, vote.dateLastVoted);
                updatePs.setBoolean(2, vote.shouldEndSeason);
                updatePs.setInt(3, vote.seasonId);
                updatePs.setString(4, vote.playerId.toString());
                int rowsAffected = updatePs.executeUpdate();

                // If no rows were affected by the update, insert a new vote
                if (rowsAffected == 0) {
                    String insertQuery = "INSERT INTO votes (season_id, player_id, date_last_voted, should_end_season) VALUES (?, ?, ?, ?)";
                    PreparedStatement insertPs = connection.prepareStatement(insertQuery);
                    insertPs.setInt(1, vote.seasonId);
                    insertPs.setString(2, vote.playerId.toString());
                    insertPs.setTimestamp(3, vote.dateLastVoted);
                    insertPs.setBoolean(4, vote.shouldEndSeason);
                    return insertPs.executeUpdate();
                } else {
                    return rowsAffected;
                }
            } catch (SQLException e) {
                Bukkit.getLogger().severe("Failed to save vote: \n" + e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Integer> insert(Vote vote) {
        return null;
    }

    @Override
    public CompletableFuture<Integer> update(Vote vote) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = database.getConnection()) {
                String query = "UPDATE votes SET date_last_voted = ?, should_end_season = ? WHERE id = ?";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setTimestamp(1, vote.dateLastVoted);
                ps.setBoolean(2, vote.shouldEndSeason);
                ps.setInt(3, vote.id);

                return ps.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe("Failed to update vote: \n" + e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Integer> delete(Vote vote) {
        return null;
    }
}
