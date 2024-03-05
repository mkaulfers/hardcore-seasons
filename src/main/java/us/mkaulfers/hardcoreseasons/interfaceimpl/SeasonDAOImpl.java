package us.mkaulfers.hardcoreseasons.interfaceimpl;

import us.mkaulfers.hardcoreseasons.interfaces.SeasonDAO;
import us.mkaulfers.hardcoreseasons.models.Database;
import us.mkaulfers.hardcoreseasons.models.Season;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SeasonDAOImpl implements SeasonDAO {
    Database database;

    public SeasonDAOImpl(Database database) {
        this.database = database;
    }

    @Override
    public Season get(int id) throws SQLException {
        Season season = null;
        Connection connection = database.getConnection();

        String query = "SELECT * FROM seasons WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, id);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            season = new Season(
                    rs.getInt("id"),
                    rs.getInt("season_id"),
                    rs.getDate("start_date"),
                    rs.getDate("end_date")
            );
        }

        return season;
    }

    @Override
    public List<Season> getAll() throws SQLException {
        List<Season> seasons = new ArrayList<>();
        Connection connection = database.getConnection();

        String query = "SELECT * FROM seasons";
        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Season season = new Season(
                    rs.getInt("id"),
                    rs.getInt("season_id"),
                    rs.getDate("start_date"),
                    rs.getDate("end_date")
            );
            seasons.add(season);
        }

        return seasons;
    }

    @Override
    public int save(Season season) throws SQLException {
        Connection connection = database.getConnection();
        String query = "INSERT INTO seasons (id, start_date, end_date) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE start_date = ?, end_date = ?";

        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, season.seasonId);
        ps.setDate(2, (Date) season.startDate);
        ps.setDate(3, (Date) season.endDate);
        ps.setDate(4, (Date) season.startDate);
        ps.setDate(5, (Date) season.endDate);

        return ps.executeUpdate();
    }

    @Override
    public int insert(Season season) throws SQLException {
        Connection connection = database.getConnection();
        String query = "INSERT INTO seasons (id, start_date, end_date) VALUES (?, ?)";

        PreparedStatement ps = connection.prepareStatement(query);
        ps.setDate(1, (Date) season.startDate);
        ps.setDate(2, (Date) season.endDate);

        return ps.executeUpdate();
    }

    @Override
    public int update(Season season) throws SQLException {
        Connection connection = database.getConnection();
        String query = "UPDATE seasons SET start_date = ?, end_date = ? WHERE id = ?";

        PreparedStatement ps = connection.prepareStatement(query);
        ps.setDate(1, (Date) season.startDate);
        ps.setDate(2, (Date) season.endDate);
        ps.setInt(3, season.seasonId);

        return ps.executeUpdate();
    }

    @Override
    public int delete(Season season) throws SQLException {
        Connection connection = database.getConnection();
        String query = "DELETE FROM seasons WHERE id = ?";

        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, season.seasonId);

        return ps.executeUpdate();
    }
}
