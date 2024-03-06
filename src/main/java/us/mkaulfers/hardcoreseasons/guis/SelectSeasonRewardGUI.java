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

public class SelectSeasonRewardGUI {
    public static void make(Player player, HardcoreSeasons plugin) {
        player.sendMessage(ChatColor.BLUE + "Loading seasonal rewards.");
        ChestGui gui = new ChestGui(6, ChatColor.BLUE + "Select Season");

        // Rewards
        PaginatedPane pages = new PaginatedPane(0, 0, 9, 5);

        plugin.rewardManager.getWonSeasonalRewardsForPlayer(player).thenAccept(rewards -> {
            List<GuiItem> guiItems = new ArrayList<>();

            for (SeasonReward seasonReward : rewards) {
                ItemStack guiChestItem = new ItemStack(Material.CHEST);
                ItemMeta guiChestItemMeta = guiChestItem.getItemMeta();
                guiChestItemMeta.setDisplayName(ChatColor.GOLD + "Season " + ChatColor.AQUA + seasonReward.getSeasonId());
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
