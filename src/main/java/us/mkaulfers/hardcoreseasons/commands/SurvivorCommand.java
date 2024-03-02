package us.mkaulfers.hardcoreseasons.commands;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
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
        PaginatedGui gui = Gui.paginated()
                .title(Component.text(ChatColor.DARK_BLUE + "Claim Rewards"))
                .rows(6)
                .create();

        GuiItem goBackPanel = ItemBuilder
                .from(Material.SPECTRAL_ARROW)
                .name(Component.text(ChatColor.GOLD + "Go Back"))
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    player.sendMessage("Go Back");
                });

        GuiItem currentPage = ItemBuilder
                .from(Material.PAPER)
                .name(Component.text(ChatColor.GOLD + "Page " + ChatColor.AQUA + gui.getCurrentPageNum() + ChatColor.GOLD + "/" + ChatColor.AQUA + gui.getPagesNum()))
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    player.sendMessage("Current Page");
                });

        GuiItem pageBack = ItemBuilder
                .from(Material.ARROW)
                .name(Component.text(ChatColor.GOLD + "Previous"))
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    gui.previous();
                    gui.updateItem(49, currentPage);
                    player.sendMessage("Previous");
                });

        GuiItem pageNext = ItemBuilder
                .from(Material.ARROW)
                .name(Component.text(ChatColor.GOLD + "Next"))
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    gui.next();
                    gui.updateItem(49, currentPage);
                    player.sendMessage("Next");
                });

        GuiItem close = ItemBuilder
                .from(Material.BARRIER)
                .name(Component.text(ChatColor.RED + "Close"))
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    event.getWhoClicked().closeInventory();
                });

        GuiItem placeholder = ItemBuilder
                .from(Material.BLACK_STAINED_GLASS_PANE)
                .name(Component.text(""))
                .asGuiItem(event -> {
                    event.setCancelled(true);
                });

        gui.setItem(45, goBackPanel);
        gui.setItem(46, placeholder);
        gui.setItem(47, placeholder);
        gui.setItem(48, pageBack);
        gui.setItem(49, currentPage);
        gui.setItem(50, pageNext);
        gui.setItem(51, placeholder);
        gui.setItem(52, placeholder);
        gui.setItem(53, close);

        gui.open(player);
    }
}
