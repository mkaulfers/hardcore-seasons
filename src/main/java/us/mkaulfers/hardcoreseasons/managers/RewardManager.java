package us.mkaulfers.hardcoreseasons.managers;

import org.bukkit.inventory.ItemStack;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.utils.InventoryUtils;

import java.util.ArrayList;
import java.util.List;

class WonSeason {
    public String seasonName;
    public int seasonId;

    public WonSeason(int seasonId) {
        this.seasonName = "Season " + seasonId;
        this.seasonId = seasonId;
    }
}


public class RewardManager {
    HardcoreSeasons plugin;

    public RewardManager(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }

    public WonSeason[] getWonSeasons() {
        return new WonSeason[] {
                new WonSeason(1),
                new WonSeason(2),
                new WonSeason(3),
                new WonSeason(4),
                new WonSeason(5),
                new WonSeason(6),
                new WonSeason(7),
                new WonSeason(8),
                new WonSeason(9),
                new WonSeason( 10)
        };
    }

    public List<ItemStack> getRewards(int seasonId) {
        String[] encodedContainers = plugin.databaseManager.chestManager.containers.stream()
                .filter(container -> container.seasonId == seasonId)
                .map(container -> container.contents)
                .toArray(String[]::new);

        String[] encodedInventories = plugin.databaseManager.inventoryManager.inventories.stream()
                .filter(inventory -> inventory.seasonId == seasonId)
                .map(inventory -> inventory.contents)
                .toArray(String[]::new);

        String[] encodedEndChests = plugin.databaseManager.endChestManager.endChests.stream()
                .filter(endChest -> endChest.seasonId == seasonId)
                .map(endChest -> endChest.contents)
                .toArray(String[]::new);

        List<ItemStack> mergedContents = new ArrayList<>();

        try {
            for (String encodedContainer : encodedContainers) {
                ItemStack[] decodedContainersContents = InventoryUtils.itemStackArrayFromBase64(encodedContainer);
                mergedContents.addAll(List.of(decodedContainersContents));
            }

            for (String encodedInventory : encodedInventories) {
                ItemStack[] decodedInventoryContents = InventoryUtils.itemStackArrayFromBase64(encodedInventory);
                mergedContents.addAll(List.of(decodedInventoryContents));
            }

            for (String encodedEndChest : encodedEndChests) {
                ItemStack[] decodedEndChestContents = InventoryUtils.itemStackArrayFromBase64(encodedEndChest);
                mergedContents.addAll(List.of(decodedEndChestContents));
            }

            return mergedContents;
        } catch (Exception e) {
            plugin.getLogger().warning("[Hardcore Seasons]: Could not deserialize containers contents while distributing rewards.\n" + e.getMessage());
        }
        return new ArrayList<>();
    }
}
