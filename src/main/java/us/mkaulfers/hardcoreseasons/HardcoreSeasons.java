package us.mkaulfers.hardcoreseasons;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import us.mkaulfers.hardcoreseasons.commands.SeasonCommand;
import us.mkaulfers.hardcoreseasons.listeners.*;
import us.mkaulfers.hardcoreseasons.managers.*;
import us.mkaulfers.hardcoreseasons.orm.HDataSource;

import static us.mkaulfers.hardcoreseasons.enums.InternalPlaceholder.CURRENT_SEASON;
import static us.mkaulfers.hardcoreseasons.enums.InternalPlaceholder.NEXT_SEASON;

public final class HardcoreSeasons extends JavaPlugin {
    public HDataSource hDataSource;
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
        currentSeasonNum = hDataSource.getActiveSeason().getSeasonId();
        placeholderManager = new PlaceholderManager();
        placeholderManager.setPlaceholderValue(CURRENT_SEASON, String.valueOf(currentSeasonNum));
        placeholderManager.setPlaceholderValue(NEXT_SEASON, String.valueOf(currentSeasonNum + 1));

        seasonManager = new SeasonManager(this);
        rewardManager = new RewardManager(this);
        worldManager = new WorldManager(this);
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
