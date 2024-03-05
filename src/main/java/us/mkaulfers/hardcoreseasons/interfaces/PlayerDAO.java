package us.mkaulfers.hardcoreseasons.interfaces;

import us.mkaulfers.hardcoreseasons.models.Player;

import java.sql.SQLException;
import java.util.UUID;

public interface PlayerDAO extends DAO<Player> {
    Player get(UUID playerId, int seasonId) throws SQLException;
}
