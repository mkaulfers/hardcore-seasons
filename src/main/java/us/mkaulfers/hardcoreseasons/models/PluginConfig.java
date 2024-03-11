package us.mkaulfers.hardcoreseasons.models;

import java.util.List;

public class PluginConfig {
    public boolean claimingEnabled;
    public boolean trackingEnabled;
    public boolean persistSeasonWorlds;
    public boolean unloadPastSeasons;
    public int minSeasonLength;
    public int maxSeasonLength;
    public int maxSurvivorsRemaining;
    public int lastLoginThreshold;
    public int notificationInterval;
    public int voteResetInterval;
    List<String> endOfSeasonCommands;
    public String storageType;
    public MySQLConfig mySQLConfig;

    public PluginConfig(boolean claimingEnabled,
                        boolean trackingEnabled,
                        boolean persistSeasonWorlds,
                        boolean unloadPastSeasons,
                        int minSeasonLength,
                        int maxSeasonLength,
                        int maxSurvivorsRemaining,
                        int lastLoginThreshold,
                        int notificationInterval,
                        int voteResetInterval,
                        List<String> endOfSeasonCommands,
                        String storageType,
                        MySQLConfig mySQLConfig) {
        this.claimingEnabled = claimingEnabled;
        this.trackingEnabled = trackingEnabled;
        this.persistSeasonWorlds = persistSeasonWorlds;
        this.unloadPastSeasons = unloadPastSeasons;
        this.minSeasonLength = minSeasonLength;
        this.maxSeasonLength = maxSeasonLength;
        this.maxSurvivorsRemaining = maxSurvivorsRemaining;
        this.lastLoginThreshold = lastLoginThreshold;
        this.notificationInterval = notificationInterval;
        this.voteResetInterval = voteResetInterval;
        this.endOfSeasonCommands = endOfSeasonCommands;
        this.storageType = storageType;
        this.mySQLConfig = mySQLConfig;
    }
}
