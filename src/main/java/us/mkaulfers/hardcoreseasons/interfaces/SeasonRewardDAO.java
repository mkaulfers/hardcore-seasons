package us.mkaulfers.hardcoreseasons.interfaces;

import org.bukkit.inventory.ItemStack;
import us.mkaulfers.hardcoreseasons.models.SeasonReward;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface SeasonRewardDAO extends DAO<SeasonReward> {
    CompletableFuture<List<SeasonReward>> getPlayerRewards(String playerId);
    void updateRedeemedRewards(int seasonId, UUID playerId, List<ItemStack> itemStackContents);
}
