package us.mkaulfers.hardcoreseasons.services;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.support.ConnectionSource;
import org.bukkit.Bukkit;
import us.mkaulfers.hardcoreseasons.models.SeasonReward;
import us.mkaulfers.hardcoreseasons.models.TrackedContainer;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class TrackedContainerService {
    public List<TrackedContainer> trackedContainers;
    private final DataSource ds;
    private final String jdbcUrl;

    public TrackedContainerService(DataSource ds, String jdbcUrl) {
        this.ds = ds;
        this.jdbcUrl = jdbcUrl;
        loadTrackedContainers();
    }

    private void loadTrackedContainers() {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, jdbcUrl)) {
            Dao<TrackedContainer, Integer> trackedContainerDao = DaoManager.createDao(connectionSource, TrackedContainer.class);
            trackedContainers = trackedContainerDao.queryForAll();
        } catch (Exception e) {
            Bukkit.getLogger().severe("[HardcoreSeasons]: Failed to load tracked containers: " + e.getMessage());
        }
    }

    public TrackedContainer getTrackedContainer(int seasonId, int posX, int posY, int posZ, String world, String type) {
        for (TrackedContainer trackedContainer : trackedContainers) {
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

    public List<TrackedContainer> getTrackedContainers(int seasonId) {
        List<TrackedContainer> filteredTrackedContainers = new ArrayList<>();

        trackedContainers.forEach(trackedContainer -> {
            if (trackedContainer.getSeasonId() == seasonId) {
                filteredTrackedContainers.add(trackedContainer);
            }
        });

        return filteredTrackedContainers;
    }

    public void setTrackedContainer(TrackedContainer trackedContainer) {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, jdbcUrl)) {
            Dao<TrackedContainer, Integer> trackedContainerDao = DaoManager.createDao(connectionSource, TrackedContainer.class);
            trackedContainerDao.create(trackedContainer);
            trackedContainers.add(trackedContainer);
        } catch (Exception e) {
            Bukkit.getLogger().severe("[HardcoreSeasons]: Failed to set tracked container: " + e.getMessage());
        }
    }

    public void updateTrackedContainer(TrackedContainer trackedContainer) {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, jdbcUrl)) {
            Dao<TrackedContainer, Integer> trackedContainerDao = DaoManager.createDao(connectionSource, TrackedContainer.class);
            trackedContainerDao.update(trackedContainer);

            for (TrackedContainer container : trackedContainers) {
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
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, jdbcUrl)) {
            Dao<TrackedContainer, Integer> trackedContainerDao = DaoManager.createDao(connectionSource, TrackedContainer.class);

            DeleteBuilder<TrackedContainer, Integer> deleteBuilder = trackedContainerDao.deleteBuilder();

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
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, jdbcUrl)) {
            Dao<TrackedContainer, Integer> trackedContainerDao = DaoManager.createDao(connectionSource, TrackedContainer.class);

            DeleteBuilder<TrackedContainer, Integer> deleteBuilder = trackedContainerDao.deleteBuilder();

            deleteBuilder.where()
                    .eq("season_id", seasonId);

            deleteBuilder.delete();

            trackedContainers.removeIf(trackedContainer -> trackedContainer.getSeasonId() == seasonId);
        } catch (Exception e) {
            Bukkit.getLogger().severe("[HardcoreSeasons]: Failed to delete tracked containers: " + e.getMessage());
        }
    }
}
