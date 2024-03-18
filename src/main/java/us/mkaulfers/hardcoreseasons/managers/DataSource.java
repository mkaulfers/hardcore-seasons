package us.mkaulfers.hardcoreseasons.managers;

import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.models.*;
import us.mkaulfers.hardcoreseasons.services.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.UUID;

import static us.mkaulfers.hardcoreseasons.utils.ConversionUtils.daysToMillis;

public class DataSource {
    public EndChestService endChests;
    public ParticipantInventoryService inventories;
    public ParticipantService participants;
    public SeasonService seasons;
    public SeasonRewardService rewards;
    public TrackedContainerService containers;
    public VoteService votes;
    private final HardcoreSeasons plugin;
    private final HikariConfig config = new HikariConfig();
    private final HikariDataSource ds;

    public DataSource(HardcoreSeasons plugin) {
        this.plugin = plugin;

        PluginConfig pluginConfig = plugin.configManager.config;

        if (pluginConfig.storageType.equalsIgnoreCase("sqlite")) {
            config.setJdbcUrl("jdbc:sqlite:plugins/HardcoreSeasons/hardcoreseasons.db");
        } else {
            String host = pluginConfig.mySQLConfig.host;
            int port = pluginConfig.mySQLConfig.port;
            String database = pluginConfig.mySQLConfig.database;
            String username = pluginConfig.mySQLConfig.username;
            String password = pluginConfig.mySQLConfig.password;
            config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
            config.setUsername(username);
            config.setPassword(password);
        }

        this.ds = new HikariDataSource(config);

        initTables();

        endChests = new EndChestService(ds, config.getJdbcUrl());
        inventories = new ParticipantInventoryService(ds, config.getJdbcUrl());
        participants = new ParticipantService(ds, config.getJdbcUrl());
        seasons = new SeasonService(plugin, ds, config.getJdbcUrl());
        rewards = new SeasonRewardService(ds, config.getJdbcUrl());
        containers = new TrackedContainerService(ds, config.getJdbcUrl());
        votes = new VoteService(ds, config.getJdbcUrl());
    }

    private void initTables() {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, config.getJdbcUrl())) {
            TableUtils.createTableIfNotExists(connectionSource, EndChest.class);
            TableUtils.createTableIfNotExists(connectionSource, ParticipantInventory.class);
            TableUtils.createTableIfNotExists(connectionSource, Participant.class);
            TableUtils.createTableIfNotExists(connectionSource, Season.class);
            TableUtils.createTableIfNotExists(connectionSource, SeasonReward.class);
            TableUtils.createTableIfNotExists(connectionSource, TrackedContainer.class);
            TableUtils.createTableIfNotExists(connectionSource, Vote.class);
        } catch (Exception e) {
            Bukkit.getLogger().severe("[HardcoreSeasons]: Failed to create tables: " + e.getMessage());
        }
    }

    public void resurrectPlayer(UUID playerId) {
        Participant participant = participants.getParticipant(playerId, seasons.getActiveSeason().getSeasonId());
        participant.setDead(false);
        participants.updateParticipant(participant);
    }

    public void generatePlaceholderStats() {
        int playersActiveCount = 0;
        int playersAliveCount = 0;
        int playersDeadCount = 0;
        int inventoriesCount = 0;
        int inventoriesItemsCount = 0;
        int enderChestsCount = 0;
        int enderChestsItemsCount = 0;
        int containerCount = 0;
        int containerItemsCount = 0;

        for (Participant participant : participants.participants) {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            Timestamp threshold = new Timestamp(now.getTime() - daysToMillis(plugin.configManager.config.lastLoginThreshold));

            if (participant.getLastOnline().after(threshold)) {
                playersActiveCount++;
            }

            if (!participant.isDead()) {
                playersAliveCount++;
            } else {
                playersDeadCount++;
            }
        }

        for (ParticipantInventory inventory : inventories.inventories) {
            inventoriesCount++;
            inventoriesItemsCount += inventory.getContentsCount();
        }

        for (EndChest endChest : endChests.endChests) {
            enderChestsCount++;
            enderChestsItemsCount += endChest.getContentsCount();
        }

        for (TrackedContainer trackedContainer : containers.trackedContainers) {
            containerCount++;
            containerItemsCount += trackedContainer.getContentsCount();
        }

        Season activeSeason = seasons.getActiveSeason();
        Timestamp startDate = activeSeason.getStartDate();
        Timestamp softEndDate = activeSeason.getSoftEndDate();

        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy 'at' h:mma");
        String formattedStartDate = sdf.format(startDate);
        String formattedSoftEndDate = sdf.format(softEndDate);

        plugin.placeholderManager.seasonStartDate = formattedStartDate;
        plugin.placeholderManager.seasonVoteStartDate = formattedSoftEndDate;

        if (plugin.configManager.config.maxSeasonLength != -1) {
            Timestamp endDate = activeSeason.getHardEndDate();
            plugin.placeholderManager.seasonEndDate = sdf.format(endDate);
        }

        plugin.placeholderManager.playersActiveCount = playersActiveCount;
        plugin.placeholderManager.playersAliveCount = playersAliveCount;
        plugin.placeholderManager.playersDeadCount = playersDeadCount;
        plugin.placeholderManager.inventoriesCount = inventoriesCount;
        plugin.placeholderManager.inventoriesItemsCount = inventoriesItemsCount;
        plugin.placeholderManager.enderChestsCount = enderChestsCount;
        plugin.placeholderManager.enderChestsItemsCount = enderChestsItemsCount;
        plugin.placeholderManager.containersCount = containerCount;
        plugin.placeholderManager.containersItemsCount = containerItemsCount;
    }
}
