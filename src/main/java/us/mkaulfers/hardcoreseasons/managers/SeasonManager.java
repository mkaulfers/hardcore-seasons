package us.mkaulfers.hardcoreseasons.managers;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.models.Participant;
import us.mkaulfers.hardcoreseasons.models.Season;
import us.mkaulfers.hardcoreseasons.models.Vote;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static us.mkaulfers.hardcoreseasons.enums.LocalizationKey.*;

public class SeasonManager {
    HardcoreSeasons plugin;

    public SeasonManager(HardcoreSeasons plugin) {
        this.plugin = plugin;
        scheduleVoteReminder();
        scheduleSeasonEndTracker();
    }

    private void scheduleVoteReminder() {
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {

            Season activeSeason = plugin.db.seasons.getActiveSeason();
            Timestamp now = new Timestamp(System.currentTimeMillis());

            // Only prompt players to vote if the soft-end date has passed
            if (now.before(activeSeason.getSoftEndDate())) {
                return;
            }
            if (now.before(activeSeason.getHardEndDate())) {
                return;
            }

            List<Participant> participants = plugin.db.participants.getParticipants(activeSeason.getSeasonId());

            // Only prompt players to vote if there are less than or equal to the maxSurvivorsRemaining
            if (participants.size() > plugin.configManager.config.maxSurvivorsRemaining) {
                return;
            }

            // Only send a message to players who have not voted yet
            // Or to players who need to confirm their vote again
            // because the voteResetInterval has passed

            for (Participant participant : participants) {
                Vote vote = plugin.db.votes.getVote(participant.getPlayerId(), activeSeason.getSeasonId());

                int confirmationThreshold = plugin.configManager.config.voteResetInterval;
                Timestamp threshold = new Timestamp(now.getTime() - confirmationThreshold * 86400000L);

                if (vote == null || vote.getDateLastVoted() == null || vote.getDateLastVoted().before(threshold)) {
                    Player player = plugin.getServer().getPlayer(participant.getPlayerId());
                    if (player != null) {
                        player.sendMessage(plugin.configManager.localization.getLocalized(REQUESTING_VOTE_TOP));
                        player.sendMessage(plugin.configManager.localization.getLocalized(REQUESTING_VOTE_BOTTOM));
                    }
                }
            }

            List<Vote> votes = plugin.db.votes.getVotes(activeSeason.getSeasonId());

            // Check if there are enough votes to end the season
            int votePercent = plugin.configManager.config.minVotesToEndSeason;
            long voteCount = votes.size();
            long voteEndCount = votes.stream().filter(Vote::isShouldEndSeason).count();

            if (voteCount == 0) {
                return;
            }

            int voteEndPercent = (int) ((voteEndCount * 100) / voteCount);

            if (voteEndPercent >= votePercent) {
                endSeason(participants);
            }
        }, minutesToTicks(1), minutesToTicks(plugin.configManager.config.notificationInterval));
    }

    private void scheduleSeasonEndTracker() {
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {

                    Season activeSeason = plugin.db.seasons.getActiveSeason();

                    if (activeSeason == null) { return; }

                    List<Participant> participants = plugin.db.participants.getParticipants(activeSeason.getSeasonId());

                    if (participants == null) { return; }

                    List<Participant> activePlayers = new ArrayList<>();
                    Timestamp now = new Timestamp(System.currentTimeMillis());
                    Timestamp lastOnlineThreshold = new Timestamp(now.getTime() - plugin.configManager.config.lastLoginThreshold * 86400000L);

                    for (Participant participant : participants) {
                        if (participant.getLastOnline() == null || participant.getLastOnline().after(lastOnlineThreshold)) {
                            activePlayers.add(participant);
                        }
                    }

                    if (plugin.configManager.config.maxSeasonLength != -1) {
                        if (activeSeason.getHardEndDate().before(now)) {
                            endSeason(activePlayers);
                        }
                    }

                    if (activeSeason.getSoftEndDate().before(now)) {
                        if (activePlayers.isEmpty()) {
                            endSeason(activePlayers);
                        }
                    }

                }, 60, // Delay 3 seconds
                minutesToTicks(plugin.configManager.config.mySQLConfig.updateInterval));
    }

    /**
     * Ends the season, generates seasonal rewards, splitting as needed.
     * Destroys the world, and generates a new world.
     */
    private void endSeason(List<Participant> winners) {
        kickPlayersPreventRejoin();

        // Generate Rewards
        plugin.rewardManager.saveRewards(winners);

        // Generate New SeasonCMD
        Timestamp seasonStartDate = new Timestamp(System.currentTimeMillis());
        Timestamp seasonSoftEndDate = new Timestamp(System.currentTimeMillis() + plugin.configManager.config.minSeasonLength * 86400000L);
        Timestamp seasonHardEndDate = null;

        if (plugin.configManager.config.maxSeasonLength != -1) {
            seasonHardEndDate = new Timestamp(System.currentTimeMillis() + plugin.configManager.config.maxSeasonLength * 86400000L);
        }

        Season newSeason = new Season();
        int currentSeasonNum = plugin.activeSeason.getSeasonId();
        newSeason.setSeasonId(currentSeasonNum + 1);
        newSeason.setStartDate(seasonStartDate);
        newSeason.setSoftEndDate(seasonSoftEndDate);
        newSeason.setHardEndDate(seasonHardEndDate);

        plugin.db.seasons.setActiveSeason(newSeason);
        plugin.activeSeason = newSeason;

        plugin.placeholderManager.currentSeason = newSeason.getSeasonId();
        plugin.placeholderManager.nextSeason = newSeason.getSeasonId() + 1;

        // Generate New Worlds
        plugin.worldManager = new WorldManager(plugin);
        clearOfflinePlayerData();
        clearOnlinePlayerData();
    }

    private void kickPlayersPreventRejoin() {
        // Kick All Online Players
        Collection<? extends Player> players = plugin.getServer().getOnlinePlayers();
        for (Player player : players) {
            player.kickPlayer(plugin.configManager.localization.getLocalized(SEASON_ENDING));
        }
        plugin.isGeneratingNewSeason = true;

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            plugin.isGeneratingNewSeason = false;
        }, secondsToTicks(60));
    }

    private void clearOfflinePlayerData() {
        List<World> worlds = plugin.getServer().getWorlds();
        for (World world : worlds) {
            File playerData = new File(world.getName() + "/playerdata");
            if (playerData.exists()) {
                File[] files = playerData.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.getName().endsWith(".dat")) {
                            file.delete();
                        }
                    }
                }
            }
        }
    }

    private void clearOnlinePlayerData() {
        Collection<? extends Player> players = plugin.getServer().getOnlinePlayers();
        for (Player player : players) {
            player.getInventory().clear();
            player.getEnderChest().clear();
        }
    }

    private int secondsToTicks(int seconds) {
        return seconds * 20;
    }

    private int minutesToTicks(int minutes) {
        return secondsToTicks(minutes * 60);
    }

    private int hoursToTicks(int hours) {
        return minutesToTicks(hours * 60);
    }

    private int daysToTicks(int days) {
        return hoursToTicks(days * 24);
    }
}
