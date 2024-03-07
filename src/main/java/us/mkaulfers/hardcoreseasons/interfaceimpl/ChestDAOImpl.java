package us.mkaulfers.hardcoreseasons.interfaceimpl;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import us.mkaulfers.hardcoreseasons.interfaces.ChestDAO;
import us.mkaulfers.hardcoreseasons.models.Database;
import us.mkaulfers.hardcoreseasons.models.TrackedChest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public CompletableFuture<Map<String, TrackedChest>> getAllForSeasonMap(int seasonId) {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, TrackedChest> chests = new HashMap<>();
            getAll().thenAccept(list -> list.forEach(chest ->
                    chests.put(chest.world + ":" + chest.x + ":" + chest.y + ":" + chest.z, chest)
            ));
            return chests;
        });
    }

    @Override
    public CompletableFuture<List<TrackedChest>> getAll(int seasonId) {
        return CompletableFuture.supplyAsync(() -> {

            List<TrackedChest> trackedChests = new ArrayList<>();
            try (Connection connection = database.getConnection()) {

                String query = "SELECT * FROM tracked_chests WHERE season_id = ?";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setInt(1, seasonId);
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
                String searchQuery = "SELECT * FROM tracked_chests WHERE x = ? AND y = ? AND z = ? AND world = ? AND type = ?";
                PreparedStatement searchPs = connection.prepareStatement(searchQuery);
                searchPs.setInt(1, trackedChest.x);
                searchPs.setInt(2, trackedChest.y);
                searchPs.setInt(3, trackedChest.z);
                searchPs.setString(4, trackedChest.world);
                searchPs.setString(5, trackedChest.type);
                ResultSet rs = searchPs.executeQuery();
                if (!rs.next()) { // insert
                    String insertQuery = "INSERT INTO tracked_chests (season_id, x, y, z, world, type, contents) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement insertPs = connection.prepareStatement(insertQuery);
                    insertPs.setInt(1, trackedChest.seasonId);
                    insertPs.setInt(2, trackedChest.x);
                    insertPs.setInt(3, trackedChest.y);
                    insertPs.setInt(4, trackedChest.z);
                    insertPs.setString(5, trackedChest.world);
                    insertPs.setString(6, trackedChest.type);
                    insertPs.setString(7, trackedChest.contents);
                    return insertPs.executeUpdate();
                } else { // update
                    String updateQuery = "UPDATE tracked_chests SET season_id = ?, x = ?, y = ?, z = ?, world = ?, type = ?, contents = ? WHERE x = ? AND y = ? AND z = ? AND world = ? AND type = ?";
                    PreparedStatement updatePs = connection.prepareStatement(updateQuery);
                    updatePs.setInt(1, trackedChest.seasonId);
                    updatePs.setInt(2, trackedChest.x);
                    updatePs.setInt(3, trackedChest.y);
                    updatePs.setInt(4, trackedChest.z);
                    updatePs.setString(5, trackedChest.world);
                    updatePs.setString(6, trackedChest.type);
                    updatePs.setString(7, trackedChest.contents);
                    updatePs.setInt(8, trackedChest.x);
                    updatePs.setInt(9, trackedChest.y);
                    updatePs.setInt(10, trackedChest.z);
                    updatePs.setString(11, trackedChest.world);
                    updatePs.setString(12, trackedChest.type);
                    return updatePs.executeUpdate();
                }
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
    public CompletableFuture<Void> updateBatch(List<TrackedChest> chestsToUpdate) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = database.getConnection()) {
                String query = "UPDATE tracked_chests SET season_id = ?, x = ?, y = ?, z = ?, world = ?, type = ?, contents = ? WHERE id = ?";
                PreparedStatement ps = connection.prepareStatement(query);

                int i = 0;
                for (TrackedChest trackedChest : chestsToUpdate) {
                    ps.setInt(1, trackedChest.seasonId);
                    ps.setInt(2, trackedChest.x);
                    ps.setInt(3, trackedChest.y);
                    ps.setInt(4, trackedChest.z);
                    ps.setString(5, trackedChest.world);
                    ps.setString(6, trackedChest.type);
                    ps.setString(7, trackedChest.contents);
                    ps.setInt(8, trackedChest.id);
                    ps.addBatch();
                    i++;

                    // execute every 1000 items or when it's the last item.
                    if (i % 1000 == 0 || i == chestsToUpdate.size()) {
                        ps.executeBatch(); // Execute every 1000 items.
                    }
                }
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to update chest." + e.getMessage());
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
