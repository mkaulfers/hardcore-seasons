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
                    reloadPlugin(player);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (command.getName().equals("hardcoreseasons") || command.getName().equals("hcs")) {
            if (commandSender instanceof Player) {
                if (args.length == 1) {
                    return StringUtil.copyPartialMatches(args[0], List.of("reload"), new ArrayList<>());
                }
            }
        }
        return null;
    }

    private void reloadPlugin(Player player) {
        plugin.reloadConfig();
        player.sendMessage("HardcoreSeasons configuration reloaded");
    }
}
