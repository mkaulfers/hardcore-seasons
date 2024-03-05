package us.mkaulfers.hardcoreseasons.managers;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.interfaceimpl.ChestDAOImpl;
import us.mkaulfers.hardcoreseasons.interfaceimpl.EndChestDAOImpl;
import us.mkaulfers.hardcoreseasons.interfaceimpl.InventoryDAOImpl;
import us.mkaulfers.hardcoreseasons.interfaces.ChestDAO;
import us.mkaulfers.hardcoreseasons.interfaces.EndChestDAO;
import us.mkaulfers.hardcoreseasons.interfaces.InventoryDAO;
import us.mkaulfers.hardcoreseasons.models.Database;
import us.mkaulfers.hardcoreseasons.models.SurvivorInventory;
import us.mkaulfers.hardcoreseasons.models.TrackedChest;
import us.mkaulfers.hardcoreseasons.models.TrackedEndChest;
import us.mkaulfers.hardcoreseasons.utils.InventoryUtils;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RewardManager {
    Database db;

    public RewardManager(Database db) {
        this.db = db;
    }

    public CompletableFuture<List<ItemStack>> getRewards(int seasonId) {
        return CompletableFuture.supplyAsync(() -> {
            List<ItemStack> rewards = new ArrayList<>();

            // Get Chests From Season ID
            try {
                ChestDAO chestDAO = new ChestDAOImpl(db);
                List<TrackedChest> chests = chestDAO.getAll(seasonId).get();

                for (TrackedChest chest : chests) {
                    ItemStack[] decodedItems = InventoryUtils.itemStackArrayFromBase64(chest.contents);
                    rewards.addAll(Arrays.asList(decodedItems));
                }

            } catch (Exception e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to get chest rewards." + e.getMessage());
            }

            // Get End Chests From Season ID
            try {
                EndChestDAO endChestDAO = new EndChestDAOImpl(db);
                List<TrackedEndChest> endChests = endChestDAO.getAll(seasonId).get();

                for (TrackedEndChest endChest : endChests) {
                    ItemStack[] decodedItems = InventoryUtils.itemStackArrayFromBase64(endChest.contents);
                    rewards.addAll(Arrays.asList(decodedItems));
                }

            } catch (Exception e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to get end chest rewards." + e.getMessage());
            }

            // Get Inventories From Season ID
            try {
                InventoryDAO inventoryDAO = new InventoryDAOImpl(db);
                List<SurvivorInventory> inventories = inventoryDAO.getAll(seasonId).get();

                for (SurvivorInventory inventory : inventories) {
                    ItemStack[] decodedItems = InventoryUtils.itemStackArrayFromBase64(inventory.contents);
                    rewards.addAll(Arrays.asList(decodedItems));
                }

            } catch (Exception e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to get inventory rewards." + e.getMessage());
            }

            return rewards;
        });
    }
}
