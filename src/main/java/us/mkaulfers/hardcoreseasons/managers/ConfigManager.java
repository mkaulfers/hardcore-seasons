package us.mkaulfers.hardcoreseasons.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.models.Localization;
import us.mkaulfers.hardcoreseasons.models.MySQLConfig;
import us.mkaulfers.hardcoreseasons.models.PluginConfig;

import java.io.File;
import java.util.List;

public class ConfigManager {
    HardcoreSeasons plugin;

    public PluginConfig config;
    public Localization localization;

    public ConfigManager(HardcoreSeasons plugin) {
        this.plugin = plugin;
        loadConfig(plugin);
        loadLocalization(plugin);
    }

    public void loadConfig(HardcoreSeasons plugin) {
        plugin.getConfig().options().copyDefaults();
        plugin.saveDefaultConfig();

        FileConfiguration rawConfig = plugin.getConfig();
        boolean claimingEnabled = rawConfig.getBoolean("claimingEnabled");
        boolean trackingEnabled = rawConfig.getBoolean("trackingEnabled");
        boolean persistSeasonWorlds = rawConfig.getBoolean("persistSeasonWorlds");
        boolean unloadPastSeasons = rawConfig.getBoolean("unloadPastSeasons");
        int minSeasonLength = rawConfig.getInt("minSeasonLength");
        int maxSeasonLength = rawConfig.getInt("maxSeasonLength");
        int maxSurvivorsRemaining = rawConfig.getInt("maxSurvivorsRemaining");
        int lastLoginThreshold = rawConfig.getInt("lastLoginThreshold");
        int notificationInterval = rawConfig.getInt("notificationInterval");
        int voteResetInterval = rawConfig.getInt("voteResetInterval");
        List<String> endOfSeasonCommands = rawConfig.getStringList("endOfSeasonCommands");
        String storageType = rawConfig.getString("storageType");

        String host = rawConfig.getString("MySQL.host");
        int port = rawConfig.getInt("MySQL.port");
        String database = rawConfig.getString("MySQL.database");
        String username = rawConfig.getString("MySQL.username");
        String password = rawConfig.getString("MySQL.password");
        int updateInterval = rawConfig.getInt("MySQL.updateInterval");

        MySQLConfig mySQLConfig = new MySQLConfig(
                host,
                port,
                database,
                username,
                password,
                updateInterval
        );

        config = new PluginConfig(
                claimingEnabled,
                trackingEnabled,
                persistSeasonWorlds,
                unloadPastSeasons,
                minSeasonLength,
                maxSeasonLength,
                maxSurvivorsRemaining,
                lastLoginThreshold,
                notificationInterval,
                voteResetInterval,
                endOfSeasonCommands,
                storageType,
                mySQLConfig
        );
    }

    // Load the localization.yml from the same directory as the config.yml
    private void loadLocalization(HardcoreSeasons plugin) {
        File localizationFile = new File(plugin.getDataFolder(), "localization.yml");
        plugin.saveResource("localization.yml", false);
        FileConfiguration rawLocalization = YamlConfiguration.loadConfiguration(localizationFile);

        // System Messages
        String configReloaded = rawLocalization.getString("configReloaded");
        String mustBeAPlayer = rawLocalization.getString("mustBeAPlayer");
        String noPermission = rawLocalization.getString("noPermission");
        String invalidCommand = rawLocalization.getString("invalidCommand");

        // Redeeming Rewards GUI
        String loadingRewards = rawLocalization.getString("loadingRewards");
        String rewardGoBack = rawLocalization.getString("rewardGoBack");
        String rewardPrevious = rawLocalization.getString("rewardPrevious");
        String rewardPage = rawLocalization.getString("rewardPage");
        String rewardNext = rawLocalization.getString("rewardNext");
        String rewardClose = rawLocalization.getString("rewardClose");
        String rewardPageCounter = rawLocalization.getString("rewardPageCounter");
        String inventoryFull = rawLocalization.getString("inventoryFull");

        // Selecting Season GUI
        String loadingSeasons = rawLocalization.getString("loadingSeasons");
        String selectSeasonTitle = rawLocalization.getString("selectSeasonTitle");
        String seasonItemName = rawLocalization.getString("seasonItemName");
        String seasonPrevious = rawLocalization.getString("seasonPrevious");
        String seasonNext = rawLocalization.getString("seasonNext");
        String seasonClose = rawLocalization.getString("seasonClose");
        String seasonPageCounter = rawLocalization.getString("seasonPageCounter");

        // Player Join Messages
        String haveDied = rawLocalization.getString("haveDied");
        String requestingVoteTop = rawLocalization.getString("requestingVoteTop");
        String requestingVoteBottom = rawLocalization.getString("requestingVoteBottom");

        // Player Death Messages
        String deathMessage = rawLocalization.getString("deathMessage");

        localization = new Localization(
                configReloaded,
                mustBeAPlayer,
                noPermission,
                invalidCommand,
                loadingRewards,
                rewardGoBack,
                rewardPrevious,
                rewardPage,
                rewardNext,
                rewardClose,
                rewardPageCounter,
                inventoryFull,
                loadingSeasons,
                selectSeasonTitle,
                seasonItemName,
                seasonPrevious,
                seasonNext,
                seasonClose,
                seasonPageCounter,
                haveDied,
                requestingVoteTop,
                requestingVoteBottom,
                deathMessage
        );
    }
}
