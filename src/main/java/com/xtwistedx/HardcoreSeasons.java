package com.xtwistedx;

import com.xtwistedx.models.HCConfig;
import com.xtwistedx.models.MySQLConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class HardcoreSeasons extends JavaPlugin {
    HCConfig hcConfig;

    // Lifecycle methods
    @Override
    public void onEnable() {
        loadConfigs();
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
        String[] endOfSeasonCommands = rawConfig.getStringList("endOfSeasonCommands").toArray(new String[0]);
        String host = rawConfig.getString("mySQLConfig.host");
        int port = rawConfig.getInt("mySQLConfig.port");
        String database = rawConfig.getString("mySQLConfig.database");
        String username = rawConfig.getString("mySQLConfig.username");
        String password = rawConfig.getString("mySQLConfig.password");

        MySQLConfig mySQLConfig = new MySQLConfig(host,
                port,
                database,
                username,
                password);

        hcConfig = new HCConfig(minSeasonLength,
                maxSeasonLength,
                maxSurvivorsRemaining,
                lastLoginThreshold,
                endOfSeasonCommands,
                mySQLConfig);
    }
}
