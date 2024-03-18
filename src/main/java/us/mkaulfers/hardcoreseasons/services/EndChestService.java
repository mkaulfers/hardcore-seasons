package us.mkaulfers.hardcoreseasons.services;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.support.ConnectionSource;
import org.bukkit.Bukkit;
import us.mkaulfers.hardcoreseasons.models.EndChest;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EndChestService {
    public List<EndChest> endChests;
    private final DataSource ds;
    private final String jdbcUrl;
    
    public EndChestService(DataSource ds, String jdbcUrl) {
        this.ds = ds;
        this.jdbcUrl = jdbcUrl;
        loadEndChests();
    }

    public void loadEndChests() {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, jdbcUrl)) {
            Dao<EndChest, Integer> endChestDao = DaoManager.createDao(connectionSource, EndChest.class);
            endChests = endChestDao.queryForAll();
        } catch (Exception e) {
            Bukkit.getLogger().severe("[HardcoreSeasons]: Failed to load end chests: " + e.getMessage());
        }
    }

    public EndChest getEndChest(UUID playerId, int seasonId) {
        for (EndChest endChest : endChests) {
            if (endChest.getSeasonId() == seasonId && endChest.getPlayerId().equals(playerId)) {
                return endChest;
            }
        }

        return null;
    }

    public List<EndChest> getEndChests(int seasonId) {
        List<EndChest> filteredEndChests = new ArrayList<>();

        endChests.forEach(endChest -> {
            if (endChest.getSeasonId() == seasonId) {
                filteredEndChests.add(endChest);
            }
        });

        return filteredEndChests;
    }

    public void setEndChest(EndChest endChest) {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, jdbcUrl)) {
            Dao<EndChest, Integer> endChestDao = DaoManager.createDao(connectionSource, EndChest.class);
            endChestDao.create(endChest);
            endChests.add(endChest);
        } catch (Exception e) {
            Bukkit.getLogger().severe("[HardcoreSeasons]: Failed to set end chest: " + e.getMessage());
        }
    }

    public void updateEndChest(EndChest endChest) {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, jdbcUrl)) {
            Dao<EndChest, Integer> endChestDao = DaoManager.createDao(connectionSource, EndChest.class);
            endChestDao.update(endChest);

            for (EndChest chest : endChests) {
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
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, jdbcUrl)) {
            Dao<EndChest, Integer> endChestDao = DaoManager.createDao(connectionSource, EndChest.class);

            DeleteBuilder<EndChest, Integer> deleteBuilder = endChestDao.deleteBuilder();

            deleteBuilder.where()
                    .eq("season_id", seasonId);

            deleteBuilder.delete();

            endChests.removeIf(endChest -> endChest.getSeasonId() == seasonId);
        } catch (Exception e) {
            Bukkit.getLogger().severe("[HardcoreSeasons]: Failed to delete end chests: " + e.getMessage());
        }
    }
}
