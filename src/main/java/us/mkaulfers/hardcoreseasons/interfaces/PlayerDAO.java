package us.mkaulfers.hardcoreseasons.interfaces;

import us.mkaulfers.hardcoreseasons.models.Participant;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PlayerDAO extends DAO<Participant> {
    CompletableFuture<Participant> get(UUID playerId, int seasonId);
    CompletableFuture<List<Participant>> getAllForSeason(int seasonId);
    CompletableFuture<List<Participant>> getAllAliveForSeason(int seasonId);
}
