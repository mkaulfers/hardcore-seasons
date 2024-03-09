package us.mkaulfers.hardcoreseasons.models;

import org.bukkit.command.CommandSender;
import us.mkaulfers.hardcoreseasons.interfaces.CommandAction;

import java.util.ArrayList;
import java.util.List;

public class CommandNode {
    public String name;
    public String permission;
    public List<CommandNode> arguments;
    public CommandAction action;

    public CommandNode(String name, String permission, CommandAction action) {
        this.name = name;
        this.permission = permission == null ? null : "hardcoreseasons." + permission;
        this.arguments = new ArrayList<>();
        this.action = action;
    }

    public void addArg(CommandNode arg) {
        this.arguments.add(arg);
    }

    public boolean hasPermission(CommandSender sender) {
        // If permission is null, it's available to all players
        return this.permission == null || sender.hasPermission(this.permission);
    }

    public void execute(CommandSender sender) {
        if (this.action != null) {
            this.action.execute(sender);
        }
    }
}
