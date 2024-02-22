package com.xtwistedx.models;

public class HCConfig {
    int minSeasonLength;
    int maxSeasonLength;
    int maxSurvivorsRemaining;
    int lastLoginThreshold;
    String[] endOfSeasonCommands;
    MySQLConfig mySQLConfig;

    public HCConfig(int minSeasonLength, int maxSeasonLength, int maxSurvivorsRemaining, int lastLoginThreshold, String[] endOfSeasonCommands, MySQLConfig mySQLConfig) {
        this.minSeasonLength = minSeasonLength;
        this.maxSeasonLength = maxSeasonLength;
        this.maxSurvivorsRemaining = maxSurvivorsRemaining;
        this.lastLoginThreshold = lastLoginThreshold;
        this.endOfSeasonCommands = endOfSeasonCommands;
        this.mySQLConfig = mySQLConfig;
    }
}
