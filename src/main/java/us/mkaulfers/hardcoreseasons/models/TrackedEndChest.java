package us.mkaulfers.hardcoreseasons.models;

import org.bukkit.inventory.ItemStack;
import us.mkaulfers.hardcoreseasons.interfaces.RewardSource;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class TrackedEndChest implements Comparable<TrackedEndChest>, RewardSource {


    public int id;
    public int seasonId;
    public UUID playerId;
    public String contents;

    @Override
    public int compareTo(TrackedEndChest o) {
        return this.playerId.compareTo(o.playerId);
    }

    public TrackedEndChest(int id, UUID playerId, int activeSeason, String serializedEndChest) {
        this.id = id;
        this.seasonId = activeSeason;
        this.playerId = playerId;
        this.contents = serializedEndChest;
    }

    @Override
    public String getContents() {
        return contents;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public CompletableFuture<List<ItemStack>> redeemReward(Database db) {
        return null;
    }

    @Override
    public CompletableFuture<Boolean> updateRewardContents(List<ItemStack> items, Database db) {
        return null;
    }
}
