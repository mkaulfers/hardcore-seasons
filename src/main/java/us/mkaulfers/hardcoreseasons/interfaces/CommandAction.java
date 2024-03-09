package us.mkaulfers.hardcoreseasons.interfaces;

import org.bukkit.command.CommandSender;

@FunctionalInterface
public interface CommandAction {
    void execute(CommandSender sender);
}
