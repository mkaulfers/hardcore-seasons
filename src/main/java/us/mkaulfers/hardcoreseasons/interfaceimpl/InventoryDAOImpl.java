package us.mkaulfers.hardcoreseasons.interfaceimpl;

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

public class InventoryDAOImpl implements InventoryDAO {
    Database database;

    public InventoryDAOImpl(Database database) {
        this.database = database;
    }

    @Override
    public SurvivorInventory get(int id) throws SQLException {
        SurvivorInventory survivorInventory = null;
        Connection connection = null;

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
    }

    @Override
    public List<SurvivorInventory> getAll() throws SQLException {
        List<SurvivorInventory> survivorInventories = new ArrayList<>();
        Connection connection = database.getConnection();

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
    }

    @Override
    public int save(SurvivorInventory survivorInventory) throws SQLException {
        String searchQuery = "SELECT * FROM inventories WHERE player_id = ? AND season_id = ?";
        try(Connection connection = database.getConnection()) {
            PreparedStatement searchPs = connection.prepareStatement(searchQuery);
            searchPs.setString(1, survivorInventory.playerId.toString());
            searchPs.setInt(2, survivorInventory.seasonId);
            ResultSet rs = searchPs.executeQuery();
            if(!rs.next()) { // insert
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
        }
    }

    @Override
    public int insert(SurvivorInventory survivorInventory) throws SQLException {
        Connection connection = database.getConnection();
        String query = "INSERT INTO inventories (id, player_id, season_id, contents) VALUES (?, ?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, survivorInventory.id);
        ps.setString(2, survivorInventory.playerId.toString());
        ps.setInt(3, survivorInventory.seasonId);
        ps.setString(4, survivorInventory.contents);

        return ps.executeUpdate();
    }

    @Override
    public int update(SurvivorInventory survivorInventory) throws SQLException {
        Connection connection = database.getConnection();
        String query = "UPDATE inventories SET player_id = ?, season_id = ?, contents = ? WHERE id = ?";

        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, survivorInventory.playerId.toString());
        ps.setInt(2, survivorInventory.seasonId);
        ps.setString(3, survivorInventory.contents);
        ps.setInt(4, survivorInventory.id);

        return ps.executeUpdate();
    }

    @Override
    public int delete(SurvivorInventory survivorInventory) throws SQLException {
        Connection connection = database.getConnection();
        String query = "DELETE FROM inventories WHERE id = ?";

        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, survivorInventory.id);

        return ps.executeUpdate();
    }
}
