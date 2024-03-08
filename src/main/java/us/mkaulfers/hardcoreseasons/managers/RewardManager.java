package us.mkaulfers.hardcoreseasons.managers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.interfaceimpl.SeasonRewardDAOImpl;
import us.mkaulfers.hardcoreseasons.interfaces.*;
import us.mkaulfers.hardcoreseasons.interfaceimpl.ChestDAOImpl;
import us.mkaulfers.hardcoreseasons.interfaceimpl.EndChestDAOImpl;
import us.mkaulfers.hardcoreseasons.interfaceimpl.InventoryDAOImpl;
import us.mkaulfers.hardcoreseasons.models.*;
import us.mkaulfers.hardcoreseasons.utils.InventoryUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class RewardManager {
    HardcoreSeasons plugin;

    public RewardManager(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }

    public CompletableFuture<List<SeasonReward>> getWonSeasonalRewardsForPlayer(Player player) {
        // Async operation to obtain SeasonRewardDAO
        CompletableFuture<SeasonRewardDAO> seasonRewardDAOFuture = CompletableFuture.supplyAsync(() -> new SeasonRewardDAOImpl(plugin.database));

        // Use thenCompose to chain the async operation of getting player rewards
        return seasonRewardDAOFuture.thenCompose(seasonRewardDAO ->
                seasonRewardDAO.getPlayerRewards(player.getUniqueId().toString())
        ).exceptionally(ex -> {
            Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to get seasonRewards for player. " + ex.getMessage());
            return Collections.emptyList(); // Provide an empty list in case of failure
        });
    }

    public CompletableFuture<List<RewardSource>> getRewardSourcesAsync(int seasonId) {
        return CompletableFuture.supplyAsync(() -> {
            List<RewardSource> rewards = new ArrayList<>();

            ChestDAO chestDAO = new ChestDAOImpl(plugin.database);
            EndChestDAO endChestDAO = new EndChestDAOImpl(plugin.database);
            InventoryDAO inventoryDAO = new InventoryDAOImpl(plugin.database);

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

    public void saveRewards(List<Participant> winners) {
        if (winners.isEmpty()) {
            return;
        }

        List<RewardSource> rewardSources = getRewardSourcesAsync(plugin.currentSeasonNum).join();
        List<ItemStack> rewards = new ArrayList<>();

        for (RewardSource rewardSource : rewardSources) {
            try {
                ItemStack[] contents = InventoryUtils.itemStackArrayFromBase64(rewardSource.getContents());
                rewards.addAll(Arrays.asList(contents));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        distributeRewards(winners, rewards);
    }

    private void distributeRewards(List<Participant> winners, List<ItemStack> rewards) {
        // Step 1: Aggregate ItemStacks by Material
        Map<Material, Integer> aggregatedRewards = new HashMap<>();
        for (ItemStack reward : rewards) {
            aggregatedRewards.merge(reward.getType(), reward.getAmount(), Integer::sum);
        }

        // Sort winners based on joinDate, then by lastOnline for handling remainders
        winners.sort(
                Comparator.comparing(Participant::getJoinDate)
                        .thenComparing(Participant::getLastOnline)
                        .reversed()
        );

        // Step 2: Distribute Items Evenly Among Winners
        Map<Participant, List<ItemStack>> winnerRewards = new HashMap<>();
        for (Map.Entry<Material, Integer> entry : aggregatedRewards.entrySet()) {
            Material material = entry.getKey();
            int totalAmount = entry.getValue();
            int baseAmount = totalAmount / winners.size();
            int remainder = totalAmount % winners.size();

            for (int i = 0; i < winners.size(); i++) {
                Participant winner = winners.get(i);
                int amountToGive = baseAmount;
                if (i == 0 && remainder > 0) { // First winner gets the remainder
                    amountToGive += remainder;
                }

                if (amountToGive > 0) {
                    winnerRewards.computeIfAbsent(winner, k -> new ArrayList<>())
                            .add(new ItemStack(material, amountToGive));
                }
            }
        }

        // Step 4 & 5: Create ItemStacks for Winners and Update Database
        // Here you would have a mechanism to convert these rewards into a form that can be stored in your database
        // and then call the method to update the database.
        // This is an example and may need to be adjusted based on your database structure and methods.
        saveWinnerRewardsToDatabase(winnerRewards);
    }

    private void saveWinnerRewardsToDatabase(Map<Participant, List<ItemStack>> winnerRewards) {
        // This method should contain logic to save the rewards for each player in the database
        // The exact implementation will depend on how your database is structured and how you manage transactions
        for (Map.Entry<Participant, List<ItemStack>> entry : winnerRewards.entrySet()) {
            Participant winner = entry.getKey();
            List<ItemStack> rewards = entry.getValue();

            List<ItemStack> shulkerBoxes = new ArrayList<>();
            packRewardsIntoShulkerBoxes(new LinkedList<>(rewards), shulkerBoxes);
            SeasonRewardDAO seasonRewardDAO = new SeasonRewardDAOImpl(plugin.database);
            seasonRewardDAO.save(new SeasonReward(
                    0,
                    plugin.currentSeasonNum,
                    winner.playerId,
                    InventoryUtils.itemStackArrayToBase64(shulkerBoxes.toArray(new ItemStack[0])),
                    false
            ));
        }
    }

    private static void packRewardsIntoShulkerBoxes(Queue<ItemStack> rewardQueue, List<ItemStack> shulkerBoxes) {
        ItemStack shulkerBox = new ItemStack(Material.SHULKER_BOX);
        BlockStateMeta blockStateMeta = (BlockStateMeta) shulkerBox.getItemMeta();
        ShulkerBox shulkerBoxBlock = (ShulkerBox) blockStateMeta.getBlockState();

        while (!rewardQueue.isEmpty()) {

            ItemStack reward = rewardQueue.poll();
            HashMap<Integer, ItemStack> overflow = shulkerBoxBlock.getInventory().addItem(reward);

            // If there's overflow, it means our ShulkerBox can't fit any more items of the same kind.
            if (!overflow.isEmpty()) {

                // Store the current ShulkerBox
                blockStateMeta.setBlockState(shulkerBoxBlock);
                shulkerBox.setItemMeta(blockStateMeta);
                shulkerBoxes.add(shulkerBox);

                // And we start a fresh one
                shulkerBox = new ItemStack(Material.SHULKER_BOX);
                blockStateMeta = (BlockStateMeta) shulkerBox.getItemMeta();
                shulkerBoxBlock = (ShulkerBox) blockStateMeta.getBlockState();

                // Add overflow back into the reward queue
                rewardQueue.add(overflow.get(0));
            }
        }

        // Finally, we store the last ShulkerBox, whether it's full or partially filled
        blockStateMeta.setBlockState(shulkerBoxBlock);
        shulkerBox.setItemMeta(blockStateMeta);
        shulkerBoxes.add(shulkerBox);
    }
}
