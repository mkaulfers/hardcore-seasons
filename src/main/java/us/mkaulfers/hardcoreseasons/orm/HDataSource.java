package us.mkaulfers.hardcoreseasons.orm;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import us.mkaulfers.hardcoreseasons.models.PluginConfig;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HDataSource {
    private final PluginConfig pluginConfig;
    private final HikariConfig config = new HikariConfig();
    private final HikariDataSource ds;

    private List<HEndChest> endChests;
    private List<HInventory> inventories;
    private List<HParticipant> participants;
    private List<HSeason> seasons;
    private List<HSeasonReward> seasonRewards;
    private List<HTrackedContainer> trackedContainers;
    private List<HVote> votes;

    public HEndChest getEndChest(UUID playerId, int seasonId) {
        for (HEndChest endChest : endChests) {
            if (endChest.getSeasonId() == seasonId && endChest.getPlayerId().equals(playerId)) {
                return endChest;
            }
        }

        return null;
    }

    public List<HEndChest> getEndChests(int seasonId) {
        List<HEndChest> filteredEndChests = new ArrayList<>();

        endChests.forEach(endChest -> {
            if (endChest.getSeasonId() == seasonId) {
                filteredEndChests.add(endChest);
            }
        });

        return filteredEndChests;
    }

    public void setEndChest(HEndChest endChest) {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, config.getJdbcUrl())) {
            Dao<HEndChest, Integer> endChestDao = DaoManager.createDao(connectionSource, HEndChest.class);
            endChestDao.create(endChest);
            endChests.add(endChest);
        } catch (Exception e) {
            Bukkit.getLogger().severe("[HardcoreSeasons]: Failed to set end chest: " + e.getMessage());
        }
    }

    public void updateEndChest(HEndChest endChest) {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, config.getJdbcUrl())) {
            Dao<HEndChest, Integer> endChestDao = DaoManager.createDao(connectionSource, HEndChest.class);
            endChestDao.update(endChest);

            for (HEndChest chest : endChests) {
                if (chest.getId() == endChest.getId()) {
                    chest.setId(endChest.getId());
                    chest.setSeasonId(endChest.getSeasonId());
                    chest.setPlayerId(endChest.getPlayerId());
                    chest.setContents(endChest.getContents());
                    break;
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().severe("[HardcoreSeasons]: Failed to update end chest: " + e.getMessage());
        }
    }

    public void deleteEndChests(int seasonId) {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, config.getJdbcUrl())) {
            Dao<HEndChest, Integer> endChestDao = DaoManager.createDao(connectionSource, HEndChest.class);

            DeleteBuilder<HEndChest, Integer> deleteBuilder = endChestDao.deleteBuilder();

            deleteBuilder.where()
                    .eq("season_id", seasonId);

            deleteBuilder.delete();

            endChests.removeIf(endChest -> endChest.getSeasonId() == seasonId);
        } catch (Exception e) {
            Bukkit.getLogger().severe("[HardcoreSeasons]: Failed to delete end chests: " + e.getMessage());
        }
    }

    public HInventory getInventory(UUID playerId, int seasonId) {
        for (HInventory inventory : inventories) {
            if (inventory.getSeasonId() == seasonId && inventory.getPlayerId().equals(playerId)) {
                return inventory;
            }
        }

        return null;
    }

    public List<HInventory> getInventories(int seasonId) {
        List<HInventory> filteredInventories = new ArrayList<>();

        inventories.forEach(inventory -> {
            if (inventory.getSeasonId() == seasonId) {
                filteredInventories.add(inventory);
            }
        });

        return filteredInventories;
    }

    public void setInventory(HInventory inventory) {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, config.getJdbcUrl())) {
            Dao<HInventory, Integer> inventoryDao = DaoManager.createDao(connectionSource, HInventory.class);
            inventoryDao.create(inventory);
            inventories.add(inventory);
        } catch (Exception e) {
            Bukkit.getLogger().severe("[HardcoreSeasons]: Failed to set inventory: " + e.getMessage());
        }
    }

    public void updateInventory(HInventory inventory) {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, config.getJdbcUrl())) {
            Dao<HInventory, Integer> inventoryDao = DaoManager.createDao(connectionSource, HInventory.class);
            inventoryDao.update(inventory);

            for (HInventory inv : inventories) {
                if (inv.getId() == inventory.getId()) {
                    inv.setId(inventory.getId());
                    inv.setSeasonId(inventory.getSeasonId());
                    inv.setPlayerId(inventory.getPlayerId());
                    inv.setContents(inventory.getContents());
                    break;
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().severe("[HardcoreSeasons]: Failed to update inventory: " + e.getMessage());
        }
    }

    public void deleteInventories(int seasonId) {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, config.getJdbcUrl())) {
            Dao<HInventory, Integer> inventoryDao = DaoManager.createDao(connectionSource, HInventory.class);

            DeleteBuilder<HInventory, Integer> deleteBuilder = inventoryDao.deleteBuilder();

            deleteBuilder.where()
                    .eq("season_id", seasonId);

            deleteBuilder.delete();

            inventories.removeIf(inventory -> inventory.getSeasonId() == seasonId);
        } catch (Exception e) {
            Bukkit.getLogger().severe("[HardcoreSeasons]: Failed to delete inventories: " + e.getMessage());
        }
    }

    public HParticipant getParticipant(UUID uuid, int seasonId) {
        for (HParticipant participant : participants) {
            if (participant.getSeasonId() == seasonId && participant.getPlayerId().equals(uuid)) {
                return participant;
            }
        }

        return null;
    }

    public List<HParticipant> getParticipants(int seasonId) {
        List<HParticipant> filteredParticipants = new ArrayList<>();

        participants.forEach(participant -> {
            if (participant.getSeasonId() == seasonId) {
                filteredParticipants.add(participant);
            }
        });

        return filteredParticipants;
    }

    public void setParticipant(HParticipant participant) {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, config.getJdbcUrl())) {
            Dao<HParticipant, Integer> participantDao = DaoManager.createDao(connectionSource, HParticipant.class);
            participantDao.create(participant);
            participants.add(participant);
        } catch (Exception e) {
            Bukkit.getLogger().severe("[HardcoreSeasons]: Failed to set participant: " + e.getMessage());
        }
    }

    public void updateParticipant(HParticipant participant) {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, config.getJdbcUrl())) {
            Dao<HParticipant, Integer> participantDao = DaoManager.createDao(connectionSource, HParticipant.class);
            participantDao.update(participant);

            for (HParticipant p : participants) {
                if (p.getId() == participant.getId()) {
                    p.setPlayerId(participant.getPlayerId());
                    p.setSeasonId(participant.getSeasonId());
                    p.setJoinDate(participant.getJoinDate());
                    p.setLastOnline(participant.getLastOnline());
                    p.setLastX(participant.getLastX());
                    p.setLastY(participant.getLastY());
                    p.setLastZ(participant.getLastZ());
                    p.setDead(participant.isDead());
                    break;
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().severe("[HardcoreSeasons]: Failed to update participant: " + e.getMessage());
        }
    }

    public HSeason getActiveSeason() {
        HSeason activeSeason = null;

        // Get season with the highest seasonId
        for (HSeason season : seasons) {
            if (activeSeason == null || season.getSeasonId() > activeSeason.getSeasonId()) {
                activeSeason = season;
            }
        }

        return activeSeason;
    }

    public void setActiveSeason(HSeason season) {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, config.getJdbcUrl())) {
            Dao<HSeason, Integer> seasonDao = DaoManager.createDao(connectionSource, HSeason.class);
            seasonDao.create(season);
            seasons.add(season);
        } catch (Exception e) {
            Bukkit.getLogger().severe("[HardcoreSeasons]: Failed to set active season: " + e.getMessage());
        }
    }

    public List<HSeasonReward> getSeasonRewards(UUID playerId) {
        List<HSeasonReward> filteredSeasonRewards = new ArrayList<>();

        seasonRewards.forEach(seasonReward -> {
            if (seasonReward.getPlayerId().equals(playerId)) {
                filteredSeasonRewards.add(seasonReward);
            }
        });

        return filteredSeasonRewards;
    }

    public void setSeasonReward(HSeasonReward seasonReward) {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, config.getJdbcUrl())) {
            Dao<HSeasonReward, Integer> seasonRewardDao = DaoManager.createDao(connectionSource, HSeasonReward.class);
            seasonRewardDao.create(seasonReward);
            seasonRewards.add(seasonReward);
        } catch (Exception e) {
            Bukkit.getLogger().severe("[HardcoreSeasons]: Failed to set season reward: " + e.getMessage());
        }
    }

    public void updateSeasonReward(int seasonId, UUID playerId, String contents) {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, config.getJdbcUrl())) {
            Dao<HSeasonReward, Integer> seasonRewardDao = DaoManager.createDao(connectionSource, HSeasonReward.class);

            for (HSeasonReward seasonReward : seasonRewards) {
                if (seasonReward.getSeasonId() == seasonId && seasonReward.getPlayerId().equals(playerId)) {
                    seasonReward.setContents(contents);
                    seasonRewardDao.update(seasonReward);
                    break;
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().severe("[HardcoreSeasons]: Failed to update season reward: " + e.getMessage());
        }
    }

    public HTrackedContainer getTrackedContainer(int seasonId, int posX, int posY, int posZ, String world, String type) {
        for (HTrackedContainer trackedContainer : trackedContainers) {
            if (trackedContainer.getSeasonId() == seasonId &&
                    trackedContainer.getPosX() == posX &&
                    trackedContainer.getPosY() == posY &&
                    trackedContainer.getPosZ() == posZ &&
                    trackedContainer.getWorld().equals(world) &&
                    trackedContainer.getType().equals(type)) {
                return trackedContainer;
            }
        }

        return null;
    }

    public List<HTrackedContainer> getTrackedContainers(int seasonId) {
        List<HTrackedContainer> filteredTrackedContainers = new ArrayList<>();

        trackedContainers.forEach(trackedContainer -> {
            if (trackedContainer.getSeasonId() == seasonId) {
                filteredTrackedContainers.add(trackedContainer);
            }
        });

        return filteredTrackedContainers;
    }

    public void setTrackedContainer(HTrackedContainer trackedContainer) {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, config.getJdbcUrl())) {
            Dao<HTrackedContainer, Integer> trackedContainerDao = DaoManager.createDao(connectionSource, HTrackedContainer.class);
            trackedContainerDao.create(trackedContainer);
            trackedContainers.add(trackedContainer);
        } catch (Exception e) {
            Bukkit.getLogger().severe("[HardcoreSeasons]: Failed to set tracked container: " + e.getMessage());
        }
    }

    public void updateTrackedContainer(HTrackedContainer trackedContainer) {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, config.getJdbcUrl())) {
            Dao<HTrackedContainer, Integer> trackedContainerDao = DaoManager.createDao(connectionSource, HTrackedContainer.class);
            trackedContainerDao.update(trackedContainer);

            for (HTrackedContainer container : trackedContainers) {
                if (container.getSeasonId() == trackedContainer.getSeasonId() &&
                        container.getPosX() == trackedContainer.getPosX() &&
                        container.getPosY() == trackedContainer.getPosY() &&
                        container.getPosZ() == trackedContainer.getPosZ() &&
                        container.getWorld().equals(trackedContainer.getWorld()) &&
                        container.getType().equals(trackedContainer.getType())) {
                    container.setContents(trackedContainer.getContents());
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().severe("[HardcoreSeasons]: Failed to update tracked container: " + e.getMessage());
        }
    }

    public void deleteTrackedContainer(int seasonId, int posX, int posY, int posZ) {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, config.getJdbcUrl())) {
            Dao<HTrackedContainer, Integer> trackedContainerDao = DaoManager.createDao(connectionSource, HTrackedContainer.class);

            DeleteBuilder<HTrackedContainer, Integer> deleteBuilder = trackedContainerDao.deleteBuilder();

            deleteBuilder.where()
                    .eq("season_id", seasonId)
                    .and()
                    .eq("pos_x", posX)
                    .and()
                    .eq("pos_y", posY)
                    .and()
                    .eq("pos_z", posZ);

            deleteBuilder.delete();

            trackedContainers.removeIf(trackedContainer ->
                    trackedContainer.getSeasonId() == seasonId &&
                            trackedContainer.getPosX() == posX &&
                            trackedContainer.getPosY() == posY &&
                            trackedContainer.getPosZ() == posZ
            );

        } catch (Exception e) {
            Bukkit.getLogger().severe("[HardcoreSeasons]: Failed to delete tracked container: " + e.getMessage());
        }
    }

    public void deleteTrackedContainers(int seasonId) {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, config.getJdbcUrl())) {
            Dao<HTrackedContainer, Integer> trackedContainerDao = DaoManager.createDao(connectionSource, HTrackedContainer.class);

            DeleteBuilder<HTrackedContainer, Integer> deleteBuilder = trackedContainerDao.deleteBuilder();

            deleteBuilder.where()
                    .eq("season_id", seasonId);

            deleteBuilder.delete();

            trackedContainers.removeIf(trackedContainer -> trackedContainer.getSeasonId() == seasonId);
        } catch (Exception e) {
            Bukkit.getLogger().severe("[HardcoreSeasons]: Failed to delete tracked containers: " + e.getMessage());
        }
    }

    public HVote getVote(UUID playerId, int seasonId) {
        for (HVote vote : votes) {
            if (vote.getSeasonId() == seasonId && vote.getPlayerId().equals(playerId)) {
                return vote;
            }
        }

        return null;
    }

    public List<HVote> getVotes(int seasonId) {
        List<HVote> filteredVotes = new ArrayList<>();

        votes.forEach(vote -> {
            if (vote.getSeasonId() == seasonId) {
                filteredVotes.add(vote);
            }
        });

        return filteredVotes;
    }

    public boolean setVote(HVote vote) {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, config.getJdbcUrl())) {
            Dao<HVote, Integer> votesDao = DaoManager.createDao(connectionSource, HVote.class);
            votesDao.create(vote);
            votes.add(vote);
            return true;
        } catch (Exception e) {
            Bukkit.getLogger().severe("[HardcoreSeasons]: Failed to set vote: " + e.getMessage());
            return false;
        }
    }

    public HDataSource(PluginConfig pluginConfig) {
        if (!pluginConfig.storageType.equalsIgnoreCase("sqlite")) {
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

        this.pluginConfig = pluginConfig;
        this.ds = new HikariDataSource(config);

        initTables();
        loadTables();
    }

    private void initTables() {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, config.getJdbcUrl())) {
            TableUtils.createTableIfNotExists(connectionSource, HEndChest.class);
            TableUtils.createTableIfNotExists(connectionSource, HInventory.class);
            TableUtils.createTableIfNotExists(connectionSource, HParticipant.class);
            TableUtils.createTableIfNotExists(connectionSource, HSeason.class);
            TableUtils.createTableIfNotExists(connectionSource, HSeasonReward.class);
            TableUtils.createTableIfNotExists(connectionSource, HTrackedContainer.class);
            TableUtils.createTableIfNotExists(connectionSource, HVote.class);
        } catch (Exception e) {
            Bukkit.getLogger().severe("[HardcoreSeasons]: Failed to create tables: " + e.getMessage());
        }
    }

    private void loadTables() {
        loadEndChests();
        loadInventories();
        loadParticipants();
        loadSeasons();
        loadSeasonRewards();
        loadTrackedContainers();
        loadVotes();
    }

    private void loadEndChests() {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, config.getJdbcUrl())) {
            Dao<HEndChest, Integer> endChestDao = DaoManager.createDao(connectionSource, HEndChest.class);
            endChests = endChestDao.queryForAll();
        } catch (Exception e) {
            Bukkit.getLogger().severe("[HardcoreSeasons]: Failed to load end chests: " + e.getMessage());
        }
    }

    private void loadInventories() {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, config.getJdbcUrl())) {
            Dao<HInventory, Integer> inventoryDao = DaoManager.createDao(connectionSource, HInventory.class);
            inventories = inventoryDao.queryForAll();
        } catch (Exception e) {
            Bukkit.getLogger().severe("[HardcoreSeasons]: Failed to load inventories: " + e.getMessage());
        }
    }

    private void loadParticipants() {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, config.getJdbcUrl())) {
            Dao<HParticipant, Integer> participantDao = DaoManager.createDao(connectionSource, HParticipant.class);
            participants = participantDao.queryForAll();
        } catch (Exception e) {
            Bukkit.getLogger().severe("[HardcoreSeasons]: Failed to load participants: " + e.getMessage());
        }
    }

    private void loadSeasons() {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, config.getJdbcUrl())) {
            Dao<HSeason, Integer> seasonDao = DaoManager.createDao(connectionSource, HSeason.class);
            seasons = seasonDao.queryForAll();

            if (seasons.isEmpty()) {
                HSeason season = new HSeason();
                season.setId(1);
                season.setSeasonId(1);
                season.setStartDate(new Timestamp(System.currentTimeMillis()));
                season.setSoftEndDate(new Timestamp(System.currentTimeMillis() + daysToMillis(pluginConfig.minSeasonLength)));
                season.setHardEndDate(new Timestamp(System.currentTimeMillis() + daysToMillis(pluginConfig.maxSeasonLength)));
                seasonDao.create(season);
                seasons = seasonDao.queryForAll();
            }

        } catch (Exception e) {
            Bukkit.getLogger().severe("[HardcoreSeasons]: Failed to load seasons: " + e.getMessage());
        }
    }

    private void loadSeasonRewards() {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, config.getJdbcUrl())) {
            Dao<HSeasonReward, Integer> seasonRewardDao = DaoManager.createDao(connectionSource, HSeasonReward.class);
            seasonRewards = seasonRewardDao.queryForAll();
        } catch (Exception e) {
            Bukkit.getLogger().severe("[HardcoreSeasons]: Failed to load season rewards: " + e.getMessage());
        }
    }

    private void loadTrackedContainers() {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, config.getJdbcUrl())) {
            Dao<HTrackedContainer, Integer> trackedContainerDao = DaoManager.createDao(connectionSource, HTrackedContainer.class);
            trackedContainers = trackedContainerDao.queryForAll();
        } catch (Exception e) {
            Bukkit.getLogger().severe("[HardcoreSeasons]: Failed to load tracked containers: " + e.getMessage());
        }
    }

    private void loadVotes() {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, config.getJdbcUrl())) {
            Dao<HVote, Integer> votesDao = DaoManager.createDao(connectionSource, HVote.class);
            votes = votesDao.queryForAll();
        } catch (Exception e) {
            Bukkit.getLogger().severe("[HardcoreSeasons]: Failed to load votes: " + e.getMessage());
        }
    }

    private long daysToMillis(int days) {
        return 1000L * 60 * 60 * 24 * days;
    }
}
