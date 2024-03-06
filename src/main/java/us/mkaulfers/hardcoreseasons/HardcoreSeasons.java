package us.mkaulfers.hardcoreseasons;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import us.mkaulfers.hardcoreseasons.commands.HardcoreSeasonsCommand;
import us.mkaulfers.hardcoreseasons.commands.SurvivorCommand;
import us.mkaulfers.hardcoreseasons.interfaceimpl.SeasonDAOImpl;
import us.mkaulfers.hardcoreseasons.interfaces.SeasonDAO;
import us.mkaulfers.hardcoreseasons.listeners.*;
import us.mkaulfers.hardcoreseasons.managers.RewardManager;
import us.mkaulfers.hardcoreseasons.models.Database;
import us.mkaulfers.hardcoreseasons.models.MySQLConfig;
import us.mkaulfers.hardcoreseasons.models.PluginConfig;

import java.util.List;

public final class HardcoreSeasons extends JavaPlugin {
    public PluginConfig pluginConfig;
    public Database database;
    public int activeSeason;
    public RewardManager rewardManager;

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
        // Plugin shutdown logic
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
        this.getCommand("survivor").setExecutor(new SurvivorCommand(this));
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new BlockPlace(this), this);
        pm.registerEvents(new BlockBreak(this), this);
        pm.registerEvents(new PlayerJoin(this), this);
        pm.registerEvents(new PlayerDeath(this), this);
        pm.registerEvents(new PlayerQuit(this), this);
        pm.registerEvents(new InventoryClose(this), this);
    }

    private void handleStorage() {
        if (pluginConfig.storageType.equalsIgnoreCase("mysql")) {
            database = new Database(pluginConfig.mySQLConfig);

            SeasonDAO seasonDAO = new SeasonDAOImpl(database);
            seasonDAO.getActiveSeasonId().thenAccept(seasonId -> {
                activeSeason = seasonId;
            });

            rewardManager = new RewardManager(database);

        } else {
            /// Use SQLite
        }
    }
}
