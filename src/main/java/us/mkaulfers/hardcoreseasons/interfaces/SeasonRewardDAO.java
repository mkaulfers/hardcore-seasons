package us.mkaulfers.hardcoreseasons.interfaces;

import us.mkaulfers.hardcoreseasons.models.SeasonReward;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SeasonRewardDAO extends DAO<SeasonReward> {
    CompletableFuture<List<SeasonReward>> getPlayerRewards(String playerId);
}
