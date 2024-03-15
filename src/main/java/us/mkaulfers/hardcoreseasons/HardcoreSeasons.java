package us.mkaulfers.hardcoreseasons;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import us.mkaulfers.hardcoreseasons.commands.SeasonCommand;
import us.mkaulfers.hardcoreseasons.interfaceimpl.SeasonDAOImpl;
import us.mkaulfers.hardcoreseasons.interfaces.SeasonDAO;
import us.mkaulfers.hardcoreseasons.listeners.*;
import us.mkaulfers.hardcoreseasons.managers.*;
import us.mkaulfers.hardcoreseasons.models.Database;
import us.mkaulfers.hardcoreseasons.models.MySQLConfig;
import us.mkaulfers.hardcoreseasons.orm.HDataSource;
import us.mkaulfers.hardcoreseasons.orm.HSeason;

import java.sql.Connection;
import java.sql.SQLException;

import static us.mkaulfers.hardcoreseasons.enums.InternalPlaceholder.CURRENT_SEASON;
import static us.mkaulfers.hardcoreseasons.enums.InternalPlaceholder.NEXT_SEASON;

public final class HardcoreSeasons extends JavaPlugin {
    public HDataSource hDataSource;
    public Database database;
    public boolean isGeneratingNewSeason = false;
    public int currentSeasonNum;
    public ConfigManager configManager;
    public SeasonManager seasonManager;
    public WorldManager worldManager;
    public RewardManager rewardManager;
    public PlaceholderManager placeholderManager;

    // Lifecycle methods
    @Override
    public void onEnable() {
        this.configManager = new ConfigManager(this);
        registerCommands();
        handleStorage();
        registerListeners();
    }

    private void registerCommands() {
        this.getCommand("season").setExecutor(new SeasonCommand(this));
    }

    private void handleStorage() {
        if (!configManager.config.trackingEnabled) {
            return;
        }

        hDataSource = new HDataSource(configManager.config);

        seasonManager = new SeasonManager(this);
        rewardManager = new RewardManager(this);
        worldManager = new WorldManager(this);


        database = new Database(configManager.config);
        SeasonDAO seasonDAO = new SeasonDAOImpl(database);
        seasonDAO.getActiveSeasonId().thenAccept(seasonId -> {
            currentSeasonNum = seasonId;
            placeholderManager = new PlaceholderManager();
            placeholderManager.setPlaceholderValue(CURRENT_SEASON, String.valueOf(seasonId));
            placeholderManager.setPlaceholderValue(NEXT_SEASON, String.valueOf(seasonId + 1));
        });
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new BlockPlace(this), this);
        pm.registerEvents(new BlockBreak(this), this);
        pm.registerEvents(new PlayerJoin(this), this);
        pm.registerEvents(new PlayerDeath(this), this);
        pm.registerEvents(new PlayerQuit(this), this);
        pm.registerEvents(new InventoryClose(this), this);
        pm.registerEvents(new InventoryMoveItem(this), this);
        pm.registerEvents(new PlayerPortal(this), this);
        pm.registerEvents(new PlayerSpawnLocation(this), this);
        pm.registerEvents(new AsyncPlayerPreLogin(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
