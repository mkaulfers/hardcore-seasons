package usa.mkaulfers.hardcoreseasons.models;

import java.util.List;

public class Config {
    boolean seasonalServer;
    int minSeasonLength;
    int maxSeasonLength;
    int maxSurvivorsRemaining;
    int lastLoginThreshold;
    int confirmationIntervalDays;
    int confirmationIntervalHours;
    int confirmationIntervalMinutes;
    List<String> endOfSeasonCommands;
    public String storageType;
    public MySQLConfig mySQLConfig;

    public Config(boolean seasonalServer,
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
        this.seasonalServer = seasonalServer;
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
