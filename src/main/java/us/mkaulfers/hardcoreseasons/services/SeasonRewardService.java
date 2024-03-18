package us.mkaulfers.hardcoreseasons.services;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import org.bukkit.Bukkit;
import us.mkaulfers.hardcoreseasons.models.ParticipantInventory;
import us.mkaulfers.hardcoreseasons.models.SeasonReward;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SeasonRewardService {
    public List<SeasonReward> seasonRewards;
    private final DataSource ds;
    private final String jdbcUrl;

    public SeasonRewardService(DataSource ds, String jdbcUrl) {
        this.ds = ds;
        this.jdbcUrl = jdbcUrl;
        loadSeasonRewards();
    }

    private void loadSeasonRewards() {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, jdbcUrl)) {
            Dao<SeasonReward, Integer> seasonRewardDao = DaoManager.createDao(connectionSource, SeasonReward.class);
            seasonRewards = seasonRewardDao.queryForAll();
        } catch (Exception e) {
            Bukkit.getLogger().severe("[HardcoreSeasons]: Failed to load season rewards: " + e.getMessage());
        }
    }

    public List<SeasonReward> getSeasonRewards(UUID playerId) {
        List<SeasonReward> filteredSeasonRewards = new ArrayList<>();

        seasonRewards.forEach(seasonReward -> {
            if (seasonReward.getPlayerId().equals(playerId)) {
                filteredSeasonRewards.add(seasonReward);
            }
        });

        return filteredSeasonRewards;
    }

    public void setSeasonReward(SeasonReward seasonReward) {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, jdbcUrl)) {
            Dao<SeasonReward, Integer> seasonRewardDao = DaoManager.createDao(connectionSource, SeasonReward.class);
            seasonRewardDao.create(seasonReward);
            seasonRewards.add(seasonReward);
        } catch (Exception e) {
            Bukkit.getLogger().severe("[HardcoreSeasons]: Failed to set season reward: " + e.getMessage());
        }
    }

    public void updateSeasonReward(int seasonId, UUID playerId, String contents) {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, jdbcUrl)) {
            Dao<SeasonReward, Integer> seasonRewardDao = DaoManager.createDao(connectionSource, SeasonReward.class);

            for (SeasonReward seasonReward : seasonRewards) {
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
}
