package us.mkaulfers.hardcoreseasons.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import us.mkaulfers.hardcoreseasons.interfaceimpl.SeasonRewardDAOImpl;
import us.mkaulfers.hardcoreseasons.interfaces.*;
import us.mkaulfers.hardcoreseasons.interfaceimpl.ChestDAOImpl;
import us.mkaulfers.hardcoreseasons.interfaceimpl.EndChestDAOImpl;
import us.mkaulfers.hardcoreseasons.interfaceimpl.InventoryDAOImpl;
import us.mkaulfers.hardcoreseasons.models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RewardManager {
    Database db;

    public RewardManager(Database db) {
        this.db = db;
    }

    public CompletableFuture<List<SeasonReward>> getWonSeasonalRewardsForPlayer(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            List<SeasonReward> seasonRewards = new ArrayList<>();
            try {
                SeasonRewardDAO seasonRewardDAO = new SeasonRewardDAOImpl(db);
                seasonRewards = seasonRewardDAO.getPlayerRewards(player.getUniqueId().toString()).get();
                return seasonRewards;
            } catch (Exception e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to get seasonRewards for player. " + e.getMessage());
                return seasonRewards;
            }
        });
    }

    public CompletableFuture<List<RewardSource>> getRewardSourcesAsync(int seasonId) {
        return CompletableFuture.supplyAsync(() -> {
            List<RewardSource> rewards = new ArrayList<>();

            ChestDAO chestDAO = new ChestDAOImpl(db);
            EndChestDAO endChestDAO = new EndChestDAOImpl(db);
            InventoryDAO inventoryDAO = new InventoryDAOImpl(db);

            // Create CompletableFuture for each DAO operation
            CompletableFuture<List<TrackedChest>> chestDAOFuture = chestDAO.getAll(seasonId);
            CompletableFuture<List<TrackedEndChest>> endChestDAOFuture = endChestDAO.getAll(seasonId);
            CompletableFuture<List<SurvivorInventory>> inventoryDAOFuture = inventoryDAO.getAll(seasonId);

            // Combine all DAO Futures
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(chestDAOFuture, endChestDAOFuture, inventoryDAOFuture);

            try {
                // Wait until all futures are complete
                allFutures.get();

                // After all futures are complete, combine their results into 'rewards'
                List<TrackedChest> chests = chestDAOFuture.get();
                List<TrackedEndChest> endChests = endChestDAOFuture.get();
                List<SurvivorInventory> inventories = inventoryDAOFuture.get();

                rewards.addAll(chests);
                rewards.addAll(endChests);
                rewards.addAll(inventories);
            } catch (Exception e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to get rewards. " + e.getMessage());
            }

            return rewards;
        });
    }
}
