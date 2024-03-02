package us.mkaulfers.hardcoreseasons.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;

import java.util.List;

public class SurvivorCommand implements TabExecutor {

    HardcoreSeasons plugin;

    public SurvivorCommand(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            if (strings.length == 0) {
                if (s.equalsIgnoreCase("survivor") || s.equalsIgnoreCase("surv")) {
                    showSurvivorGUI(player);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }

    public void showSurvivorGUI(Player player) {
        openRewardGUI(player);
    }

    public void openRewardGUI(Player player) {
        Inventory gui = Bukkit.createInventory(player, 54, ChatColor.DARK_BLUE + "Claim Rewards");

        ItemStack goBackPanel = new ItemStack(Material.SPECTRAL_ARROW);
        ItemStack pageBack = new ItemStack(Material.ARROW);
        ItemStack currentPage = new ItemStack(Material.PAPER);
        ItemStack pageNext = new ItemStack(Material.ARROW);
        ItemStack close = new ItemStack(Material.BARRIER);

        ItemMeta goBackPanelMeta = goBackPanel.getItemMeta();
        ItemMeta pageBackMeta = pageBack.getItemMeta();
        ItemMeta currentPageMeta = currentPage.getItemMeta();
        ItemMeta pageNextMeta = pageNext.getItemMeta();
        ItemMeta closeMeta = close.getItemMeta();

        if (goBackPanelMeta != null) {
            goBackPanelMeta.setDisplayName(ChatColor.GOLD + "Go Back");
            goBackPanel.setItemMeta(goBackPanelMeta);
        }

        if (pageBackMeta != null) {
            pageBackMeta.setDisplayName(ChatColor.GOLD + "Previous");
            pageBack.setItemMeta(pageBackMeta);
        }

        if (currentPageMeta != null) {
            currentPageMeta.setDisplayName(ChatColor.GOLD + "Page " + ChatColor.AQUA + "1" + ChatColor.GOLD + "/" + ChatColor.AQUA + "1");
            currentPage.setItemMeta(currentPageMeta);
        }

        if (pageNextMeta != null) {
            pageNextMeta.setDisplayName(ChatColor.GOLD + "Next");
            pageNext.setItemMeta(pageNextMeta);
        }

        if (closeMeta != null) {
            closeMeta.setDisplayName(ChatColor.RED + "Close");
            close.setItemMeta(closeMeta);
        }

        gui.setItem(45, goBackPanel);
        gui.setItem(48, pageBack);
        gui.setItem(49, currentPage);
        gui.setItem(50, pageNext);
        gui.setItem(53, close);

        for (int i : List.of(46, 47, 51, 52)) {
            ItemStack placeholder = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta placeholderMeta = placeholder.getItemMeta();
            if (placeholderMeta != null) {
                placeholderMeta.setDisplayName(" ");
                placeholder.setItemMeta(placeholderMeta);
            }
            gui.setItem(i, placeholder);
        }

        player.openInventory(gui);
    }
}
