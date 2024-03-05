package us.mkaulfers.hardcoreseasons.guis;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.managers.ItemDiffManager;
import us.mkaulfers.hardcoreseasons.utils.InventoryUtils;

import java.util.List;
import java.util.UUID;

public class RedeemRewardsGUI {
    public static void make(Player player, HardcoreSeasons plugin) {
        UUID playerId = player.getUniqueId();
        ChestGui gui = new ChestGui(6, ChatColor.DARK_BLUE + "Player Menu");

        // Rewards
        PaginatedPane pages = new PaginatedPane(0, 0, 9, 5);

        // TODO: Replace with items from the database, that count as rewards.
        ItemStack[] databaseItems = new ItemStack[0];
        List<ItemStack> guiItems = InventoryUtils.getGUIItemsList(databaseItems);
        ItemDiffManager itemDiffManager = new ItemDiffManager(databaseItems, guiItems);

        int itemsPerPage = 5 * 9;
        int pagesNeeded = (int)Math.max(Math.ceil((double) itemDiffManager.getGuiItems().size() / (double)itemsPerPage), 1.0);

        for(int i = 0; i < pagesNeeded; ++i) {
            OutlinePane page = new OutlinePane(0, 0, 9, 5);

            for(int j = 0; j < itemsPerPage; ++j) {
                int index = i * itemsPerPage + j;
                if (index >= itemDiffManager.getGuiItems().size()) {
                    break;
                }

                page.addItem(new GuiItem(itemDiffManager.getGuiItems().get(index), event -> {
                    ItemStack item = event.getCurrentItem();
                    Player p = (Player) event.getWhoClicked();

                    List<List<ItemStack>> separatedItems = itemDiffManager.fetchItemsOfType(item);
                    List<ItemStack> fetchedItems = separatedItems.get(0);

                    if (!fetchedItems.isEmpty()) {
                        p.getInventory().addItem(fetchedItems.toArray(new ItemStack[0]));
                    } else {
                        p.sendMessage(ChatColor.RED + "You don't have any more of this item.");
                    }

                    GuiItem guiItem = page.getItems().get(index);
                    ItemStack updatedItem = guiItem.getItem();
                    ItemMeta updatedMeta = updatedItem.getItemMeta();
                    updatedMeta.setLore(List.of(
                            ChatColor.GREEN+ "Redeemed"
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

        gui.addPane(navigation);
        gui.show(player);
    }
}
