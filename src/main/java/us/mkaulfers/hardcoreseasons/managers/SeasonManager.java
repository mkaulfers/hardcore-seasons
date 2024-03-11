package us.mkaulfers.hardcoreseasons.managers;

import org.bukkit.World;
import org.bukkit.entity.Player;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.interfaceimpl.PlayerDAOImpl;
import us.mkaulfers.hardcoreseasons.interfaceimpl.SeasonDAOImpl;
import us.mkaulfers.hardcoreseasons.interfaces.PlayerDAO;
import us.mkaulfers.hardcoreseasons.interfaces.SeasonDAO;
import us.mkaulfers.hardcoreseasons.models.Participant;
import us.mkaulfers.hardcoreseasons.models.Season;

import java.io.File;
import java.sql.Date;
import java.util.Collection;
import java.util.List;

public class SeasonManager {
    HardcoreSeasons plugin;

    public SeasonManager(HardcoreSeasons plugin) {
        this.plugin = plugin;
        scheduleSeasonEndTracker();
    }

    private void scheduleSeasonEndTracker() {
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {

                    SeasonDAO seasonDAO = new SeasonDAOImpl(plugin.database);
                    PlayerDAO playerDAO = new PlayerDAOImpl(plugin.database);

                    Season activeSeason = seasonDAO.get(plugin.currentSeasonNum).join();
                    List<Participant> participants = playerDAO.getAllForSeason(plugin.currentSeasonNum).join();
                    List<Participant> activePlayers = getActivePlayers(participants);
                    Date currentDate = new Date(System.currentTimeMillis());

                    if (plugin.configManager.config.maxSeasonLength != -1) {
                        if (activeSeason.hardEndDate.before(currentDate)) {
                            endSeason(activePlayers);
                        }
                    }

                    if (activeSeason.softEndDate.before(currentDate)) {
                        if (!activePlayers.isEmpty()) {
                            endSeason(activePlayers);
                        }

                        if (activePlayers.size() <= plugin.configManager.config.maxSurvivorsRemaining) {
                            plugin.shouldRequestSeasonEnd = true;
                        }
                    }


                }, plugin.configManager.config.mySQLConfig.updateInterval * 20L * 60L, // Delay config * 20 ticks * 60 seconds = minutes
                plugin.configManager.config.mySQLConfig.updateInterval * 20L * 60L); // Period config * 20 ticks * 60 seconds = minutes
    }

    /**
     * Players must have been online from the current date minus the lastLoginThreshold
     *
     * @param participants
     * @return List<Participant> activePlayers
     */
    private List<Participant> getActivePlayers(List<Participant> participants) {
        int lastLoginThreshold = plugin.configManager.config.lastLoginThreshold;
        Date lastLoginThresholdDate = new Date(System.currentTimeMillis() - ((long) lastLoginThreshold * 24 * 60 * 60 * 1000));
        participants.removeIf(player -> player.lastOnline.before(lastLoginThresholdDate));
        return participants;
    }

    /**
     * Ends the season, generates seasonal rewards, splitting as needed.
     * Destroys the world, and generates a new world.
     */
    private void endSeason(List<Participant> winners) {
        // Generate Rewards
        plugin.rewardManager.saveRewards(winners);

        // Generate New Season
        SeasonDAO seasonDAO = new SeasonDAOImpl(plugin.database);

        Date seasonStartDate = new Date(System.currentTimeMillis());
        Date seasonSoftEndDate = new Date(System.currentTimeMillis() + (long) plugin.configManager.config.minSeasonLength * 24 * 60 * 60 * 1000);
        Date seasonHardEndDate = null;

        if (plugin.configManager.config.maxSeasonLength != -1) {
            seasonHardEndDate = new Date(System.currentTimeMillis() + (long) plugin.configManager.config.maxSeasonLength * 24 * 60 * 60 * 1000);
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
        clearOfflinePlayerData();
        clearOnlinePlayerData();
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
}
