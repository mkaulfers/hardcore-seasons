package us.mkaulfers.hardcoreseasons.models;

import java.util.List;

public class PluginConfig {
    public String configVersion;
    public boolean claimingEnabled;
    public boolean trackingEnabled;
    public boolean persistSeasonWorlds;
    public boolean unloadPastSeasons;
    public int minSeasonLength;
    public int maxSeasonLength;
    public int maxSurvivorsRemaining;
    public int minVotesToEndSeason;
    public int lastLoginThreshold;
    public int notificationInterval;
    public int voteResetInterval;
    public List<String> endOfSeasonCommands;
    public String storageType;
    public MySQLConfig mySQLConfig;

    public PluginConfig(
            String configVersion,
            boolean claimingEnabled,
            boolean trackingEnabled,
            boolean persistSeasonWorlds,
            boolean unloadPastSeasons,
            int minSeasonLength,
            int maxSeasonLength,
            int maxSurvivorsRemaining,
            int minVotesToEndSeason,
            int lastLoginThreshold,
            int notificationInterval,
            int voteResetInterval,
            List<String> endOfSeasonCommands,
            String storageType,
            MySQLConfig mySQLConfig
    ) {
        this.configVersion = configVersion;
        this.claimingEnabled = claimingEnabled;
        this.trackingEnabled = trackingEnabled;
        this.persistSeasonWorlds = persistSeasonWorlds;
        this.unloadPastSeasons = unloadPastSeasons;
        this.minSeasonLength = minSeasonLength;
        this.maxSeasonLength = maxSeasonLength;
        this.maxSurvivorsRemaining = maxSurvivorsRemaining;
        this.minVotesToEndSeason = minVotesToEndSeason;
        this.lastLoginThreshold = lastLoginThreshold;
        this.notificationInterval = notificationInterval;
        this.voteResetInterval = voteResetInterval;
        this.endOfSeasonCommands = endOfSeasonCommands;
        this.storageType = storageType;
        this.mySQLConfig = mySQLConfig;
    }
}
