package us.mkaulfers.hardcoreseasons.interfaceimpl;

import us.mkaulfers.hardcoreseasons.interfaces.EndChestDAO;
import us.mkaulfers.hardcoreseasons.models.Database;
import us.mkaulfers.hardcoreseasons.models.SurvivorEndChest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EndChestDAOImpl implements EndChestDAO {
    Database database;

    public EndChestDAOImpl(Database database) {
        this.database = database;
    }

    @Override
    public SurvivorEndChest get(int id) throws SQLException {
        try(Connection connection = database.getConnection()) {
            SurvivorEndChest endChestDAO = null;
            String query = "SELECT * FROM end_chests WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                endChestDAO = new SurvivorEndChest(
                        rs.getInt("id"),
                        UUID.fromString(rs.getString("player_id")),
                        rs.getInt("season_id"),
                        rs.getString("contents")
                );
            }
            return endChestDAO;
        }
    }

    @Override
    public List<SurvivorEndChest> getAll() throws SQLException {
        try(Connection connection = database.getConnection()) {
            List<SurvivorEndChest> endChests = new ArrayList<>();

            String query = "SELECT * FROM end_chests";
            PreparedStatement ps = connection.prepareStatement(query);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                SurvivorEndChest endChestDAO = new SurvivorEndChest(
                        rs.getInt("id"),
                        UUID.fromString(rs.getString("player_id")),
                        rs.getInt("season_id"),
                        rs.getString("contents")
                );
                endChests.add(endChestDAO);
            }

            return endChests;
        }
    }

    @Override
    public int save(SurvivorEndChest endChestDAO) throws SQLException {
        try(Connection connection = database.getConnection()) {
            String query = "INSERT INTO end_chests (id, player_id, season_id, contents) VALUES (?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE player_id = ?, season_id = ?, contents = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, endChestDAO.id);
            ps.setString(2, endChestDAO.playerId.toString());
            ps.setInt(3, endChestDAO.seasonId);
            ps.setString(4, endChestDAO.contents);
            ps.setString(5, endChestDAO.playerId.toString());
            ps.setInt(6, endChestDAO.seasonId);
            ps.setString(7, endChestDAO.contents);

            return ps.executeUpdate();
        }
    }

    @Override
    public int insert(SurvivorEndChest endChestDAO) throws SQLException {
        try(Connection connection = database.getConnection()) {
            String query = "INSERT INTO end_chests (id, player_id, season_id, contents) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, endChestDAO.id);
            ps.setString(2, endChestDAO.playerId.toString());
            ps.setInt(3, endChestDAO.seasonId);
            ps.setString(4, endChestDAO.contents);

            return ps.executeUpdate();
        }
    }

    @Override
    public int update(SurvivorEndChest endChestDAO) throws SQLException {
        try(Connection connection = database.getConnection()) {
            String query = "UPDATE end_chests SET contents = ? WHERE id = ? AND player_id = ? AND season_id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, endChestDAO.contents);
            ps.setInt(2, endChestDAO.id);
            ps.setString(3, endChestDAO.playerId.toString());
            ps.setInt(4, endChestDAO.seasonId);

            return ps.executeUpdate();
        }
    }

    @Override
    public int delete(SurvivorEndChest endChestDAO) throws SQLException {
        try(Connection connection = database.getConnection()) {
            String query = "DELETE FROM end_chests WHERE id = ? AND player_id = ? AND season_id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, endChestDAO.id);
            ps.setString(2, endChestDAO.playerId.toString());
            ps.setInt(3, endChestDAO.seasonId);

            return ps.executeUpdate();
        }
    }
}
