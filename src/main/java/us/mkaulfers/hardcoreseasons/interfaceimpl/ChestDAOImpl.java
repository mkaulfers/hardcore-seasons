package us.mkaulfers.hardcoreseasons.interfaceimpl;

import org.bukkit.Bukkit;
import us.mkaulfers.hardcoreseasons.interfaces.ChestDAO;
import us.mkaulfers.hardcoreseasons.models.Database;
import us.mkaulfers.hardcoreseasons.models.TrackedChest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ChestDAOImpl implements ChestDAO {
    Database database;

    public ChestDAOImpl(Database database) {
        this.database = database;
    }

    @Override
    public CompletableFuture<TrackedChest> get(int x, int y, int z, String world, String type) {
        return CompletableFuture.supplyAsync(() -> {
            TrackedChest trackedChest = null;

            try (Connection connection = database.getConnection()) {
                String query = "SELECT * FROM tracked_chests WHERE x = ? AND y = ? AND z = ? AND world = ? AND type = ?";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setInt(1, x);
                ps.setInt(2, y);
                ps.setInt(3, z);
                ps.setString(4, world);
                ps.setString(5, type);

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    trackedChest = new TrackedChest(
                            rs.getInt("id"),
                            rs.getInt("season_id"),
                            rs.getInt("x"),
                            rs.getInt("y"),
                            rs.getInt("z"),
                            rs.getString("world"),
                            rs.getString("type"),
                            rs.getString("contents")
                    );
                }
                return trackedChest;
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to get chest." + e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<TrackedChest> get(int id) {
        return CompletableFuture.supplyAsync(() -> {
            TrackedChest trackedChest = null;

            try (Connection connection = database.getConnection()) {
                String query = "SELECT * FROM tracked_chests WHERE id = ?";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setInt(1, id);

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    trackedChest = new TrackedChest(
                            rs.getInt("id"),
                            rs.getInt("season_id"),
                            rs.getInt("x"),
                            rs.getInt("y"),
                            rs.getInt("z"),
                            rs.getString("world"),
                            rs.getString("type"),
                            rs.getString("contents")
                    );
                }
                return trackedChest;
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to get chest." + e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<List<TrackedChest>> getAll() {
        return CompletableFuture.supplyAsync(() -> {

            List<TrackedChest> trackedChests = new ArrayList<>();
            try (Connection connection = database.getConnection()) {

                String query = "SELECT * FROM tracked_chests";
                PreparedStatement ps = connection.prepareStatement(query);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    TrackedChest trackedChest = new TrackedChest(
                            rs.getInt("id"),
                            rs.getInt("season_id"),
                            rs.getInt("x"),
                            rs.getInt("y"),
                            rs.getInt("z"),
                            rs.getString("world"),
                            rs.getString("type"),
                            rs.getString("contents")
                    );
                    trackedChests.add(trackedChest);
                }
                return trackedChests;
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to get all chests." + e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Integer> save(TrackedChest trackedChest) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = database.getConnection()) {
                String query = "INSERT INTO tracked_chests (season_id, x, y, z, world, type, contents) VALUES (?, ?, ?, ?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE season_id = ?, x = ?, y = ?, z = ?, world = ?, type = ?, contents = ?";

                PreparedStatement ps = connection.prepareStatement(query);
                ps.setInt(1, trackedChest.seasonId);
                ps.setInt(2, trackedChest.x);
                ps.setInt(3, trackedChest.y);
                ps.setInt(4, trackedChest.z);
                ps.setString(5, trackedChest.world);
                ps.setString(6, trackedChest.type);
                ps.setString(7, trackedChest.contents);
                ps.setInt(8, trackedChest.seasonId);
                ps.setInt(9, trackedChest.x);
                ps.setInt(10, trackedChest.y);
                ps.setInt(11, trackedChest.z);
                ps.setString(12, trackedChest.world);
                ps.setString(13, trackedChest.type);
                ps.setString(14, trackedChest.contents);
                return ps.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to save chest." + e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Integer> insert(TrackedChest trackedChest) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = database.getConnection()) {
                String query = "INSERT INTO tracked_chests (season_id, x, y, z, world, type, contents) VALUES (?, ?, ?, ?, ?, ?, ?)";

                PreparedStatement ps = connection.prepareStatement(query);
                ps.setInt(1, trackedChest.seasonId);
                ps.setInt(2, trackedChest.x);
                ps.setInt(3, trackedChest.y);
                ps.setInt(4, trackedChest.z);
                ps.setString(5, trackedChest.world);
                ps.setString(6, trackedChest.type);
                ps.setString(7, trackedChest.contents);

                return ps.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to insert chest." + e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Integer> update(TrackedChest trackedChest) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = database.getConnection()) {
                String query = "UPDATE tracked_chests SET season_id = ?, x = ?, y = ?, z = ?, world = ?, type = ?, contents = ? WHERE id = ?";

                PreparedStatement ps = connection.prepareStatement(query);
                ps.setInt(1, trackedChest.seasonId);
                ps.setInt(2, trackedChest.x);
                ps.setInt(3, trackedChest.y);
                ps.setInt(4, trackedChest.z);
                ps.setString(5, trackedChest.world);
                ps.setString(6, trackedChest.type);
                ps.setString(7, trackedChest.contents);
                ps.setInt(8, trackedChest.id);

                return ps.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to update chest." + e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Integer> delete(TrackedChest trackedChest) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = database.getConnection()) {
                String query = "DELETE FROM tracked_chests WHERE x = ? AND y = ? AND z = ? AND world = ? AND type = ?";

                PreparedStatement ps = connection.prepareStatement(query);
                ps.setInt(1, trackedChest.x);
                ps.setInt(2, trackedChest.y);
                ps.setInt(3, trackedChest.z);
                ps.setString(4, trackedChest.world);
                ps.setString(5, trackedChest.type);

                return ps.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to delete chest." + e.getMessage());
                return null;
            }
        });
    }
}
