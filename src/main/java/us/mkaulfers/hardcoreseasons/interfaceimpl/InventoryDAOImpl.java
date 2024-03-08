package us.mkaulfers.hardcoreseasons.interfaceimpl;

import org.bukkit.Bukkit;
import us.mkaulfers.hardcoreseasons.interfaces.InventoryDAO;
import us.mkaulfers.hardcoreseasons.models.Database;
import us.mkaulfers.hardcoreseasons.models.SurvivorInventory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class InventoryDAOImpl implements InventoryDAO {
    Database database;

    public InventoryDAOImpl(Database database) {
        this.database = database;
    }

    @Override
    public CompletableFuture<SurvivorInventory> get(int id) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = database.getConnection()) {
                SurvivorInventory survivorInventory = null;

                String query = "SELECT * FROM inventories WHERE id = ?";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setInt(1, id);

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    survivorInventory = new SurvivorInventory(
                            rs.getInt("id"),
                            UUID.fromString(rs.getString("player_id")),
                            rs.getInt("season_id"),
                            rs.getString("contents")
                    );
                }

                return survivorInventory;
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to get inventory." + e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<List<SurvivorInventory>> getAll(int seasonId) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = database.getConnection()) {
                List<SurvivorInventory> survivorInventories = new ArrayList<>();

                String query = "SELECT * FROM inventories WHERE season_id = ?";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setInt(1, seasonId);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    SurvivorInventory survivorInventory = new SurvivorInventory(
                            rs.getInt("id"),
                            UUID.fromString(rs.getString("player_id")),
                            rs.getInt("season_id"),
                            rs.getString("contents")
                    );
                    survivorInventories.add(survivorInventory);
                }

                return survivorInventories;
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to get all inventories." + e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<List<SurvivorInventory>> getAll() {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = database.getConnection()) {
                List<SurvivorInventory> survivorInventories = new ArrayList<>();

                String query = "SELECT * FROM inventories";
                PreparedStatement ps = connection.prepareStatement(query);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    SurvivorInventory survivorInventory = new SurvivorInventory(
                            rs.getInt("id"),
                            UUID.fromString(rs.getString("player_id")),
                            rs.getInt("season_id"),
                            rs.getString("contents")
                    );
                    survivorInventories.add(survivorInventory);
                }

                return survivorInventories;
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to get all inventories." + e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Integer> save(SurvivorInventory survivorInventory) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = database.getConnection()) {
                String searchQuery = "SELECT * FROM inventories WHERE player_id = ? AND season_id = ?";
                PreparedStatement searchPs = connection.prepareStatement(searchQuery);
                searchPs.setString(1, survivorInventory.playerId.toString());
                searchPs.setInt(2, survivorInventory.seasonId);
                ResultSet rs = searchPs.executeQuery();
                if (!rs.next()) { // insert
                    String insertQuery = "INSERT INTO inventories (player_id, season_id, contents) VALUES (?, ?, ?)";
                    PreparedStatement insertPs = connection.prepareStatement(insertQuery);
                    insertPs.setString(1, survivorInventory.playerId.toString());
                    insertPs.setInt(2, survivorInventory.seasonId);
                    insertPs.setString(3, survivorInventory.contents);
                    return insertPs.executeUpdate();
                } else { // update
                    String updateQuery = "UPDATE inventories SET contents = ? WHERE player_id = ? AND season_id = ?";
                    PreparedStatement updatePs = connection.prepareStatement(updateQuery);
                    updatePs.setString(1, survivorInventory.contents);
                    updatePs.setString(2, survivorInventory.playerId.toString());
                    updatePs.setInt(3, survivorInventory.seasonId);
                    return updatePs.executeUpdate();
                }
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to save/update inventory." + e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Integer> insert(SurvivorInventory survivorInventory) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = database.getConnection()) {
                String query = "INSERT INTO inventories (id, player_id, season_id, contents) VALUES (?, ?, ?, ?)";

                PreparedStatement ps = connection.prepareStatement(query);
                ps.setInt(1, survivorInventory.id);
                ps.setString(2, survivorInventory.playerId.toString());
                ps.setInt(3, survivorInventory.seasonId);
                ps.setString(4, survivorInventory.contents);

                return ps.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to insert inventory." + e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Integer> update(SurvivorInventory survivorInventory) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = database.getConnection()) {
                String query = "UPDATE inventories SET player_id = ?, season_id = ?, contents = ? WHERE id = ?";

                PreparedStatement ps = connection.prepareStatement(query);
                ps.setString(1, survivorInventory.playerId.toString());
                ps.setInt(2, survivorInventory.seasonId);
                ps.setString(3, survivorInventory.contents);
                ps.setInt(4, survivorInventory.id);

                return ps.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to update inventory." + e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Integer> delete(SurvivorInventory survivorInventory) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = database.getConnection()) {
                String query = "DELETE FROM inventories WHERE id = ?";

                PreparedStatement ps = connection.prepareStatement(query);
                ps.setInt(1, survivorInventory.id);

                return ps.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to delete inventory." + e.getMessage());
                return null;
            }
        });
    }
}
