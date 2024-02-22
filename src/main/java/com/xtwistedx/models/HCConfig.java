package com.xtwistedx.models;

import java.util.List;

public class HCConfig {
    int minSeasonLength;
    int maxSeasonLength;
    int maxSurvivorsRemaining;
    int lastLoginThreshold;
    int confirmationIntervalDays;
    int confirmationIntervalHours;
    int confirmationIntervalMinutes;
    List<String> endOfSeasonCommands;
    String storageType;
    MySQLConfig mySQLConfig;

    public HCConfig(int minSeasonLength,
                    int maxSeasonLength,
                    int maxSurvivorsRemaining,
                    int lastLoginThreshold,
                    int confirmationIntervalDays,
                    int confirmationIntervalHours,
                    int confirmationIntervalMinutes,
                    List<String> endOfSeasonCommands,
                    String storageType,
                    MySQLConfig mySQLConfig) {
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
