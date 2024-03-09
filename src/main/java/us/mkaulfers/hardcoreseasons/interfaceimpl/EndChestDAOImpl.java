package us.mkaulfers.hardcoreseasons.interfaceimpl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import us.mkaulfers.hardcoreseasons.interfaces.EndChestDAO;
import us.mkaulfers.hardcoreseasons.models.Database;
import us.mkaulfers.hardcoreseasons.models.TrackedEndChest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class EndChestDAOImpl implements EndChestDAO {
    Database database;

    public EndChestDAOImpl(Database database) {
        this.database = database;
    }

    @Override
    public CompletableFuture<TrackedEndChest> get(int id) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = database.getConnection()) {
                TrackedEndChest endChestDAO = null;
                String query = "SELECT * FROM end_chests WHERE id = ?";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setInt(1, id);

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    endChestDAO = new TrackedEndChest(
                            rs.getInt("id"),
                            UUID.fromString(rs.getString("player_id")),
                            rs.getInt("season_id"),
                            rs.getString("contents")
                    );
                }
                return endChestDAO;
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to get endChest." + e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<List<TrackedEndChest>> getAll(int seasonId) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = database.getConnection()) {
                List<TrackedEndChest> endChests = new ArrayList<>();

                String query = "SELECT * FROM end_chests WHERE season_id = ?";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setInt(1, seasonId);

                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    TrackedEndChest endChestDAO = new TrackedEndChest(
                            rs.getInt("id"),
                            UUID.fromString(rs.getString("player_id")),
                            rs.getInt("season_id"),
                            rs.getString("contents")
                    );
                    endChests.add(endChestDAO);
                }

                return endChests;
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to get all endChests." + e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<List<TrackedEndChest>> getAll() {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = database.getConnection()) {
                List<TrackedEndChest> endChests = new ArrayList<>();

                String query = "SELECT * FROM end_chests";
                PreparedStatement ps = connection.prepareStatement(query);

                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    TrackedEndChest endChestDAO = new TrackedEndChest(
                            rs.getInt("id"),
                            UUID.fromString(rs.getString("player_id")),
                            rs.getInt("season_id"),
                            rs.getString("contents")
                    );
                    endChests.add(endChestDAO);
                }

                return endChests;
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to get all endChests." + e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Integer> save(TrackedEndChest endChestDAO) {
        return CompletableFuture.supplyAsync(() -> {
            String searchQuery = "SELECT * FROM end_chests WHERE player_id = ? AND season_id = ?";
            try (Connection connection = database.getConnection()) {
                PreparedStatement searchPs = connection.prepareStatement(searchQuery);
                searchPs.setString(1, endChestDAO.playerId.toString());
                searchPs.setInt(2, endChestDAO.seasonId);
                ResultSet rs = searchPs.executeQuery();
                if (!rs.next()) { // insert
                    String insertQuery = "INSERT INTO end_chests (player_id, season_id, contents) VALUES (?, ?, ?)";
                    PreparedStatement insertPs = connection.prepareStatement(insertQuery);
                    insertPs.setString(1, endChestDAO.playerId.toString());
                    insertPs.setInt(2, endChestDAO.seasonId);
                    insertPs.setString(3, endChestDAO.contents);
                    return insertPs.executeUpdate();
                } else { // update
                    String updateQuery = "UPDATE end_chests SET contents = ? WHERE player_id = ? AND season_id = ?";
                    PreparedStatement updatePs = connection.prepareStatement(updateQuery);
                    updatePs.setString(1, endChestDAO.contents);
                    updatePs.setString(2, endChestDAO.playerId.toString());
                    updatePs.setInt(3, endChestDAO.seasonId);
                    return updatePs.executeUpdate();
                }
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to save endChest." + e.getMessage());
                return 0;
            }
        });
    }

    @Override
    public CompletableFuture<Integer> insert(TrackedEndChest endChestDAO) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = database.getConnection()) {
                String query = "INSERT INTO end_chests (id, player_id, season_id, contents) VALUES (?, ?, ?, ?)";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setInt(1, endChestDAO.id);
                ps.setString(2, endChestDAO.playerId.toString());
                ps.setInt(3, endChestDAO.seasonId);
                ps.setString(4, endChestDAO.contents);

                return ps.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to insert endChest." + e.getMessage());
                return 0;
            }
        });
    }

    @Override
    public CompletableFuture<Integer> update(TrackedEndChest endChestDAO) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = database.getConnection()) {
                String query = "UPDATE end_chests SET contents = ? WHERE id = ? AND player_id = ? AND season_id = ?";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setString(1, endChestDAO.contents);
                ps.setInt(2, endChestDAO.id);
                ps.setString(3, endChestDAO.playerId.toString());
                ps.setInt(4, endChestDAO.seasonId);

                return ps.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to update endChest." + e.getMessage());
                return 0;
            }
        });
    }

    @Override
    public CompletableFuture<Integer> delete(TrackedEndChest endChestDAO) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = database.getConnection()) {
                String query = "DELETE FROM end_chests WHERE id = ? AND player_id = ? AND season_id = ?";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setInt(1, endChestDAO.id);
                ps.setString(2, endChestDAO.playerId.toString());
                ps.setInt(3, endChestDAO.seasonId);

                return ps.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to delete endChest." + e.getMessage());
                return 0;
            }
        });
    }
}
