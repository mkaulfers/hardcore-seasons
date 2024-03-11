package us.mkaulfers.hardcoreseasons.interfaceimpl;

import org.bukkit.Bukkit;
import us.mkaulfers.hardcoreseasons.interfaces.PlayerDAO;
import us.mkaulfers.hardcoreseasons.models.Database;
import us.mkaulfers.hardcoreseasons.models.Participant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerDAOImpl implements PlayerDAO {
    Database database;

    public PlayerDAOImpl(Database database) {
        this.database = database;
    }

    @Override
    public CompletableFuture<Participant> get(UUID playerId, int seasonId) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = database.getConnection()) {
                Participant participant = null;

                String query = "SELECT * FROM participants WHERE player_id = ? AND season_id = ?";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setString(1, playerId.toString());
                ps.setInt(2, seasonId);

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    participant = new Participant(
                            rs.getInt("id"),
                            UUID.fromString(rs.getString("player_id")),
                            rs.getInt("season_id"),
                            rs.getTimestamp("join_date"),
                            rs.getTimestamp("last_online"),
                            rs.getBoolean("is_dead")
                    );
                }

                return participant;
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to get player." + e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Participant> get(int id) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = database.getConnection()) {
                Participant participant = null;

                String query = "SELECT * FROM participants WHERE id = ?";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setInt(1, id);

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    participant = new Participant(
                            rs.getInt("id"),
                            UUID.fromString(rs.getString("player_id")),
                            rs.getInt("season_id"),
                            rs.getTimestamp("join_date"),
                            rs.getTimestamp("last_online"),
                            rs.getBoolean("is_dead")
                    );
                }

                return participant;
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to get player. \n" + e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<List<Participant>> getAllForSeason(int seasonId) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = database.getConnection()) {
                List<Participant> participants = new ArrayList<>();

                String query = "SELECT * FROM participants WHERE season_id = ?";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setInt(1, seasonId);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    Participant participant = new Participant(
                            rs.getInt("id"),
                            UUID.fromString(rs.getString("player_id")),
                            rs.getInt("season_id"),
                            rs.getTimestamp("join_date"),
                            rs.getTimestamp("last_online"),
                            rs.getBoolean("is_dead")
                    );
                    participants.add(participant);
                }

                return participants;
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to get all players for season. \n" + e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<List<Participant>> getAllAliveForSeason(int seasonId) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = database.getConnection()) {
                List<Participant> participants = new ArrayList<>();

                String query = "SELECT * FROM participants WHERE season_id = ? AND is_dead = 0";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setInt(1, seasonId);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    Participant participant = new Participant(
                            rs.getInt("id"),
                            UUID.fromString(rs.getString("player_id")),
                            rs.getInt("season_id"),
                            rs.getTimestamp("join_date"),
                            rs.getTimestamp("last_online"),
                            rs.getBoolean("is_dead")
                    );
                    participants.add(participant);
                }

                return participants;
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to get all alive players for season." + e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<List<Participant>> getAll() {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = database.getConnection()) {
                List<Participant> participants = new ArrayList<>();

                String query = "SELECT * FROM participants WHERE season_id = ?";
                PreparedStatement ps = connection.prepareStatement(query);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    Participant participant = new Participant(
                            rs.getInt("id"),
                            UUID.fromString(rs.getString("player_id")),
                            rs.getInt("season_id"),
                            rs.getTimestamp("join_date"),
                            rs.getTimestamp("last_online"),
                            rs.getBoolean("is_dead")
                    );
                    participants.add(participant);
                }

                return participants;
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to get all players." + e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Integer> save(Participant participant) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = database.getConnection()) {
                // First, attempt to update
                String updateQuery = "UPDATE participants SET season_id = ?, join_date = ?, last_online = ?, is_dead = ? WHERE player_id = ?";
                PreparedStatement updatePs = connection.prepareStatement(updateQuery);
                updatePs.setInt(1, participant.seasonId);
                updatePs.setTimestamp(2, participant.joinDate);
                updatePs.setTimestamp(3, participant.lastOnline);
                updatePs.setBoolean(4, participant.isDead);
                updatePs.setString(5, participant.playerId.toString());

                int rowsAffected = updatePs.executeUpdate();

                // If update does not affect any rows, perform insert
                if (rowsAffected == 0) {
                    String insertQuery = "INSERT INTO participants (player_id, season_id, join_date, last_online, is_dead) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement insertPs = connection.prepareStatement(insertQuery);
                    insertPs.setString(1, participant.playerId.toString());
                    insertPs.setInt(2, participant.seasonId);
                    insertPs.setTimestamp(3, participant.joinDate);
                    insertPs.setTimestamp(4, participant.lastOnline);
                    insertPs.setBoolean(5, participant.isDead);
                    return insertPs.executeUpdate();
                } else {
                    return rowsAffected;
                }
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to save participant." + e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Integer> insert(Participant participant) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = database.getConnection()) {
                String query = "INSERT INTO participants (player_id, season_id, join_date, last_online, is_dead) VALUES (?, ?, ?, ?, ?)";

                PreparedStatement ps = connection.prepareStatement(query);
                ps.setString(1, participant.playerId.toString());
                ps.setInt(2, participant.seasonId);
                ps.setTimestamp(3, participant.joinDate);
                ps.setTimestamp(4, participant.lastOnline);
                ps.setBoolean(5, participant.isDead);

                return ps.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to insert participant." + e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Integer> update(Participant participant) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = database.getConnection()) {
                String query = "UPDATE participants SET season_id = ?, join_date = ?, last_online = ?, is_dead = ? WHERE id = ?";

                PreparedStatement ps = connection.prepareStatement(query);
                ps.setInt(1, participant.seasonId);
                ps.setTimestamp(2, participant.joinDate);
                ps.setTimestamp(3, participant.lastOnline);
                ps.setBoolean(4, participant.isDead);
                ps.setInt(5, participant.id);

                return ps.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to update participant." + e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Integer> delete(Participant participant) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = database.getConnection()) {
                String query = "DELETE FROM players WHERE id = ?";

                PreparedStatement ps = connection.prepareStatement(query);
                ps.setString(1, participant.playerId.toString());

                return ps.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to delete participant." + e.getMessage());
                return null;
            }
        });
    }
}
