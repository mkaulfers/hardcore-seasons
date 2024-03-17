package us.mkaulfers.hardcoreseasons;

import co.aikar.commands.PaperCommandManager;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.exception.NoPacketAdapterAvailableException;
import net.megavex.scoreboardlibrary.api.noop.NoopScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.api.sidebar.component.ComponentSidebarLayout;
import net.megavex.scoreboardlibrary.api.sidebar.component.SidebarComponent;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import us.mkaulfers.hardcoreseasons.commands.Season;
import us.mkaulfers.hardcoreseasons.guis.InfoSidebar;
import us.mkaulfers.hardcoreseasons.listeners.*;
import us.mkaulfers.hardcoreseasons.managers.*;
import us.mkaulfers.hardcoreseasons.models.ResRequest;
import us.mkaulfers.hardcoreseasons.orm.HDataSource;

import java.util.ArrayList;
import java.util.List;

public final class HardcoreSeasons extends JavaPlugin {
    public HDataSource hDataSource;
    public boolean isGeneratingNewSeason = false;
    public int currentSeasonNum;
    public ConfigManager configManager;
    public SeasonManager seasonManager;
    public WorldManager worldManager;
    public RewardManager rewardManager;
    public PlaceholderManager placeholderManager;
    public InfoSidebar infoSidebar;

    // Lifecycle methods
    @Override
    public void onEnable() {
        this.configManager = new ConfigManager(this);
        registerCommands();
        handleStorage();
        registerListeners();
        initInfoSidebar();
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

        manager.registerCommand(new Season(this));
    }

    private void handleStorage() {
        if (!configManager.config.trackingEnabled) {
            return;
        }

        hDataSource = new HDataSource(this);
        currentSeasonNum = hDataSource.getActiveSeason().getSeasonId();
        placeholderManager = new PlaceholderManager();
        placeholderManager.currentSeason = currentSeasonNum;
        placeholderManager.nextSeason = currentSeasonNum + 1;

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

    private void initInfoSidebar() {
        if (!configManager.config.trackingEnabled) {
            return;
        }

        infoSidebar = new InfoSidebar(this);
    }
}
