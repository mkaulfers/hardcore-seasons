package com.xtwistedx;

import com.xtwistedx.commands.HardcoreSeasonsCommand;
import com.xtwistedx.listeners.TrackableBlockPlaced;
import com.xtwistedx.models.HCConfig;
import com.xtwistedx.models.MySQLConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class HardcoreSeasons extends JavaPlugin {
    HCConfig hcConfig;

    // Lifecycle methods
    @Override
    public void onEnable() {
        loadConfigs();
        registerCommands();
        registerListeners();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
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

        hcConfig = new HCConfig(minSeasonLength,
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

    public void registerCommands() {
        this.getCommand("hardcoreseasons").setExecutor(new HardcoreSeasonsCommand(this));
    }

    public void registerListeners() {
        getServer().getPluginManager().registerEvents(new TrackableBlockPlaced(), this);
    }
}
