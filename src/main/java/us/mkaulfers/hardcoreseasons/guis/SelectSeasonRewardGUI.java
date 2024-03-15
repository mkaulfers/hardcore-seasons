package us.mkaulfers.hardcoreseasons.guis;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.orm.HSeasonReward;

import java.util.ArrayList;
import java.util.List;

import static us.mkaulfers.hardcoreseasons.enums.InternalPlaceholder.*;
import static us.mkaulfers.hardcoreseasons.enums.LocalizationKey.*;

public class SelectSeasonRewardGUI {
    public static void make(Player player, HardcoreSeasons plugin) {
        player.sendMessage(plugin.configManager.localization.getLocalized(LOADING_SEASONS));
        ChestGui gui = new ChestGui(6, plugin.configManager.localization.getLocalized(SELECT_SEASON_TITLE));

        // Rewards
        PaginatedPane pages = new PaginatedPane(0, 0, 9, 5);

        List<GuiItem> seasonChestItem = new ArrayList<>();

        List<HSeasonReward> rewards = plugin.hDataSource.getSeasonRewards(player.getUniqueId());

        for (HSeasonReward seasonReward : rewards) {
            ItemStack guiChestItem = new ItemStack(Material.CHEST);
            ItemMeta guiChestItemMeta = guiChestItem.getItemMeta();

            plugin.placeholderManager.setPlaceholderValue(PAST_SEASON_NUMBER, String.valueOf(seasonReward.getSeasonId()));

            guiChestItemMeta.setDisplayName(plugin.configManager.localization.getLocalized(SEASON_ITEM_NAME));
            guiChestItem.setItemMeta(guiChestItemMeta);

            seasonChestItem.add(new GuiItem(guiChestItem, event -> {
                event.setCancelled(true);
                RedeemRewardsForSeasonGUI.make(player, seasonReward.getSeasonId(), plugin);
            }));
        }

        pages.populateWithGuiItems(seasonChestItem);
        gui.addPane(getBackground());

        // Must operate on the main thread
        Bukkit.getScheduler().runTask(plugin, () -> {
            gui.addPane(pages);
            gui.addPane(getNavigation(plugin, gui, pages));
            gui.show(player);
        });
    }

    private static OutlinePane getBackground() {
        // Background
        OutlinePane backgroundPane = new OutlinePane(0, 5, 9, 1);

        ItemStack blank = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta blankMeta = blank.getItemMeta();
        blankMeta.setDisplayName(" ");
        blank.setItemMeta(blankMeta);

        backgroundPane.addItem(new GuiItem(blank));
        backgroundPane.setRepeat(true);
        backgroundPane.setPriority(Pane.Priority.LOWEST);

        return backgroundPane;
    }

    private static StaticPane getNavigation(HardcoreSeasons plugin, ChestGui gui, PaginatedPane pages) {
        // Navigation
        StaticPane navigation = new StaticPane(0, 5, 9, 1);
        navigation.setOnClick(event -> event.setCancelled(true));

        // Previous
        ItemStack previous = new ItemStack(Material.ARROW);
        ItemMeta previousMeta = previous.getItemMeta();
        previousMeta.setDisplayName(plugin.configManager.localization.getLocalized(SEASON_PREVIOUS));
        previous.setItemMeta(previousMeta);

        plugin.placeholderManager.setPlaceholderValue(SEASON_SELECT_CURRENT_PAGE, String.valueOf(pages.getPage()));
        plugin.placeholderManager.setPlaceholderValue(SEASON_SELECT_TOTAL_PAGES, String.valueOf(pages.getPages()));

        // Current
        ItemStack current = new ItemStack(Material.PAPER);
        ItemMeta currentMeta = current.getItemMeta();
        currentMeta.setDisplayName(plugin.configManager.localization.getLocalized(SEASON_PAGE_COUNTER));
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
                currentMeta.setDisplayName(plugin.configManager.localization.getLocalized(SEASON_PAGE_COUNTER));
                current.setItemMeta(currentMeta);
                gui.update();
            }
        }), 3, 0);

        navigation.addItem(new GuiItem(current, event -> {
        }), 4, 0);

        navigation.addItem(new GuiItem(next, event -> {
            if (pages.getPage() < pages.getPages() - 1) {
                pages.setPage(pages.getPage() + 1);
                currentMeta.setDisplayName(plugin.configManager.localization.getLocalized(SEASON_PAGE_COUNTER));
                current.setItemMeta(currentMeta);
                gui.update();
            }
        }), 5, 0);

        navigation.addItem(new GuiItem(close, event -> event.getWhoClicked().closeInventory()), 8, 0);

        return navigation;
    }
}
