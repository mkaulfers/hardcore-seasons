package usa.mkaulfers.hardcoreseasons;

import org.bukkit.plugin.PluginManager;
import usa.mkaulfers.hardcoreseasons.commands.HardcoreSeasonsCommand;
import usa.mkaulfers.hardcoreseasons.listeners.SurvivorContainerBreak;
import usa.mkaulfers.hardcoreseasons.listeners.SurvivorContainerPlace;
import usa.mkaulfers.hardcoreseasons.models.PluginConfig;
import usa.mkaulfers.hardcoreseasons.models.MySQLConfig;
import usa.mkaulfers.hardcoreseasons.storage.DBManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class HardcoreSeasons extends JavaPlugin {
    private PluginConfig pluginConfig;
    private DBManager dbManager;

    // Lifecycle methods
    @Override
    public void onEnable() {
        loadConfigs();
        registerCommands();
        handleStorage();
        registerListeners();
    }

    @Override
    public void onDisable() {
        dbManager.disconnect();
    }

    // Custom methods
    private void loadConfigs() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        FileConfiguration rawConfig = getConfig();
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

        pluginConfig = new PluginConfig(
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

    private void registerCommands() {
        this.getCommand("hardcoreseasons").setExecutor(new HardcoreSeasonsCommand(this));
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new SurvivorContainerPlace(this.dbManager), this);
        pm.registerEvents(new SurvivorContainerBreak(this.dbManager), this);
//        pm.registerEvents(new PlayerJoined(this.sqlHandler), this);
    }

    private void handleStorage() {
        if (pluginConfig.storageType.equalsIgnoreCase("mysql")) {
            if (dbManager == null) {
                dbManager = new DBManager(pluginConfig);
                dbManager.connect();
                dbManager.initTables();
            }
        } else {
            /// Use SQLite
        }
    }
}
