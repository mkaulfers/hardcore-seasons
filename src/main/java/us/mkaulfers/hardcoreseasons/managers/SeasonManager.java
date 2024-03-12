package us.mkaulfers.hardcoreseasons.managers;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.interfaceimpl.PlayerDAOImpl;
import us.mkaulfers.hardcoreseasons.interfaceimpl.SeasonDAOImpl;
import us.mkaulfers.hardcoreseasons.interfaceimpl.VoteDAOImpl;
import us.mkaulfers.hardcoreseasons.interfaces.PlayerDAO;
import us.mkaulfers.hardcoreseasons.interfaces.SeasonDAO;
import us.mkaulfers.hardcoreseasons.interfaces.VoteDAO;
import us.mkaulfers.hardcoreseasons.models.Participant;
import us.mkaulfers.hardcoreseasons.models.Season;
import us.mkaulfers.hardcoreseasons.models.Vote;

import java.io.File;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static us.mkaulfers.hardcoreseasons.models.LocalizationKey.*;

public class SeasonManager {
    HardcoreSeasons plugin;

    public SeasonManager(HardcoreSeasons plugin) {
        this.plugin = plugin;
        scheduleVoteReminder();
        scheduleSeasonEndTracker();
    }

    private void scheduleVoteReminder() {
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            SeasonDAO seasonDAO = new SeasonDAOImpl(plugin.database);
            PlayerDAO playerDAO = new PlayerDAOImpl(plugin.database);

            int lastLoginThreshold = plugin.configManager.config.lastLoginThreshold;
            int maxSurvivorsRemaining = plugin.configManager.config.maxSurvivorsRemaining;

            Season activeSeason = seasonDAO.get(plugin.currentSeasonNum).join();

            if (activeSeason == null) {
                activeSeason = initializeFirstSeason(seasonDAO);
            }

            Timestamp now = new Timestamp(System.currentTimeMillis());
            Timestamp softEndDate = activeSeason.softEndDate;
            Timestamp lastLoginThresholdDate = new Timestamp(System.currentTimeMillis() - lastLoginThreshold * 86400000L);

            if (softEndDate.before(now)) {
                List<Participant> participants = playerDAO.getAllForSeason(plugin.currentSeasonNum).join();
                participants.removeIf(player -> player.lastOnline.before(lastLoginThresholdDate));

                if (participants.size() <= maxSurvivorsRemaining) {
                    VoteDAO voteDAO = new VoteDAOImpl(plugin.database);
                    List<Vote> votes = voteDAO.getAllForSeason(plugin.currentSeasonNum).join();

                    // We want to reset any votes that have a date_last_voted date that is older than the voteResetInterval
                    for (Vote vote : votes) {
                        if (shouldResetVote(vote)) {
                            vote.dateLastVoted = null;
                            voteDAO.update(vote).join();
                        }
                    }

                    List<Participant> participantsToNotify = new ArrayList<>();

                    for (Participant participant : participants) {
                        Vote vote = voteDAO.getPlayerVote(participant.playerId, plugin.currentSeasonNum).join();
                        if (vote == null || vote.dateLastVoted == null) {
                            participantsToNotify.add(participant);
                        }
                    }

                    for (Participant participant : participantsToNotify) {
                        Collection<? extends Player> players = plugin.getServer().getOnlinePlayers();
                        for (Player player : players) {
                            if (player.getUniqueId().equals(participant.playerId)) {
                                player.sendMessage(plugin.configManager.localization.getLocalized(REQUESTING_VOTE_TOP));
                                player.sendMessage(plugin.configManager.localization.getLocalized(REQUESTING_VOTE_BOTTOM));
                            }
                        }
                    }

                    if (shouldVotesEndSeason(votes)) {
                        endSeason(participants);
                    }
                }
            }
        }, minutesToTicks(1), minutesToTicks(plugin.configManager.config.notificationInterval));
    }

    private boolean shouldVotesEndSeason(List<Vote> votes) {
        if (votes.isEmpty()) {
            return false; // If there are no votes, we can't end the season.
        }

        int voteCount = votes.size();
        int voteReqPercent = plugin.configManager.config.minVotesToEndSeason;
        long voteEndCount = votes.stream().filter(vote -> vote.shouldEndSeason).count(); // Use count() directly for efficiency.

        // Calculate the percentage of votes for ending the season.
        int voteEndPercent = (int) ((voteEndCount * 100) / voteCount);

        return voteEndPercent >= voteReqPercent;
    }


    private boolean shouldResetVote(Vote vote) {
        if(vote.dateLastVoted == null || vote.shouldEndSeason) {
            return false;
        }

        Timestamp now = new Timestamp(System.currentTimeMillis());
        long voteResetIntervalMillis = plugin.configManager.config.voteResetInterval * 86400000L; // 24 * 60 * 60 * 1000
        Timestamp threshold = new Timestamp(now.getTime() - voteResetIntervalMillis);
        Bukkit.getLogger().info("Threshold: " + threshold);
        Bukkit.getLogger().info("DateLastVoted: " + vote.dateLastVoted);
        return vote.dateLastVoted.before(threshold);
    }

    private void scheduleSeasonEndTracker() {
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {

                    SeasonDAO seasonDAO = new SeasonDAOImpl(plugin.database);
                    PlayerDAO playerDAO = new PlayerDAOImpl(plugin.database);

                    Season activeSeason = seasonDAO.get(plugin.currentSeasonNum).join();

                    if (activeSeason == null) {
                        activeSeason = initializeFirstSeason(seasonDAO);
                    }

                    List<Participant> participants = playerDAO.getAllForSeason(plugin.currentSeasonNum).join();

                    if (participants == null) { return; }

                    List<Participant> activePlayers = getActivePlayers(participants);
                    Timestamp currentDate = new Timestamp(System.currentTimeMillis());

                    if (plugin.configManager.config.maxSeasonLength != -1) {
                        if (activeSeason.hardEndDate.before(currentDate)) {
                            endSeason(activePlayers);
                        }
                    }

                    if (activeSeason.softEndDate.before(currentDate)) {
                        if (activePlayers.isEmpty()) {
                            endSeason(activePlayers);
                        }
                    }

                }, 60, // Delay 3 seconds
                minutesToTicks(plugin.configManager.config.mySQLConfig.updateInterval));
    }

    @NotNull
    private Season initializeFirstSeason(SeasonDAO seasonDAO) {
        Season activeSeason;
        int minimumLength = plugin.configManager.config.minSeasonLength;
        int maximumLength = plugin.configManager.config.minSeasonLength;

        if (minimumLength < 1) {
            minimumLength = 1;
        }

        if (minimumLength > maximumLength) {
            maximumLength = minimumLength;
        }

        Timestamp startDate = new Timestamp(System.currentTimeMillis());
        Timestamp softEndDate = Timestamp.valueOf(LocalDateTime.now().plusDays(minimumLength));
        Timestamp hardEndDate = Timestamp.valueOf(LocalDateTime.now().plusDays(maximumLength));

        activeSeason = new Season(plugin.currentSeasonNum + 1, plugin.currentSeasonNum + 1, startDate, softEndDate, hardEndDate);
        plugin.currentSeasonNum = activeSeason.seasonId;
        seasonDAO.insert(activeSeason).join();
        return activeSeason;
    }

    /**
     * Players must have been online from the current date minus the lastLoginThreshold
     *
     * @param participants
     * @return List<Participant> activePlayers
     */
    private List<Participant> getActivePlayers(List<Participant> participants) {
        int lastLoginThreshold = plugin.configManager.config.lastLoginThreshold;
        Timestamp lastLoginThresholdDate = new Timestamp(System.currentTimeMillis() - ((long) lastLoginThreshold * 86400000L));
        participants.removeIf(player -> player.lastOnline.before(lastLoginThresholdDate));
        return participants;
    }

    /**
     * Ends the season, generates seasonal rewards, splitting as needed.
     * Destroys the world, and generates a new world.
     */
    private void endSeason(List<Participant> winners) {
        kickPlayersPreventRejoin();

        // Generate Rewards
        plugin.rewardManager.saveRewards(winners);

        // Generate New Season
        SeasonDAO seasonDAO = new SeasonDAOImpl(plugin.database);

        Timestamp seasonStartDate = new Timestamp(System.currentTimeMillis());
        Timestamp seasonSoftEndDate = new Timestamp(System.currentTimeMillis() + plugin.configManager.config.minSeasonLength * 86400000L);
        Timestamp seasonHardEndDate = null;

        if (plugin.configManager.config.maxSeasonLength != -1) {
            seasonHardEndDate = new Timestamp(System.currentTimeMillis() + plugin.configManager.config.maxSeasonLength * 86400000L);
        }

        Season newSeason = new Season(
                plugin.currentSeasonNum + 1,
                plugin.currentSeasonNum + 1,
                seasonStartDate,
                seasonSoftEndDate,
                seasonHardEndDate
        );

        seasonDAO.insert(newSeason).join();
        plugin.currentSeasonNum = newSeason.id;

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
