package us.mkaulfers.hardcoreseasons;

import co.aikar.commands.PaperCommandManager;
import com.j256.ormlite.logger.Level;
import com.j256.ormlite.logger.Logger;
import org.bstats.bukkit.Metrics;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import us.mkaulfers.hardcoreseasons.commands.SeasonCMD;
import us.mkaulfers.hardcoreseasons.guis.InfoSidebar;
import us.mkaulfers.hardcoreseasons.listeners.*;
import us.mkaulfers.hardcoreseasons.managers.*;
import us.mkaulfers.hardcoreseasons.models.ResRequest;
import us.mkaulfers.hardcoreseasons.models.Season;

import java.util.ArrayList;
import java.util.List;

public final class HardcoreSeasons extends JavaPlugin {
    public DataSource db;
    public boolean isGeneratingNewSeason = false;
    public Season activeSeason;
    public ConfigManager configManager;
    public SeasonManager seasonManager;
    public WorldManager worldManager;
    public RewardManager rewardManager;
    public PlaceholderManager placeholderManager;
    public InfoSidebar infoSidebar;
    public Metrics metrics;

    // Lifecycle methods
    @Override
    public void onEnable() {
        this.configManager = new ConfigManager(this);
        this.metrics = new Metrics(this, 21353);
        setLogLevel();
        registerCommands();
        handleStorage();
        registerListeners();
        initInfoSidebar();
    }

    private void setLogLevel() {
        switch (configManager.config.loggingLevel) {
            case "INFO":
                Logger.setGlobalLogLevel(Level.INFO);
                break;
            case "WARNING":
                Logger.setGlobalLogLevel(Level.WARNING);
                break;
            case "ERROR":
                Logger.setGlobalLogLevel(Level.ERROR);
                break;
            default:
                Logger.setGlobalLogLevel(Level.OFF);
        }
    }

    private void registerCommands() {
        PaperCommandManager manager = new PaperCommandManager(this);

        manager.getCommandContexts().registerContext(ResRequest.class, c -> {
            String playerName = c.popFirstArg();
            OfflinePlayer offlinePlayer = getServer().getOfflinePlayer(playerName);
            return new ResRequest(playerName, offlinePlayer.getUniqueId());
        });

        manager.getCommandCompletions().registerCompletion("resurrectCompletion", c -> {
            List<String> playerNames = new ArrayList<>();
            OfflinePlayer[] offlinePlayers = getServer().getOfflinePlayers();

            for (OfflinePlayer offlinePlayer : offlinePlayers) {
                playerNames.add(offlinePlayer.getName());
            }

            return playerNames;
        });

        manager.registerCommand(new SeasonCMD(this));
    }

    private void handleStorage() {
        if (!configManager.config.trackingEnabled) {
            return;
        }

        db = new DataSource(this);
        activeSeason = db.seasons.getActiveSeason();
        placeholderManager = new PlaceholderManager();
        placeholderManager.currentSeason = activeSeason.getSeasonId();
        placeholderManager.nextSeason = activeSeason.getSeasonId() + 1;

        seasonManager = new SeasonManager(this);
        rewardManager = new RewardManager(this);
        worldManager = new WorldManager(this);
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new AsyncPlayerPreLogin(this), this);
        pm.registerEvents(new BlockBreak(this), this);
        pm.registerEvents(new BlockPlace(this), this);
        pm.registerEvents(new EntityPickupItem(this), this);
        pm.registerEvents(new InventoryClose(this), this);
        pm.registerEvents(new InventoryMoveItem(this), this);
        pm.registerEvents(new PlayerItemConsume(this), this);
        pm.registerEvents(new PlayerDeath(this), this);
        pm.registerEvents(new PlayerDropItem(this), this);
        pm.registerEvents(new PlayerJoin(this), this);
        pm.registerEvents(new PlayerPortal(this), this);
        pm.registerEvents(new PlayerQuit(this), this);
        pm.registerEvents(new PlayerSpawnLocation(this), this);
    }

    private void initInfoSidebar() {
        if (!configManager.config.trackingEnabled) {
            return;
        }

        infoSidebar = new InfoSidebar(this);
    }
}
