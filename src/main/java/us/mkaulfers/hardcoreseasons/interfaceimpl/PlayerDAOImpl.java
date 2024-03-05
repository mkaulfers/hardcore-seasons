package us.mkaulfers.hardcoreseasons.interfaceimpl;

import org.bukkit.Bukkit;
import us.mkaulfers.hardcoreseasons.interfaces.PlayerDAO;
import us.mkaulfers.hardcoreseasons.models.Database;
import us.mkaulfers.hardcoreseasons.models.Player;

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
    public CompletableFuture<Player> get(UUID playerId, int seasonId) {
        return CompletableFuture.supplyAsync(() -> {
            try(Connection connection = database.getConnection()) {
                Player player = null;

                String query = "SELECT * FROM players WHERE player_id = ? AND season_id = ?";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setString(1, playerId.toString());
                ps.setInt(2, seasonId);

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    player = new Player(
                            rs.getInt("id"),
                            UUID.fromString(rs.getString("player_id")),
                            rs.getInt("season_id"),
                            rs.getTimestamp("join_date"),
                            rs.getTimestamp("last_online"),
                            rs.getBoolean("is_dead")
                    );
                }

                return player;
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to get player." +e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Player> get(int id) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = database.getConnection()) {
                Player player = null;

                String query = "SELECT * FROM players WHERE id = ?";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setInt(1, id);

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    player = new Player(
                            rs.getInt("id"),
                            UUID.fromString(rs.getString("player_id")),
                            rs.getInt("season_id"),
                            rs.getTimestamp("join_date"),
                            rs.getTimestamp("last_online"),
                            rs.getBoolean("is_dead")
                    );
                }

                return player;
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to get player." +e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<List<Player>> getAll() {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = database.getConnection()) {
                List<Player> players = new ArrayList<>();

                String query = "SELECT * FROM players";
                PreparedStatement ps = connection.prepareStatement(query);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    Player player = new Player(
                            rs.getInt("id"),
                            UUID.fromString(rs.getString("player_id")),
                            rs.getInt("season_id"),
                            rs.getTimestamp("join_date"),
                            rs.getTimestamp("last_online"),
                            rs.getBoolean("is_dead")
                    );
                    players.add(player);
                }

                return players;
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to get all players." +e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Integer> save(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            try(Connection connection = database.getConnection()) {
                String query = "INSERT INTO players (player_id, season_id, join_date, last_online, is_dead) VALUES (?, ?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE season_id = ?, join_date = ?, last_online = ?, is_dead = ?";

                PreparedStatement ps = connection.prepareStatement(query);
                ps.setString(1, player.playerId.toString());
                ps.setInt(2, player.seasonId);
                ps.setTimestamp(3, player.joinDate);
                ps.setTimestamp(4, player.lastOnline);
                ps.setBoolean(5, player.isDead);
                ps.setInt(6, player.seasonId);
                ps.setTimestamp(7, player.joinDate);
                ps.setTimestamp(8, player.lastOnline);
                ps.setBoolean(9, player.isDead);

                return ps.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to save player." +e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Integer> insert(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            try(Connection connection = database.getConnection()) {
                String query = "INSERT INTO players (player_id, season_id, join_date, last_online, is_dead) VALUES (?, ?, ?, ?, ?)";

                PreparedStatement ps = connection.prepareStatement(query);
                ps.setString(1, player.playerId.toString());
                ps.setInt(2, player.seasonId);
                ps.setTimestamp(3, player.joinDate);
                ps.setTimestamp(4, player.lastOnline);
                ps.setBoolean(5, player.isDead);

                return ps.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to insert player." +e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Integer> update(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            try(Connection connection = database.getConnection()) {
                String query = "UPDATE players SET season_id = ?, join_date = ?, last_online = ?, is_dead = ? WHERE id = ?";

                PreparedStatement ps = connection.prepareStatement(query);
                ps.setInt(1, player.seasonId);
                ps.setTimestamp(2, player.joinDate);
                ps.setTimestamp(3, player.lastOnline);
                ps.setBoolean(4, player.isDead);
                ps.setInt(5, player.id);

                return ps.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to update player." +e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Integer> delete(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            try(Connection connection = database.getConnection()) {
                String query = "DELETE FROM players WHERE id = ?";

                PreparedStatement ps = connection.prepareStatement(query);
                ps.setString(1, player.playerId.toString());

                return ps.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to delete player." +e.getMessage());
                return null;
            }
        });
    }
}
