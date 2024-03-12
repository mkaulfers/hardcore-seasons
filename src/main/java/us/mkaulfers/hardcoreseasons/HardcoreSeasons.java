package us.mkaulfers.hardcoreseasons;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import us.mkaulfers.hardcoreseasons.commands.SeasonCommand;
import us.mkaulfers.hardcoreseasons.interfaceimpl.SeasonDAOImpl;
import us.mkaulfers.hardcoreseasons.interfaces.SeasonDAO;
import us.mkaulfers.hardcoreseasons.listeners.*;
import us.mkaulfers.hardcoreseasons.managers.ConfigManager;
import us.mkaulfers.hardcoreseasons.managers.RewardManager;
import us.mkaulfers.hardcoreseasons.managers.SeasonManager;
import us.mkaulfers.hardcoreseasons.managers.WorldManager;
import us.mkaulfers.hardcoreseasons.models.Database;

public final class HardcoreSeasons extends JavaPlugin {
    public Database database;
    public boolean isGeneratingNewSeason = false;
    public int currentSeasonNum;
    public ConfigManager configManager;
    public SeasonManager seasonManager;
    public WorldManager worldManager;
    public RewardManager rewardManager;

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

        database = new Database(configManager.config);

        SeasonDAO seasonDAO = new SeasonDAOImpl(database);
        seasonDAO.getActiveSeasonId().thenAccept(seasonId -> {
            currentSeasonNum = seasonId;

            Bukkit.getScheduler().runTask(this, () -> {
                seasonManager = new SeasonManager(this);
                rewardManager = new RewardManager(this);
                worldManager = new WorldManager(this);
            });
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
