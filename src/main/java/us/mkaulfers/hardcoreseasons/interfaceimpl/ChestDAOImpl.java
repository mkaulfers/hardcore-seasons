package us.mkaulfers.hardcoreseasons.interfaceimpl;

import us.mkaulfers.hardcoreseasons.interfaces.ChestDAO;
import us.mkaulfers.hardcoreseasons.models.Database;
import us.mkaulfers.hardcoreseasons.models.TrackedChest;

import javax.sound.midi.Track;
import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChestDAOImpl implements ChestDAO {
    Database database;

    public ChestDAOImpl(Database database) {
        this.database = database;
    }

    @Override
    public TrackedChest get(int x, int y, int z, String world, String type) throws SQLException {
        TrackedChest trackedChest = null;

        try(Connection connection = database.getConnection()) {
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
        }

        return trackedChest;
    }

    @Override
    public TrackedChest get(int id) throws SQLException {
        TrackedChest trackedChest = null;

        try(Connection connection = database.getConnection()) {
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
        }

        return trackedChest;
    }

    @Override
    public List<TrackedChest> getAll() throws SQLException {
        List<TrackedChest> trackedChests = new ArrayList<>();
        try(Connection connection = database.getConnection()) {

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
        }

        return trackedChests;
    }

    @Override
    public int save(TrackedChest trackedChest) throws SQLException {
        try(Connection connection = database.getConnection()) {
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
        }
    }

    @Override
    public int insert(TrackedChest trackedChest) throws SQLException {
        try(Connection connection = database.getConnection()) {
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
        }
    }

    @Override
    public int update(TrackedChest trackedChest) throws SQLException {
        try(Connection connection = database.getConnection()) {
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
        }
    }

    @Override
    public int delete(TrackedChest trackedChest) throws SQLException {
        try(Connection connection = database.getConnection()) {
            String query = "DELETE FROM tracked_chests WHERE x = ? AND y = ? AND z = ? AND world = ? AND type = ?";

            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, trackedChest.x);
            ps.setInt(2, trackedChest.y);
            ps.setInt(3, trackedChest.z);
            ps.setString(4, trackedChest.world);
            ps.setString(5, trackedChest.type);

            return ps.executeUpdate();
        }
    }
}
