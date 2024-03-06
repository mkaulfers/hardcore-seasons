package us.mkaulfers.hardcoreseasons.interfaces;

import org.bukkit.inventory.ItemStack;
import us.mkaulfers.hardcoreseasons.models.Database;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface RewardSource {
    String getContents();
    int getId();
    CompletableFuture<List<ItemStack>> redeemReward(Database db);
    CompletableFuture<Boolean> updateRewardContents(List<ItemStack> items, Database db);
}
