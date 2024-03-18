package us.mkaulfers.hardcoreseasons.services;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import org.bukkit.Bukkit;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.models.Season;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.List;

import static us.mkaulfers.hardcoreseasons.utils.ConversionUtils.daysToMillis;

public class SeasonService {
    public List<Season> seasons;
    private final HardcoreSeasons plugin;
    private final DataSource ds;
    private final String jdbcUrl;

    public SeasonService(HardcoreSeasons plugin, DataSource ds, String jdbcUrl) {
        this.plugin = plugin;
        this.ds = ds;
        this.jdbcUrl = jdbcUrl;
        loadSeasons();
    }

    private void loadSeasons() {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, jdbcUrl)) {
            Dao<Season, Integer> seasonDao = DaoManager.createDao(connectionSource, Season.class);
            seasons = seasonDao.queryForAll();

            if (seasons.isEmpty()) {
                Season season = new Season();
                season.setId(1);
                season.setSeasonId(1);
                season.setStartDate(new Timestamp(System.currentTimeMillis()));
                season.setSoftEndDate(new Timestamp(System.currentTimeMillis() + daysToMillis(plugin.configManager.config.minSeasonLength)));
                season.setHardEndDate(new Timestamp(System.currentTimeMillis() + daysToMillis(plugin.configManager.config.maxSeasonLength)));
                seasonDao.create(season);
                seasons = seasonDao.queryForAll();
            }

        } catch (Exception e) {
            Bukkit.getLogger().severe("[HardcoreSeasons]: Failed to load seasons: " + e.getMessage());
        }
    }

    public Season getActiveSeason() {
        Season activeSeason = null;

        // Get season with the highest seasonId
        for (Season season : seasons) {
            if (activeSeason == null || season.getSeasonId() > activeSeason.getSeasonId()) {
                activeSeason = season;
            }
        }

        return activeSeason;
    }

    public void setActiveSeason(Season season) {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, jdbcUrl)) {
            Dao<Season, Integer> seasonDao = DaoManager.createDao(connectionSource, Season.class);
            seasonDao.create(season);
            seasons.add(season);
        } catch (Exception e) {
            Bukkit.getLogger().severe("[HardcoreSeasons]: Failed to set active season: " + e.getMessage());
        }
    }
}
