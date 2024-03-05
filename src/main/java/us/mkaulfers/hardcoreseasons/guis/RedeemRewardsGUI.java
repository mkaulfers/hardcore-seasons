package us.mkaulfers.hardcoreseasons.guis;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.enums.RewardItem;

import java.util.*;
import java.util.stream.Collectors;

public class RedeemRewardsGUI {
    public static void make(Player player, HardcoreSeasons plugin) {
        player.sendMessage(ChatColor.GREEN + "Loading rewards GUI...");
        ChestGui gui = new ChestGui(6, ChatColor.DARK_BLUE + "Player Menu");

        // Rewards
        PaginatedPane pages = new PaginatedPane(0, 0, 9, 5);

        plugin.rewardManager.getGUIRewardItems(1).thenAccept(rewardItems -> {
            // Generating a map of unique items and their counts
            Map<ItemStack, Integer> uniqueItemsWithCount = getUniqueItems(rewardItems);
            int itemsPerPage = 5 * 9;
            List<ItemStack> guiItems = new ArrayList<>(uniqueItemsWithCount.keySet());

            int pagesNeeded = (int) Math.max(Math.ceil((double) guiItems.size() / (double) itemsPerPage), 1.0);

            for (int i = 0; i < pagesNeeded; ++i) {
                OutlinePane page = new OutlinePane(0, 0, 9, 5);

                for (int j = 0; j < itemsPerPage; ++j) {
                    int index = i * itemsPerPage + j;
                    if (index >= guiItems.size()) {
                        break;
                    }

                    ItemStack guiItemStack = guiItems.get(index);
                    Integer count = uniqueItemsWithCount.get(guiItemStack);

                    // Clone the item to avoid modifying original
                    ItemStack guiItemStackClone = guiItemStack.clone();
                    ItemMeta itemMeta = guiItemStackClone.getItemMeta();
                    itemMeta.setLore(List.of(ChatColor.GOLD + "Amount: " + ChatColor.AQUA + count));
                    guiItemStackClone.setItemMeta(itemMeta);

                    page.addItem(new GuiItem(guiItemStackClone, event -> {
                        Player p = (Player) event.getWhoClicked();

                        // Fetch list of rewardItems based on event item
                        List<RewardItem> matchRewardItems = rewardItems.stream()
                                .filter(ri -> ri.getItem().getType() == event.getCurrentItem().getType())
                                .collect(Collectors.toList());

                        if (matchRewardItems.isEmpty()) {
                            p.sendMessage(ChatColor.RED + "You don't have any more of this item.");
                            gui.update();
                            return;
                        }

                        // Take first matchRewardItem and assuming all matchRewardItems have same id and type
                        RewardItem rewardItem = matchRewardItems.get(0);

                        // Claim reward first
                        p.getInventory().addItem(rewardItem.getItem());

                        // Remove reward from the database based on id and type
                        switch (rewardItem.getType()) {
                            case TRACKED_CHEST:
//                      plugin.rewardManager.removeTrackedChest(rewardItem.getId());
                                break;
                            case TRACKED_END_CHEST:
//                      plugin.rewardManager.removeTrackedEndChest(rewardItem.getId());
                                break;
                            case SURVIVOR_INVENTORY:
//                      plugin.rewardManager.removeSurvivorInventory(rewardItem.getId());
                                break;
                        }

                        rewardItems.remove(rewardItem);


                        GuiItem guiItem = page.getItems().get(index);
                        ItemStack updatedItem = guiItem.getItem();
                        ItemMeta updatedMeta = updatedItem.getItemMeta();
                        updatedMeta.setLore(List.of(
                                ChatColor.GREEN + "Redeemed"
                        ));
                        updatedItem.setItemMeta(updatedMeta);
                        guiItem.setItem(updatedItem);

                        gui.update();
                    }));
                }

                pages.addPane(i, page);
            }

            gui.addPane(pages);

            // Background
            OutlinePane backgroundPane = new OutlinePane(0, 5, 9, 1);

            ItemStack blank = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta blankMeta = blank.getItemMeta();
            blankMeta.setDisplayName(" ");
            blank.setItemMeta(blankMeta);

            backgroundPane.addItem(new GuiItem(blank));
            backgroundPane.setRepeat(true);
            backgroundPane.setPriority(Pane.Priority.LOWEST);
            gui.addPane(backgroundPane);

            // Navigation
            StaticPane navigation = new StaticPane(0, 5, 9, 1);
            navigation.setOnClick(event -> event.setCancelled(true));

            // Previous
            ItemStack previous = new ItemStack(Material.ARROW);
            ItemMeta previousMeta = previous.getItemMeta();
            previousMeta.setDisplayName(ChatColor.GOLD + "Previous");
            previous.setItemMeta(previousMeta);

            // Current
            ItemStack current = new ItemStack(Material.PAPER);
            ItemMeta currentMeta = current.getItemMeta();
            currentMeta.setDisplayName(ChatColor.GOLD + "Page " + ChatColor.AQUA + (pages.getPage() + 1) + ChatColor.GOLD + "/" + ChatColor.AQUA + (pages.getPages()));
            current.setItemMeta(currentMeta);

            // Next
            ItemStack next = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = next.getItemMeta();
            nextMeta.setDisplayName(ChatColor.GOLD + "Next");
            next.setItemMeta(nextMeta);

            // Close
            ItemStack close = new ItemStack(Material.BARRIER);
            ItemMeta closeMeta = close.getItemMeta();
            closeMeta.setDisplayName(ChatColor.RED + "Close");
            close.setItemMeta(closeMeta);

            navigation.addItem(new GuiItem(previous, event -> {
                if (pages.getPage() > 0) {
                    pages.setPage(pages.getPage() - 1);
                    currentMeta.setDisplayName(ChatColor.GOLD + "Page " + ChatColor.AQUA + (pages.getPage() + 1) + ChatColor.GOLD + "/" + ChatColor.AQUA + (pages.getPages()));
                    current.setItemMeta(currentMeta);
                    gui.update();
                }
            }), 3, 0);

            navigation.addItem(new GuiItem(current, event -> {
            }), 4, 0);

            navigation.addItem(new GuiItem(next, event -> {
                if (pages.getPage() < pages.getPages() - 1) {
                    pages.setPage(pages.getPage() + 1);
                    currentMeta.setDisplayName(ChatColor.GOLD + "Page " + ChatColor.AQUA + (pages.getPage() + 1) + ChatColor.GOLD + "/" + ChatColor.AQUA + (pages.getPages()));
                    current.setItemMeta(currentMeta);
                    gui.update();
                }
            }), 5, 0);

            navigation.addItem(new GuiItem(close, event -> event.getWhoClicked().closeInventory()), 8, 0);

            // Must operate on the main thread
            Bukkit.getScheduler().runTask(plugin, () -> {
                gui.addPane(navigation);
                gui.show(player);
            });
        }).exceptionally(e -> {
            Bukkit.getLogger().warning("[Hardcore Seasons]: Failed to get rewards: " + e.getMessage());
            return null;
        });
    }

    private static Map<ItemStack, Integer> getUniqueItems(List<RewardItem> rewardItems) {
        Map<ItemStack, Integer> uniqueItemsWithCount = new HashMap<>();

        for (RewardItem rewardItem : rewardItems) {
            ItemStack reward = rewardItem.getItem();

            // Make a copy to avoid modifying the original
            ItemStack copy = reward.clone();
            copy.setAmount(1);

            // Check for existing items that match type and lore
            Optional<ItemStack> opt = uniqueItemsWithCount.keySet().stream()
                    .filter(is -> is.getType() == copy.getType() && is.getItemMeta().equals(copy.getItemMeta()))
                    .findFirst();

            if (opt.isPresent()) {
                // Existing item found, increment count.
                ItemStack existing = opt.get();
                uniqueItemsWithCount.put(existing, uniqueItemsWithCount.get(existing) + reward.getAmount());
            } else {
                // No existing item, add the item with count.
                uniqueItemsWithCount.put(copy, reward.getAmount());
            }
        }
        return uniqueItemsWithCount;
    }
}
