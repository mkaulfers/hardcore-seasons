package us.mkaulfers.hardcoreseasons.interfaces;

import us.mkaulfers.hardcoreseasons.models.Player;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PlayerDAO extends DAO<Player> {
    CompletableFuture<Player> get(UUID playerId, int seasonId);
}
