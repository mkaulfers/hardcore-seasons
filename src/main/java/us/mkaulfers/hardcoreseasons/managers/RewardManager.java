package us.mkaulfers.hardcoreseasons.managers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.orm.*;
import us.mkaulfers.hardcoreseasons.utils.InventoryUtils;

import java.io.IOException;
import java.util.*;

public class RewardManager {
    HardcoreSeasons plugin;

    public RewardManager(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }

    public void saveRewards(List<HParticipant> winners) {
        if (winners.isEmpty()) {
            return;
        }


        List<ItemStack> rewards = new ArrayList<>();
        List<HEndChest> endChests = plugin.hDataSource.getEndChests(plugin.currentSeasonNum);
        List< HInventory> inventories = plugin.hDataSource.getInventories(plugin.currentSeasonNum);
        List<HTrackedContainer> trackedContainers = plugin.hDataSource.getTrackedContainers(plugin.currentSeasonNum);

        try {
            for (HEndChest endChest : endChests) {
                List<ItemStack> contents = List.of(InventoryUtils.itemStackArrayFromBase64(endChest.getContents()));
                rewards.addAll(contents);
            }

            for (HInventory inventory : inventories) {
                List<ItemStack> contents = List.of(InventoryUtils.itemStackArrayFromBase64(inventory.getContents()));
                rewards.addAll(contents);
            }

            for (HTrackedContainer trackedContainer : trackedContainers) {
                List<ItemStack> contents = List.of(InventoryUtils.itemStackArrayFromBase64(trackedContainer.getContents()));
                rewards.addAll(contents);
            }
        } catch (IOException e) {
            Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to get rewards. " + e.getMessage());
            return;
        }

        // Remove endChests, inventories, and trackedContainers from the database
        plugin.hDataSource.deleteEndChests(plugin.currentSeasonNum);
        plugin.hDataSource.deleteInventories(plugin.currentSeasonNum);
        plugin.hDataSource.deleteTrackedContainers(plugin.currentSeasonNum);

        distributeRewards(winners, rewards);
    }

    private void distributeRewards(List<HParticipant> winners, List<ItemStack> rewards) {
        // Step 1: Aggregate ItemStacks by Material
        Map<Material, Integer> aggregatedRewards = new HashMap<>();
        for (ItemStack reward : rewards) {
            aggregatedRewards.merge(reward.getType(), reward.getAmount(), Integer::sum);
        }

        // Sort winners based on joinDate, then by lastOnline for handling remainders
        winners.sort(
                Comparator.comparing(HParticipant::getJoinDate)
                        .thenComparing(HParticipant::getLastOnline)
                        .reversed()
        );

        // Step 2: Distribute Items Evenly Among Winners
        Map<HParticipant, List<ItemStack>> winnerRewards = new HashMap<>();
        for (Map.Entry<Material, Integer> entry : aggregatedRewards.entrySet()) {
            Material material = entry.getKey();
            int totalAmount = entry.getValue();
            int baseAmount = totalAmount / winners.size();
            int remainder = totalAmount % winners.size();

            for (int i = 0; i < winners.size(); i++) {
                HParticipant winner = winners.get(i);
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

    private void saveWinnerRewardsToDatabase(Map<HParticipant, List<ItemStack>> winnerRewards) {
        // This method should contain logic to save the rewards for each player in the database
        // The exact implementation will depend on how your database is structured and how you manage transactions
        for (Map.Entry<HParticipant, List<ItemStack>> entry : winnerRewards.entrySet()) {
            HParticipant winner = entry.getKey();
            List<ItemStack> rewards = entry.getValue();

            List<ItemStack> shulkerBoxes = new ArrayList<>();
            packRewardsIntoShulkerBoxes(new LinkedList<>(rewards), shulkerBoxes);

            HSeasonReward seasonReward = new HSeasonReward();
            seasonReward.setPlayerId(winner.getPlayerId());
            seasonReward.setSeasonId(plugin.currentSeasonNum);
            seasonReward.setContents(InventoryUtils.itemStackArrayToBase64(shulkerBoxes.toArray(new ItemStack[0])));

            plugin.hDataSource.setSeasonReward(seasonReward);
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
