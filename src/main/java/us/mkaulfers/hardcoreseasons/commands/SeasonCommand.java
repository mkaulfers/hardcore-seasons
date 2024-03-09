package us.mkaulfers.hardcoreseasons.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.guis.SelectSeasonRewardGUI;
import us.mkaulfers.hardcoreseasons.models.CommandNode;

import java.util.ArrayList;
import java.util.List;

import static us.mkaulfers.hardcoreseasons.models.LocalizationKey.*;

public class SeasonCommand implements TabExecutor {
    private CommandNode root;
    private HardcoreSeasons plugin;

    public SeasonCommand(HardcoreSeasons plugin) {
        this.plugin = plugin;

        // Root Branch
        CommandNode root = new CommandNode("season" , null, (sender -> {}));

        CommandNode claim = new CommandNode("claim", null, (sender -> {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                        SelectSeasonRewardGUI.make(player, plugin);
            } else {
                sender.sendMessage(plugin.configManager.localization.getLocalized(MUST_BE_A_PLAYER));
            }
        }));

        CommandNode start = new CommandNode("start", "admin", (sender -> {}));
        CommandNode end = new CommandNode("end", "admin", (sender -> {}));

        CommandNode reload = new CommandNode("reload", "admin", (sender -> {
            plugin.reloadConfig();
            sender.sendMessage(plugin.configManager.localization.getLocalized(CONFIG_RELOADED));
        }));

        root.addArg(claim);
        root.addArg(start);
        root.addArg(end);
        root.addArg(reload);

        // Vote Branch
        CommandNode vote = new CommandNode("vote", null, (sender -> {}));
        CommandNode vContinue = new CommandNode("continue", null, (sender -> {}));
        CommandNode vEnd = new CommandNode("end", null, (sender -> {}));

        vote.addArg(vContinue);
        vote.addArg(vEnd);

        root.addArg(vote);

        this.root = root;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        CommandNode currentNode = this.root;
        boolean executed = false;

        for (String arg : args) {
            CommandNode nextNode = null;
            boolean permissionDenied = false;

            for (CommandNode child : currentNode.arguments) {
                if (child.name.equalsIgnoreCase(arg)) {
                    if (child.hasPermission(commandSender)) {
                        nextNode = child;
                        break; // Match found, exit the loop
                    } else {
                        // Permission denied for this command
                        permissionDenied = true;
                        break; // Exit loop, but remember permission issue
                    }
                }
            }

            // Handle the case where a permission issue was detected
            if (permissionDenied) {
                commandSender.sendMessage(plugin.configManager.localization.getLocalized(NO_PERMISSION));
                return false;
            }

            // No matching node found after checking all children
            if (nextNode == null) {
                commandSender.sendMessage(plugin.configManager.localization.getLocalized(INVALID_COMMAND));
                return false;
            }

            // A matching node was found, prepare for the next iteration if any
            currentNode = nextNode;
            executed = true;
        }

        // Execute the matched command if found
        if (executed) {
            currentNode.execute(commandSender);
        }

        return executed;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        // Start from the root node and traverse based on the input args
        CommandNode currentNode = this.root;
        for (String arg : args) {
            // Check if the current argument has a matching child node
            boolean found = false;
            for (CommandNode child : currentNode.arguments) {
                if (child.name.equalsIgnoreCase(arg)) {
                    currentNode = child; // Move to the next node
                    found = true;
                    break;
                }
            }
            // If no matching child is found, break the loop
            if (!found) {
                break;
            }
        }

        // Collect suggestions for the current node
        List<String> suggestions = new ArrayList<>();
        for (CommandNode arg : currentNode.arguments) {
            if (arg.hasPermission(commandSender)) {
                suggestions.add(arg.name);
            }
        }

        return suggestions;
    }
}
