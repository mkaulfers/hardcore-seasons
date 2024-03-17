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
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.orm.HSeasonReward;
import us.mkaulfers.hardcoreseasons.utils.InventoryUtils;

import java.io.IOException;
import java.util.*;

import static us.mkaulfers.hardcoreseasons.enums.LocalizationKey.*;

public class RedeemRewardGUI {
    public static void make(Player player, int seasonId, HardcoreSeasons plugin) {
        player.sendMessage(plugin.configManager.localization.getLocalized(LOADING_REWARDS));
        plugin.placeholderManager.pastSeasonNum = seasonId;
        ChestGui gui = new ChestGui(6, plugin.configManager.localization.getLocalized(SEASON_ITEM_NAME));

        // Rewards
        int length = 9;
        int height = 5;
        PaginatedPane pages = new PaginatedPane(0, 0, length, height);

        List<HSeasonReward> playerRewards = plugin.hDataSource.getSeasonRewards(player.getUniqueId());
        List<ItemStack> shulkerBoxes = new ArrayList<>();

        for (HSeasonReward seasonReward : playerRewards) {
            if (seasonReward.getSeasonId() == seasonId) {
                try {
                    ItemStack[] contents = InventoryUtils.itemStackArrayFromBase64(seasonReward.getContents());
                    shulkerBoxes.addAll(Arrays.asList(contents));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (!shulkerBoxes.isEmpty()) {
            int itemsPerPage = height * length;
            int pagesNeeded = (int) Math.max(Math.ceil((double) shulkerBoxes.size() / (double) itemsPerPage), 1.0);

            for (int i = 0; i < pagesNeeded; ++i) {
                OutlinePane page = new OutlinePane(0, 0, length, height);

                for (int j = 0; j < itemsPerPage; ++j) {
                    int index = i * itemsPerPage + j;
                    if (index >= shulkerBoxes.size()) {
                        break;
                    }

                    GuiItem guiItem = new GuiItem(shulkerBoxes.get(index));

                    guiItem.setAction(event1 -> {
                        handleGuiItemAction(seasonId, plugin, event1, gui, guiItem, event1.getSlot(), page, shulkerBoxes, player);
                    });

                    page.addItem(guiItem);
                }

                pages.addPane(i, page);
            }
        }

        gui.addPane(getBackground());

        // Must operate on the main thread
        gui.addPane(pages);
        gui.addPane(getNavigation(plugin, player, gui, pages));
        gui.show(player);
    }

    // Method to handle the action logic
    private static void handleGuiItemAction(
            int seasonId,
            HardcoreSeasons plugin,
            InventoryClickEvent event,
            ChestGui gui,
            GuiItem guiItem,
            int index,
            OutlinePane page,
            List<ItemStack> shulkerBoxes,
            Player player
    ) {
        event.setCancelled(true);

        // Your existing logic
        BlockStateMeta blockStateMeta = (BlockStateMeta) shulkerBoxes.get(index).getItemMeta();
        ShulkerBox shulkerBox = (ShulkerBox) blockStateMeta.getBlockState();
        ItemStack[] contents = shulkerBox.getInventory().getContents();
        ItemStack[] nonNullContents = Arrays.stream(contents).filter(Objects::nonNull).toArray(ItemStack[]::new);
        Map<Integer, ItemStack> remainingItems = player.getInventory().addItem(nonNullContents);

        if (!remainingItems.isEmpty()) {

            ItemStack[] remainingNonNullItems = remainingItems.values().stream().filter(Objects::nonNull).toArray(ItemStack[]::new);
            shulkerBox.getInventory().setContents(remainingNonNullItems);
            blockStateMeta.setBlockState(shulkerBox);
            shulkerBoxes.get(index).setItemMeta(blockStateMeta);

            guiItem.setAction(event1 -> {
                handleGuiItemAction(seasonId, plugin, event1, gui, guiItem, event1.getSlot(), page, shulkerBoxes, player);
            });

            player.sendMessage(plugin.configManager.localization.getLocalized(INVENTORY_FULL));
        } else {
            shulkerBoxes.remove(index);
            page.removeItem(guiItem);
        }

        Bukkit.getLogger().info("Number of shulkerBoxes: " + shulkerBoxes.size());

        String shulkerBoxesBase64 = InventoryUtils.itemStackArrayToBase64(shulkerBoxes.toArray(new ItemStack[0]));
        plugin.hDataSource.updateSeasonReward(seasonId, player.getUniqueId(), shulkerBoxesBase64);

        gui.update();
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


    private static StaticPane getNavigation(HardcoreSeasons plugin, Player player, ChestGui gui, PaginatedPane pages) {
        // Navigation
        StaticPane navigation = new StaticPane(0, 5, 9, 1);
        navigation.setOnClick(event -> event.setCancelled(true));

        // Go Back
        ItemStack goBack = new ItemStack(Material.SPECTRAL_ARROW);
        ItemMeta goBackMeta = goBack.getItemMeta();
        goBackMeta.setDisplayName(plugin.configManager.localization.getLocalized(REWARD_GO_BACK));
        goBack.setItemMeta(goBackMeta);


        // Previous
        ItemStack previous = new ItemStack(Material.ARROW);
        ItemMeta previousMeta = previous.getItemMeta();
        previousMeta.setDisplayName(plugin.configManager.localization.getLocalized(REWARD_PREVIOUS));
        previous.setItemMeta(previousMeta);

        plugin.placeholderManager.rewardCurrentPage = pages.getPage();
        plugin.placeholderManager.rewardNextPage = pages.getPages();

        // Current
        ItemStack current = new ItemStack(Material.PAPER);
        ItemMeta currentMeta = current.getItemMeta();
        currentMeta.setDisplayName(plugin.configManager.localization.getLocalized(REWARD_PAGE) + " " + ChatColor.AQUA + (pages.getPage() + 1) + ChatColor.GOLD + "/" + ChatColor.AQUA + (pages.getPages()));
        current.setItemMeta(currentMeta);

        // Next
        ItemStack next = new ItemStack(Material.ARROW);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.setDisplayName(plugin.configManager.localization.getLocalized(REWARD_NEXT));
        next.setItemMeta(nextMeta);

        // Close
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName(plugin.configManager.localization.getLocalized(REWARD_CLOSE));
        close.setItemMeta(closeMeta);

        // Go Back
        navigation.addItem(new GuiItem(goBack, event -> {
            SelectSeasonGUI.make(player, plugin);
        }), 0, 0);

        // Previous
        navigation.addItem(new GuiItem(previous, event -> {
            if (pages.getPage() > 0) {
                pages.setPage(pages.getPage() - 1);
                currentMeta.setDisplayName(plugin.configManager.localization.getLocalized(REWARD_PAGE) + " " + ChatColor.AQUA + (pages.getPage() + 1) + ChatColor.GOLD + "/" + ChatColor.AQUA + (pages.getPages()));
                current.setItemMeta(currentMeta);
                gui.update();
            }
        }), 3, 0);

        // Current
        navigation.addItem(new GuiItem(current, event -> {
        }), 4, 0);

        // Next
        navigation.addItem(new GuiItem(next, event -> {
            if (pages.getPage() < pages.getPages() - 1) {
                pages.setPage(pages.getPage() + 1);
                currentMeta.setDisplayName(plugin.configManager.localization.getLocalized(REWARD_PAGE) + " " + ChatColor.AQUA + (pages.getPage() + 1) + ChatColor.GOLD + "/" + ChatColor.AQUA + (pages.getPages()));
                current.setItemMeta(currentMeta);
                gui.update();
            }
        }), 5, 0);

        // Close
        navigation.addItem(new GuiItem(close, event -> event.getWhoClicked().closeInventory()), 8, 0);

        return navigation;
    }
}
