package us.mkaulfers.hardcoreseasons.models;

import java.util.List;

public class PluginConfig {
    public boolean claimingEnabled;
    public boolean trackingEnabled;
    public boolean persistSeasonWorlds;
    public int minSeasonLength;
    public int maxSeasonLength;
    public int maxSurvivorsRemaining;
    public int lastLoginThreshold;
    public int confirmationIntervalDays;
    public int confirmationIntervalHours;
    public int confirmationIntervalMinutes;
    List<String> endOfSeasonCommands;
    public String storageType;
    public MySQLConfig mySQLConfig;

    public PluginConfig(boolean claimingEnabled,
                        boolean trackingEnabled,
                        boolean persistSeasonWorlds,
                        int minSeasonLength,
                        int maxSeasonLength,
                        int maxSurvivorsRemaining,
                        int lastLoginThreshold,
                        int confirmationIntervalDays,
                        int confirmationIntervalHours,
                        int confirmationIntervalMinutes,
                        List<String> endOfSeasonCommands,
                        String storageType,
                        MySQLConfig mySQLConfig) {
        this.claimingEnabled = claimingEnabled;
        this.trackingEnabled = trackingEnabled;
        this.persistSeasonWorlds = persistSeasonWorlds;
        this.minSeasonLength = minSeasonLength;
        this.maxSeasonLength = maxSeasonLength;
        this.maxSurvivorsRemaining = maxSurvivorsRemaining;
        this.lastLoginThreshold = lastLoginThreshold;
        this.confirmationIntervalDays = confirmationIntervalDays;
        this.confirmationIntervalHours = confirmationIntervalHours;
        this.confirmationIntervalMinutes = confirmationIntervalMinutes;
        this.endOfSeasonCommands = endOfSeasonCommands;
        this.storageType = storageType;
        this.mySQLConfig = mySQLConfig;
    }
}
