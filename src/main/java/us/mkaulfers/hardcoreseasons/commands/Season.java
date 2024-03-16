package us.mkaulfers.hardcoreseasons.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.guis.SelectSeasonGUI;
import us.mkaulfers.hardcoreseasons.models.ResRequest;
import us.mkaulfers.hardcoreseasons.orm.HSeason;
import us.mkaulfers.hardcoreseasons.orm.HVote;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static us.mkaulfers.hardcoreseasons.enums.InternalPlaceholder.CURRENT_SEASON;
import static us.mkaulfers.hardcoreseasons.enums.InternalPlaceholder.RESURRECTED_PLAYER_NAME;
import static us.mkaulfers.hardcoreseasons.enums.LocalizationKey.*;

@CommandAlias("season|sn")
public class Season extends BaseCommand {
    HardcoreSeasons plugin;
    public Season(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }

    @Subcommand("claim")
    public void onClaim(Player player) {
        SelectSeasonGUI.make(player, plugin);
    }

    @Subcommand("reload")
    @CommandPermission("hardcoreseasons.admin")
    public void onReload(Player player) {
        plugin.reloadConfig();
        player.sendMessage(plugin.configManager.localization.getLocalized(CONFIG_RELOADED));
    }

    @Subcommand("vote continue")
    public void onVote(Player player) {
        castVote(player, false);
    }

    @Subcommand("vote end")
    public void onVoteEnd(Player player) {
        castVote(player, true);
    }

    @Subcommand("info")
    public void onInfo(Player player) {
        player.sendMessage("Hello, world!");
    }

    @Subcommand("stats")
    public void onStats(Player player) {
        player.sendMessage("Hello, world!");
    }

    @Subcommand("resurrect")
    @CommandPermission("hardcoreseasons.admin")
    @CommandCompletion("@resurrectCompletion")
    public void onResurrect(CommandSender sender, @Single ResRequest request) {
        plugin.hDataSource.resurrectPlayer(request.playerId);
        plugin.placeholderManager.setPlaceholderValue(CURRENT_SEASON, String.valueOf(plugin.currentSeasonNum));
        plugin.placeholderManager.setPlaceholderValue(RESURRECTED_PLAYER_NAME, request.playerName);
        sender.sendMessage(plugin.configManager.localization.getLocalized(PLAYER_RESURRECTED));
    }

    private void castVote(CommandSender sender, boolean shouldEndSeason) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        HSeason activeSeason = plugin.hDataSource.getActiveSeason();

        if (activeSeason == null || !activeSeason.getSoftEndDate().before(now)) {
            sender.sendMessage(plugin.configManager.localization.getLocalized(CANNOT_VOTE));
            return;
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;

            HVote vote = new HVote();
            vote.setSeasonId(activeSeason.getId());
            vote.setPlayerId(player.getUniqueId());
            vote.setDateLastVoted(now);
            vote.setShouldEndSeason(shouldEndSeason);

            boolean successful = plugin.hDataSource.setVote(vote);

            if (successful) {
                sender.sendMessage(plugin.configManager.localization.getLocalized(shouldEndSeason ? VOTE_END_SUCCESS : VOTE_CONTINUE_SUCCESS));
            } else {
                sender.sendMessage(plugin.configManager.localization.getLocalized(VOTE_FAIL));
            }

        } else {
            sender.sendMessage(plugin.configManager.localization.getLocalized(MUST_BE_A_PLAYER));
        }
    }
}
