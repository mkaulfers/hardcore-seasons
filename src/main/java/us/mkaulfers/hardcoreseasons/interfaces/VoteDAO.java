package us.mkaulfers.hardcoreseasons.interfaces;

import us.mkaulfers.hardcoreseasons.models.Vote;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface VoteDAO extends DAO<Vote>{
    CompletableFuture<List<Vote>> getAllForSeason(int seasonId);
    CompletableFuture<Vote> getPlayerVote(UUID playerId, int seasonId);

}