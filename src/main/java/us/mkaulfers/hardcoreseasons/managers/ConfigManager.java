package us.mkaulfers.hardcoreseasons.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.models.MySQLConfig;
import us.mkaulfers.hardcoreseasons.models.PluginConfig;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ConfigManager {
    HardcoreSeasons plugin;

    public PluginConfig config;

    public ConfigManager(HardcoreSeasons plugin) {
        this.plugin = plugin;
        loadConfig(plugin);
    }

    // TODO: Fix this so it doesn't break the plugin
    public void updateConfig(HardcoreSeasons plugin) {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveDefaultConfig();
        } else {
            try {
                // Load the existing configuration
                FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

                // Save the default configuration to a temporary file
                File tempFile = new File(plugin.getDataFolder(), "temp.yml");
                if (tempFile.exists()) {
                    tempFile.delete();
                }
                plugin.saveResource("config.yml", true);
                FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(tempFile);

                // Merge the existing configuration with the defaults
                for (String key : defaultConfig.getKeys(true)) {
                    if (!config.contains(key)) {
                        config.set(key, defaultConfig.get(key));
                    }
                }

                // Save the updated configuration
                config.save(configFile);

                // Cleanup the temporary file
                tempFile.delete();

            } catch (IOException e) {
                plugin.getLogger().severe("[Hardcore Seasons]: Failed to update the configuration file.");
            }
        }

        loadConfig(plugin);
    }

    public void loadConfig(HardcoreSeasons plugin) {
        plugin.getConfig().options().copyDefaults();
        plugin.saveDefaultConfig();

        FileConfiguration rawConfig = plugin.getConfig();
        boolean seasonalServer = rawConfig.getBoolean("seasonalServer");
        int minSeasonLength = rawConfig.getInt("minSeasonLength");
        int maxSeasonLength = rawConfig.getInt("maxSeasonLength");
        int maxSurvivorsRemaining = rawConfig.getInt("maxSurvivorsRemaining");
        int lastLoginThreshold = rawConfig.getInt("lastLoginThreshold");
        int confirmationIntervalDays = rawConfig.getInt("confirmationIntervalDays");
        int confirmationIntervalHours = rawConfig.getInt("confirmationIntervalHours");
        int confirmationIntervalMinutes = rawConfig.getInt("confirmationIntervalMinutes");
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
                seasonalServer,
                minSeasonLength,
                maxSeasonLength,
                maxSurvivorsRemaining,
                lastLoginThreshold,
                confirmationIntervalDays,
                confirmationIntervalHours,
                confirmationIntervalMinutes,
                endOfSeasonCommands,
                storageType,
                mySQLConfig
        );
    }
}
