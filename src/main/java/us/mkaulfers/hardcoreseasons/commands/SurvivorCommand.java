package us.mkaulfers.hardcoreseasons.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;

import java.util.List;

public class SurvivorCommand implements TabExecutor {
    String survivorPermission = "hardcoreseasons.survivor";
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
                    if (player.hasPermission(survivorPermission)) {
                        showSurvivorGUI(player);
                        return true;
                    } else {
                        player.sendMessage("You do not have permission to use the survivor command");
                        return false;
                    }
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
        Inventory gui = Bukkit.createInventory(player, 27, "Survivor Menu");
        player.openInventory(gui);
    }
}
