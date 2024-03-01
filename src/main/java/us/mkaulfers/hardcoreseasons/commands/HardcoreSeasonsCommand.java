package us.mkaulfers.hardcoreseasons.commands;

import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/// Default command is /hardcoreseasons or /hcs
/// This command can take a `reload` argument to reload the plugin's configuration
public class HardcoreSeasonsCommand implements TabExecutor {
    String reloadPermission = "hardcoreseasons.reload";

    HardcoreSeasons plugin;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            if (strings.length == 1) {
                if (strings[0].equals("reload")) {
                    if (player.hasPermission(reloadPermission)) {
                        plugin.reloadConfig();
                        player.sendMessage("HardcoreSeasons configuration reloaded");
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

    /// Should return a list of possible completions for a command.
    /// Only current command is /hcs reload or /hardcoreseasons reload
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equals("hardcoreseasons") || command.getName().equals("hcs")) {
            if (commandSender instanceof Player) {
                if (strings.length == 1 && commandSender.hasPermission(reloadPermission)) {
                    ArrayList<String> completions = new ArrayList<>();
                    completions.add("reload");
                    completions.add("vote");
                    completions.add("endSeason");
                    return completions;
                }
            }
        }
        return null;
    }

    public HardcoreSeasonsCommand(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }
}
