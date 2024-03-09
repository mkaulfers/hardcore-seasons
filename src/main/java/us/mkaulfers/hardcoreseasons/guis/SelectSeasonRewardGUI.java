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
import us.mkaulfers.hardcoreseasons.models.SeasonReward;

import java.util.ArrayList;
import java.util.List;

import static us.mkaulfers.hardcoreseasons.models.LocalizationKey.*;

public class SelectSeasonRewardGUI {
    public static void make(Player player, HardcoreSeasons plugin) {
        player.sendMessage(plugin.configManager.localization.getLocalized(LOADING_SEASONS));
        ChestGui gui = new ChestGui(6, plugin.configManager.localization.getLocalized(SELECT_SEASON_TITLE));

        // Rewards
        PaginatedPane pages = new PaginatedPane(0, 0, 9, 5);

        plugin.rewardManager.getWonSeasonalRewardsForPlayer(player).thenAccept(rewards -> {
            List<GuiItem> guiItems = new ArrayList<>();

            for (SeasonReward seasonReward : rewards) {
                ItemStack guiChestItem = new ItemStack(Material.CHEST);
                ItemMeta guiChestItemMeta = guiChestItem.getItemMeta();
                guiChestItemMeta.setDisplayName(plugin.configManager.localization.getLocalized(SEASON_ITEM_NAME)  + " " + ChatColor.AQUA + seasonReward.getSeasonId());
                guiChestItem.setItemMeta(guiChestItemMeta);

                guiItems.add(new GuiItem(guiChestItem, event -> {
                    event.setCancelled(true);
                    RedeemRewardsForSeasonGUI.make(player, seasonReward.getSeasonId(), plugin);
                }));
            }

            pages.populateWithGuiItems(guiItems);

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
            previousMeta.setDisplayName(plugin.configManager.localization.getLocalized(SEASON_PREVIOUS));
            previous.setItemMeta(previousMeta);

            // Current
            ItemStack current = new ItemStack(Material.PAPER);
            ItemMeta currentMeta = current.getItemMeta();
            currentMeta.setDisplayName(plugin.configManager.localization.getLocalized(SEASON_PAGE_COUNTER)  + " "  + ChatColor.AQUA + (pages.getPage() + 1) + ChatColor.GOLD + "/" + ChatColor.AQUA + (pages.getPages()));
            current.setItemMeta(currentMeta);

            // Next
            ItemStack next = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = next.getItemMeta();
            nextMeta.setDisplayName(plugin.configManager.localization.getLocalized(SEASON_NEXT));
            next.setItemMeta(nextMeta);

            // Close
            ItemStack close = new ItemStack(Material.BARRIER);
            ItemMeta closeMeta = close.getItemMeta();
            closeMeta.setDisplayName(plugin.configManager.localization.getLocalized(SEASON_CLOSE));
            close.setItemMeta(closeMeta);

            navigation.addItem(new GuiItem(previous, event -> {
                if (pages.getPage() > 0) {
                    pages.setPage(pages.getPage() - 1);
                    currentMeta.setDisplayName(plugin.configManager.localization.getLocalized(SEASON_PAGE_COUNTER)  + " "  + ChatColor.AQUA + (pages.getPage() + 1) + ChatColor.GOLD + "/" + ChatColor.AQUA + (pages.getPages()));
                    current.setItemMeta(currentMeta);
                    gui.update();
                }
            }), 3, 0);

            navigation.addItem(new GuiItem(current, event -> {
            }), 4, 0);

            navigation.addItem(new GuiItem(next, event -> {
                if (pages.getPage() < pages.getPages() - 1) {
                    pages.setPage(pages.getPage() + 1);
                    currentMeta.setDisplayName(plugin.configManager.localization.getLocalized(SEASON_PAGE_COUNTER)  + " "  + ChatColor.AQUA + (pages.getPage() + 1) + ChatColor.GOLD + "/" + ChatColor.AQUA + (pages.getPages()));
                    current.setItemMeta(currentMeta);
                    gui.update();
                }
            }), 5, 0);

            navigation.addItem(new GuiItem(close, event -> event.getWhoClicked().closeInventory()), 8, 0);

            // Must operate on the main thread
            Bukkit.getScheduler().runTask(plugin, () -> {
                gui.addPane(pages);
                gui.addPane(navigation);
                gui.show(player);
            });
        }).exceptionally(e -> {
            Bukkit.getLogger().warning("[Hardcore Seasons]: Failed to get rewards: " + e.getMessage());
            return null;
        });
    }
}
