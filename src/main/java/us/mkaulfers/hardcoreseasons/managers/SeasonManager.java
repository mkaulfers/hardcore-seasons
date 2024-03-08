package us.mkaulfers.hardcoreseasons.managers;

import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.interfaceimpl.PlayerDAOImpl;
import us.mkaulfers.hardcoreseasons.interfaceimpl.SeasonDAOImpl;
import us.mkaulfers.hardcoreseasons.interfaces.PlayerDAO;
import us.mkaulfers.hardcoreseasons.interfaces.SeasonDAO;
import us.mkaulfers.hardcoreseasons.models.Participant;
import us.mkaulfers.hardcoreseasons.models.Season;

import java.sql.Date;
import java.util.List;

public class SeasonManager {
    HardcoreSeasons plugin;

    public SeasonManager(HardcoreSeasons plugin) {
        this.plugin = plugin;
        scheduleSeasonEndTracker();
    }

    private void scheduleSeasonEndTracker() {
        // If the season has reached the hard-end date end the season
        // - If there are more players than `maxSurvivorsRemaining` then generate rewards for the top players
        // - Top players are those who joined earlier and have been online more recently than the lastLoginThreshold
        // - Should compare timespan from joinDate to lastOnline largest to smallest, then trim to `maxSurvivorsRemaining`

        // If the season has reached the soft-end date, request a vote to end the season only
        // - If the maxSurvivorsRemaining is equal to active players.
        // - If the vote is successful, end the season
        // - Otherwise continue the season.

        // If no players are active, or no players are alive, end the season
        // - Only end the season if the minimum season length has been reached
        // - Also only end if there is at least one player who has joined the server


        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {

                    SeasonDAO seasonDAO = new SeasonDAOImpl(plugin.database);
                    PlayerDAO playerDAO = new PlayerDAOImpl(plugin.database);

                    Season activeSeason = seasonDAO.get(plugin.currentSeasonNum).join();
                    List<Participant> participants = playerDAO.getAllForSeason(plugin.currentSeasonNum).join();
                    List<Participant> activePlayers = getActivePlayers(participants);
                    Date currentDate = new Date(System.currentTimeMillis());

                    if (plugin.pluginConfig.maxSeasonLength != -1) {
                        if (activeSeason.hardEndDate.before(currentDate)) {
                            endSeason(activePlayers);
                        }
                    }

                    if (activeSeason.softEndDate.before(currentDate)) {
                        if (!activePlayers.isEmpty()) {
                            endSeason(activePlayers);
                        }

                        if (activePlayers.size() <= plugin.pluginConfig.maxSurvivorsRemaining) {
                            plugin.shouldRequestSeasonEnd = true;
                        }
                    }


                }, plugin.pluginConfig.mySQLConfig.updateInterval * 20L * 60L, // Delay config * 20 ticks * 60 seconds = minutes
                plugin.pluginConfig.mySQLConfig.updateInterval * 20L * 60L); // Period config * 20 ticks * 60 seconds = minutes
    }

    /**
     * Players must have been online from the current date minus the lastLoginThreshold
     *
     * @param participants
     * @return List<Participant> activePlayers
     */
    private List<Participant> getActivePlayers(List<Participant> participants) {
        int lastLoginThreshold = plugin.pluginConfig.lastLoginThreshold;
        Date lastLoginThresholdDate = new Date(System.currentTimeMillis() - ((long) lastLoginThreshold * 24 * 60 * 60 * 1000));
        participants.removeIf(player -> player.lastOnline.before(lastLoginThresholdDate));
        return participants;
    }

    /**
     * Ends the season, generates seasonal rewards, splitting as needed.
     * Destroys the world, and generates a new world.
     */
    private void endSeason(List<Participant> winners) {
        // TODO: Handle no winners case.

        // Generate Rewards
        plugin.rewardManager.saveRewards(winners);

        // Generate New Season
        SeasonDAO seasonDAO = new SeasonDAOImpl(plugin.database);

        Date seasonStartDate = new Date(System.currentTimeMillis());
        Date seasonSoftEndDate = new Date(System.currentTimeMillis() + (long) plugin.pluginConfig.minSeasonLength * 24 * 60 * 60 * 1000);
        Date seasonHardEndDate = null;

        if (plugin.pluginConfig.maxSeasonLength != -1) {
            seasonHardEndDate = new Date(System.currentTimeMillis() + (long) plugin.pluginConfig.maxSeasonLength * 24 * 60 * 60 * 1000);
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
        plugin.shouldRequestSeasonEnd = false;

        // Generate New Worlds
        plugin.worldManager = new WorldManager(plugin);
    }
}
