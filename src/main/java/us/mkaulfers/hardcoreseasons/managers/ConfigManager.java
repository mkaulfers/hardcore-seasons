package us.mkaulfers.hardcoreseasons.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.models.Localization;
import us.mkaulfers.hardcoreseasons.models.MySQLConfig;
import us.mkaulfers.hardcoreseasons.models.PluginConfig;

import java.io.*;
import java.util.function.BiConsumer;

public class ConfigManager {
    HardcoreSeasons plugin;

    public PluginConfig config;
    public Localization localization;

    public ConfigManager(HardcoreSeasons plugin) {
        this.plugin = plugin;
        loadConfig(plugin);
        loadLocalization(plugin);
    }

    public void loadConfig(HardcoreSeasons plugin) {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        plugin.saveResource("config.yml", false);
        config = constructPluginConfig(YamlConfiguration.loadConfiguration(configFile));

        updateConfigurationIfNeeded("config.yml", (config, p) -> {
            this.config = constructPluginConfig(config);
        });
    }

    private void loadLocalization(HardcoreSeasons plugin) {
        File localizationFile = new File(plugin.getDataFolder(), "localization.yml");
        plugin.saveResource("localization.yml", false);
        localization = constructLocalization(YamlConfiguration.loadConfiguration(localizationFile));

        updateConfigurationIfNeeded("localization.yml", (config, p) -> {
            this.localization = constructLocalization(config);
        });
    }

    private PluginConfig constructPluginConfig(FileConfiguration rawConfig) {
        return new PluginConfig(
                rawConfig.getString("configVersion"),
                rawConfig.getString("loggingLevel"),
                rawConfig.getBoolean("claimingEnabled"),
                rawConfig.getBoolean("trackingEnabled"),
                rawConfig.getBoolean("persistSeasonWorlds"),
                rawConfig.getBoolean("unloadPastSeasons"),
                rawConfig.getInt("minSeasonLength"),
                rawConfig.getInt("maxSeasonLength"),
                rawConfig.getInt("maxSurvivorsRemaining"),
                rawConfig.getInt("minVotesToEndSeason"),
                rawConfig.getInt("lastLoginThreshold"),
                rawConfig.getInt("notificationInterval"),
                rawConfig.getInt("voteResetInterval"),
                rawConfig.getStringList("endOfSeasonCommands"),
                rawConfig.getString("storageType"),
                new MySQLConfig(
                        rawConfig.getString("MySQL.host"),
                        rawConfig.getInt("MySQL.port"),
                        rawConfig.getString("MySQL.database"),
                        rawConfig.getString("MySQL.username"),
                        rawConfig.getString("MySQL.password"),
                        rawConfig.getInt("MySQL.updateInterval")
                )
        );
    }

    private Localization constructLocalization(FileConfiguration rawConfig) {
        return new Localization(
                plugin,
                rawConfig.getString("configVersion"),
                rawConfig.getString("configReloaded"),
                rawConfig.getString("mustBeAPlayer"),
                rawConfig.getString("noPermission"),
                rawConfig.getString("invalidCommand"),
                rawConfig.getString("loadingRewards"),
                rawConfig.getString("rewardGoBack"),
                rawConfig.getString("rewardPrevious"),
                rawConfig.getString("rewardPage"),
                rawConfig.getString("rewardNext"),
                rawConfig.getString("rewardClose"),
                rawConfig.getString("rewardPageCounter"),
                rawConfig.getString("inventoryFull"),
                rawConfig.getString("loadingSeasons"),
                rawConfig.getString("selectSeasonTitle"),
                rawConfig.getString("seasonItemName"),
                rawConfig.getString("seasonPrevious"),
                rawConfig.getString("seasonNext"),
                rawConfig.getString("seasonClose"),
                rawConfig.getString("seasonPageCounter"),
                rawConfig.getString("haveDied"),
                rawConfig.getString("cannotVote"),
                rawConfig.getString("requestingVoteTop"),
                rawConfig.getString("requestingVoteBottom"),
                rawConfig.getString("voteContinueSuccess"),
                rawConfig.getString("voteEndSuccess"),
                rawConfig.getString("voteFail"),
                rawConfig.getString("deathMessage"),
                rawConfig.getString("seasonEnding"),
                rawConfig.getString("seasonGenerating"),
                rawConfig.getString("playerResurrected"),
                rawConfig.getStringList("seasonInfo")
        );
    }

    private void updateConfigurationIfNeeded(String resourceName, BiConsumer<FileConfiguration, HardcoreSeasons> configLoader) {
        File file = new File(plugin.getDataFolder(), resourceName);
        File backupFile = new File(plugin.getDataFolder(), resourceName + "-backup.yml");
        if (!file.exists()) {
            plugin.saveResource(resourceName, false);
        }
        FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource(resourceName)));
        FileConfiguration userConfig = YamlConfiguration.loadConfiguration(file);

        String defaultVersion = defaultConfig.getString("configVersion");
        String userVersion = userConfig.getString("configVersion");

        if (defaultVersion == null || !defaultVersion.equals(userVersion)) {
            file.renameTo(backupFile);
            plugin.saveResource(resourceName, false);

            FileConfiguration newUserConfig = YamlConfiguration.loadConfiguration(file);
            mergeConfigurations(userConfig, newUserConfig);

            try {
                newUserConfig.save(file);
                backupFile.delete();
            } catch (IOException e) {
                plugin.getLogger().severe("[Hardcore Seasons]: Failed to update configuration file: " + file.getName());
                plugin.getLogger().severe("[Hardcore Seasons]: Rolling back to backup file: " + backupFile.getName());
                backupFile.renameTo(file);
            }
        }

        configLoader.accept(YamlConfiguration.loadConfiguration(file), plugin);
    }

    private void mergeConfigurations(FileConfiguration oldConfig, FileConfiguration newConfig) {
        newConfig.set("configVersion", newConfig.getString("configVersion"));

        for (String key : oldConfig.getKeys(true)) {
            if (!"configVersion".equals(key) && newConfig.contains(key)) {
                Object value = oldConfig.get(key);
                if (value != null) {
                    newConfig.set(key, value);
                }
            }
        }
    }
}
