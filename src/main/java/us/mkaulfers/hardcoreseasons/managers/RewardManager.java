package us.mkaulfers.hardcoreseasons.managers;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import us.mkaulfers.hardcoreseasons.enums.RewardItem;
import us.mkaulfers.hardcoreseasons.interfaceimpl.ChestDAOImpl;
import us.mkaulfers.hardcoreseasons.interfaceimpl.EndChestDAOImpl;
import us.mkaulfers.hardcoreseasons.interfaceimpl.InventoryDAOImpl;
import us.mkaulfers.hardcoreseasons.interfaces.ChestDAO;
import us.mkaulfers.hardcoreseasons.interfaces.EndChestDAO;
import us.mkaulfers.hardcoreseasons.interfaces.InventoryDAO;
import us.mkaulfers.hardcoreseasons.models.*;
import us.mkaulfers.hardcoreseasons.utils.InventoryUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RewardManager {
    Database db;

    public RewardManager(Database db) {
        this.db = db;
    }

    public CompletableFuture<List<RewardItem>> getGUIRewardItems(int seasonId) {
        return CompletableFuture.supplyAsync(() -> {
            List<RewardItem> rewards = new ArrayList<>();

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

                for (TrackedChest chest : chests) {
                    for (ItemStack item : InventoryUtils.itemStackArrayFromBase64(chest.contents)) {
                        rewards.add(new RewardItem(item, chest.id, RewardItemType.TRACKED_CHEST));
                    }
                }

                for (TrackedEndChest endChest : endChests) {
                    for (ItemStack item : InventoryUtils.itemStackArrayFromBase64(endChest.contents)) {
                        rewards.add(new RewardItem(item, endChest.id, RewardItemType.TRACKED_END_CHEST));
                    }
                }

                for (SurvivorInventory inventory : inventories) {
                    for (ItemStack item : InventoryUtils.itemStackArrayFromBase64(inventory.contents)) {
                        rewards.add(new RewardItem(item, inventory.id, RewardItemType.SURVIVOR_INVENTORY));
                    }
                }
            } catch (Exception e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to get rewards. " + e.getMessage());
            }

            return rewards;
        });

    }
}
