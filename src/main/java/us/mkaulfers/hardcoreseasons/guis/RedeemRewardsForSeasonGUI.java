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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.interfaces.RewardSource;
import us.mkaulfers.hardcoreseasons.utils.InventoryUtils;

import java.io.IOException;
import java.util.*;

public class RedeemRewardsForSeasonGUI {
    public static void make(Player player, int seasonId, HardcoreSeasons plugin) {
        player.sendMessage(ChatColor.BLUE + "Loading rewards for season " + ChatColor.AQUA + seasonId);
        ChestGui gui = new ChestGui(6, ChatColor.BLUE + "Season " + ChatColor.AQUA + seasonId);

        // Rewards
        PaginatedPane pages = new PaginatedPane(0, 0, 9, 5);

        plugin.rewardManager.getRewardSourcesAsync(seasonId).thenAccept(rewardSources -> {
            List<ItemStack> rewards = new ArrayList<>();
            List<ItemStack> shulkerBoxes = new ArrayList<>();

            for (RewardSource rewardSource : rewardSources) {
                try {
                    ItemStack[] contents = InventoryUtils.itemStackArrayFromBase64(rewardSource.getContents());
                    rewards.addAll(Arrays.asList(contents));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            packRewardsIntoShulkerBoxes(new LinkedList<>(rewards), shulkerBoxes);

            pages.populateWithItemStacks(shulkerBoxes);

            // Background
            OutlinePane backgroundPane = new OutlinePane(0, 5, 9, 1);

            ItemStack blank = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta blankMeta = blank.getItemMeta();
            blankMeta.setDisplayName(" ");
            blank.setItemMeta(blankMeta);

            backgroundPane.addItem(new GuiItem(blank));
            backgroundPane.setRepeat(true);
            backgroundPane.setPriority(Pane.Priority.LOWEST);


            // Navigation
            StaticPane navigation = new StaticPane(0, 5, 9, 1);
            navigation.setOnClick(event -> event.setCancelled(true));

            // Go Back
            ItemStack goBack = new ItemStack(Material.SPECTRAL_ARROW);
            ItemMeta goBackMeta = goBack.getItemMeta();
            goBackMeta.setDisplayName(ChatColor.GOLD + "Go Back");
            goBack.setItemMeta(goBackMeta);


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

            navigation.addItem(new GuiItem(goBack, event -> {
                SelectSeasonRewardGUI.make(player, plugin);
            }), 0, 0);

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
                gui.addPane(backgroundPane);
                gui.addPane(pages);
                gui.addPane(navigation);
                gui.show(player);
            });
        }).exceptionally(e -> {
            Bukkit.getLogger().warning("[Hardcore Seasons]: Failed to get rewards: \n" + e.getMessage());
            return null;
        });
    }

    // TODO: Make this part of the win condition and save on season end rather than on redeem.
    // TODO: (Cont.) This way we can just deserialize the contents and give them to the player, as well as
    // TODO: (Cont.) remove the reward from the database without tracking the RewardSource.
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
