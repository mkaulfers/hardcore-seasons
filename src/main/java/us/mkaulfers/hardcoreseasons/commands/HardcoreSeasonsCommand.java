package us.mkaulfers.hardcoreseasons.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;

import java.util.ArrayList;
import java.util.List;

public class HardcoreSeasonsCommand implements TabExecutor {
    String reloadPermission = "hardcoreseasons.reload";
    HardcoreSeasons plugin;

    public HardcoreSeasonsCommand(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            if (strings.length == 1) {
                if (strings[0].equals("reload")) {
                    if (player.hasPermission(reloadPermission)) {
                        reloadPlugin(player);
                        return true;
                    } else {
                        player.sendMessage("You do not have permission to reload HardcoreSeasons configuration");
                        return false;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (command.getName().equals("hardcoreseasons") || command.getName().equals("hcs")) {
            if (commandSender instanceof Player) {
                if (args.length == 1 && commandSender.hasPermission(reloadPermission)) {
                    return StringUtil.copyPartialMatches(args[0], List.of("reload"), new ArrayList<>());
                }
            }
        }
        return null;
    }

    private void reloadPlugin(Player player) {
        plugin.reloadConfig();
        plugin.databaseManager.connect();
        plugin.databaseManager.initTables();
        plugin.databaseManager.seasonsManager.loadSeasons();
        plugin.databaseManager.survivorsManager.loadSurvivors();
        plugin.databaseManager.containersManager.loadContainers();
        plugin.databaseManager.inventoriesManager.loadInventories();
        plugin.databaseManager.endChestsManager.loadEndChests();
        player.sendMessage("HardcoreSeasons configuration reloaded");
    }
}
