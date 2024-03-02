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

        gui.setDefaultClickAction(event -> {
            List<Integer> buttonSlots = List.of(45, 46, 47, 48, 50, 51, 52, 53);
            if (buttonSlots.contains(event.getSlot())) {
                event.setCancelled(true);
            }
        });

        // Create a bunch of test items, dirt, to add to the gui.
        for (int i = 0; i < 100; i++) {
            GuiItem item = ItemBuilder
                    .from(Material.DIRT)
                    .asGuiItem();

            gui.addItem(item);
        }

        GuiItem goBackPanel = ItemBuilder
                .from(Material.SPECTRAL_ARROW)
                .name(Component.text(ChatColor.GOLD + "Go Back"))
                .asGuiItem(event -> {
                    player.sendMessage("Go Back");
                });

        GuiItem pageBack = ItemBuilder
                .from(Material.ARROW)
                .name(Component.text(ChatColor.GOLD + "Previous"))
                .asGuiItem(event -> {
                    gui.previous();
                    gui.updateItem(49, currentPageItem(gui.getCurrentPageNum(), gui.getPagesNum()));
                    player.sendMessage("Page " + gui.getCurrentPageNum() + "/" + gui.getPagesNum());
                });

        gui.updateItem(49, currentPageItem(gui.getCurrentPageNum(), gui.getPagesNum()));

        GuiItem pageNext = ItemBuilder
                .from(Material.ARROW)
                .name(Component.text(ChatColor.GOLD + "Next"))
                .asGuiItem(event -> {
                    gui.next();
                    gui.updateItem(49, currentPageItem(gui.getCurrentPageNum(), gui.getPagesNum()));
                    player.sendMessage("Page " + gui.getCurrentPageNum() + "/" + gui.getPagesNum());
                });

        GuiItem close = ItemBuilder
                .from(Material.BARRIER)
                .name(Component.text(ChatColor.RED + "Close"))
                .asGuiItem(event -> {
                    event.getWhoClicked().closeInventory();
                });

        GuiItem placeholder = ItemBuilder
                .from(Material.BLACK_STAINED_GLASS_PANE)
                .name(Component.text(""))
                .asGuiItem();

        gui.setItem(45, goBackPanel);
        gui.setItem(46, placeholder);
        gui.setItem(47, placeholder);
        gui.setItem(48, pageBack);
        gui.setItem(50, pageNext);
        gui.setItem(51, placeholder);
        gui.setItem(52, placeholder);
        gui.setItem(53, close);

        gui.open(player);
    }

    private GuiItem currentPageItem(int currentPage, int totalPages) {
        return ItemBuilder
                .from(Material.PAPER)
                .name(Component.text(ChatColor.GOLD + "Page " + ChatColor.AQUA + currentPage + ChatColor.GOLD + "/" + ChatColor.AQUA + totalPages))
                .asGuiItem(event -> {
                    event.getWhoClicked().sendMessage("Current Page");
                });
    }
}
