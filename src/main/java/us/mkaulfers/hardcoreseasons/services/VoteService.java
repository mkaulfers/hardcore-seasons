package us.mkaulfers.hardcoreseasons.services;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import org.bukkit.Bukkit;
import us.mkaulfers.hardcoreseasons.models.Vote;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VoteService {
    public List<Vote> votes;
    private final DataSource ds;
    private final String jdbcUrl;

    public VoteService(DataSource ds, String jdbcUrl) {
        this.ds = ds;
        this.jdbcUrl = jdbcUrl;
        loadVotes();
    }

    private void loadVotes() {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, jdbcUrl)) {
            Dao<Vote, Integer> votesDao = DaoManager.createDao(connectionSource, Vote.class);
            votes = votesDao.queryForAll();
        } catch (Exception e) {
            Bukkit.getLogger().severe("[HardcoreSeasons]: Failed to load votes: " + e.getMessage());
        }
    }

    public Vote getVote(UUID playerId, int seasonId) {
        for (Vote vote : votes) {
            if (vote.getSeasonId() == seasonId && vote.getPlayerId().equals(playerId)) {
                return vote;
            }
        }

        return null;
    }

    public List<Vote> getVotes(int seasonId) {
        List<Vote> filteredVotes = new ArrayList<>();

        votes.forEach(vote -> {
            if (vote.getSeasonId() == seasonId) {
                filteredVotes.add(vote);
            }
        });

        return filteredVotes;
    }

    public boolean setVote(Vote vote) {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, jdbcUrl)) {
            Dao<Vote, Integer> votesDao = DaoManager.createDao(connectionSource, Vote.class);
            votesDao.create(vote);
            votes.add(vote);
            return true;
        } catch (Exception e) {
            Bukkit.getLogger().severe("[HardcoreSeasons]: Failed to set vote: " + e.getMessage());
            return false;
        }
    }
}
