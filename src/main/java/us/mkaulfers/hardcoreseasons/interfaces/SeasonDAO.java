package us.mkaulfers.hardcoreseasons.interfaces;

import us.mkaulfers.hardcoreseasons.models.Season;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface SeasonDAO extends DAO<Season>{
    CompletableFuture<Integer> getActiveSeasonId();
}
