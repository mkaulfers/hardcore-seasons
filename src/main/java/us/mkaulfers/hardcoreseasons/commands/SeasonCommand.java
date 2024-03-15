package us.mkaulfers.hardcoreseasons.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginLoader;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.guis.SelectSeasonRewardGUI;
import us.mkaulfers.hardcoreseasons.interfaceimpl.SeasonDAOImpl;
import us.mkaulfers.hardcoreseasons.interfaceimpl.VoteDAOImpl;
import us.mkaulfers.hardcoreseasons.interfaces.SeasonDAO;
import us.mkaulfers.hardcoreseasons.interfaces.VoteDAO;
import us.mkaulfers.hardcoreseasons.models.CommandNode;
import us.mkaulfers.hardcoreseasons.models.Season;
import us.mkaulfers.hardcoreseasons.models.Vote;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static us.mkaulfers.hardcoreseasons.enums.LocalizationKey.*;

public class SeasonCommand implements TabExecutor {
    private CommandNode root;
    private HardcoreSeasons plugin;

    public SeasonCommand(HardcoreSeasons plugin) {
        this.plugin = plugin;

        // Root Branch
        CommandNode root = new CommandNode("season", null, (sender -> {
        }));

        CommandNode claim = new CommandNode("claim", null, (sender -> {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                SelectSeasonRewardGUI.make(player, plugin);
            } else {
                sender.sendMessage(plugin.configManager.localization.getLocalized(MUST_BE_A_PLAYER));
            }
        }));

        CommandNode reload = new CommandNode("reload", "admin", (sender -> {
            plugin.reloadConfig();
            sender.sendMessage(plugin.configManager.localization.getLocalized(CONFIG_RELOADED));
        }));

        if (plugin.configManager.config.claimingEnabled) {
            root.addArg(claim);
        }

        root.addArg(reload);

        // Vote Branch
        CommandNode vote = new CommandNode("vote", null, (sender -> {
        }));

        CommandNode vContinue = new CommandNode("continue", null, (sender -> {
            castVote(sender, false);
        }));

        CommandNode vEnd = new CommandNode("end", null, (sender -> {
            castVote(sender, true);
        }));

        vote.addArg(vContinue);
        vote.addArg(vEnd);

        root.addArg(vote);

        this.root = root;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
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

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] args) {
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

    private void castVote(CommandSender sender, boolean shouldEndSeason) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        SeasonDAO seasonDAO = new SeasonDAOImpl(plugin.database);
        Season activeSeason = seasonDAO.get(plugin.currentSeasonNum).join();

        if (activeSeason == null || !activeSeason.softEndDate.before(now)) {
            sender.sendMessage(plugin.configManager.localization.getLocalized(CANNOT_VOTE));
            return;
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;

            Vote vote = new Vote(
                    0,
                    plugin.currentSeasonNum,
                    player.getUniqueId(),
                    new Timestamp(System.currentTimeMillis()),
                    shouldEndSeason
            );

            VoteDAO voteDAO = new VoteDAOImpl(plugin.database);

            voteDAO.save(vote).thenAccept(success -> {
                if (success != null) {
                    sender.sendMessage(plugin.configManager.localization.getLocalized(shouldEndSeason ? VOTE_END_SUCCESS : VOTE_CONTINUE_SUCCESS));
                } else {
                    sender.sendMessage(plugin.configManager.localization.getLocalized(VOTE_FAIL));
                }
            });

        } else {
            sender.sendMessage(plugin.configManager.localization.getLocalized(MUST_BE_A_PLAYER));
        }
    }
}
