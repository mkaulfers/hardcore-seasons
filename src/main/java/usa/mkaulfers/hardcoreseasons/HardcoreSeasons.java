package usa.mkaulfers.hardcoreseasons;

import usa.mkaulfers.hardcoreseasons.commands.HardcoreSeasonsCommand;
import usa.mkaulfers.hardcoreseasons.listeners.TrackableBlockPlaced;
import usa.mkaulfers.hardcoreseasons.models.Config;
import usa.mkaulfers.hardcoreseasons.models.MySQLConfig;
import usa.mkaulfers.hardcoreseasons.storage.SQLHandler;
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
        handleStorage();
        registerListeners();
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

        String host = rawConfig.getString("MySQL.host");
        int port = rawConfig.getInt("MySQL.port");
        String database = rawConfig.getString("MySQL.database");
        String username = rawConfig.getString("MySQL.username");
        String password = rawConfig.getString("MySQL.password");
        int updateInterval = rawConfig.getInt("MySQL.updateInterval");

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
        getServer().getPluginManager().registerEvents(new TrackableBlockPlaced(this.sqlHandler), this);
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
