package com.xtwistedx;

import com.xtwistedx.commands.HardcoreSeasonsCommand;
import com.xtwistedx.listeners.TrackableBlockPlaced;
import com.xtwistedx.models.Config;
import com.xtwistedx.models.MySQLConfig;
import com.xtwistedx.storage.SQLHandler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class HardcoreSeasons extends JavaPlugin {
    private Config config;
    private SQLHandler sqlHandler;

    // Lifecycle methods
    @Override
    public void onEnable() {
        loadConfigs();
        registerCommands();
        registerListeners();
        handleStorage();
    }

    @Override
    public void onDisable() {
        sqlHandler.disconnect();
    }

    // Custom methods
    private void loadConfigs() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        FileConfiguration rawConfig = getConfig();
        int minSeasonLength = rawConfig.getInt("minSeasonLength");
        int maxSeasonLength = rawConfig.getInt("maxSeasonLength");
        int maxSurvivorsRemaining = rawConfig.getInt("maxSurvivorsRemaining");
        int lastLoginThreshold = rawConfig.getInt("lastLoginThreshold");
        int confirmationIntervalDays = rawConfig.getInt("confirmationIntervalDays");
        int confirmationIntervalHours = rawConfig.getInt("confirmationIntervalHours");
        int confirmationIntervalMinutes = rawConfig.getInt("confirmationIntervalMinutes");
        List<String> endOfSeasonCommands = rawConfig.getStringList("endOfSeasonCommands");
        String storageType = rawConfig.getString("storageType");

        String host = rawConfig.getString("mySQLConfig.host");
        int port = rawConfig.getInt("mySQLConfig.port");
        String database = rawConfig.getString("mySQLConfig.database");
        String username = rawConfig.getString("mySQLConfig.username");
        String password = rawConfig.getString("mySQLConfig.password");
        int updateInterval = rawConfig.getInt("mySQLConfig.updateInterval");

        MySQLConfig mySQLConfig = new MySQLConfig(host,
                port,
                database,
                username,
                password,
                updateInterval);

        config = new Config(minSeasonLength,
                maxSeasonLength,
                maxSurvivorsRemaining,
                lastLoginThreshold,
                confirmationIntervalDays,
                confirmationIntervalHours,
                confirmationIntervalMinutes,
                endOfSeasonCommands,
                storageType,
                mySQLConfig);
    }

    private void registerCommands() {
        this.getCommand("hardcoreseasons").setExecutor(new HardcoreSeasonsCommand(this));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new TrackableBlockPlaced(), this);
    }

    private void handleStorage() {
        if(config.storageType.equalsIgnoreCase("mysql")) {
            sqlHandler = new SQLHandler(config);
            try {
                sqlHandler.connect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            /// Use SQLite
        }
    }
}
