package us.mkaulfers.hardcoreseasons.services;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import org.bukkit.Bukkit;
import us.mkaulfers.hardcoreseasons.models.Participant;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ParticipantService {
    public List<Participant> participants;
    private final DataSource ds;
    private final String jdbcUrl;

    public ParticipantService(DataSource ds, String jdbcUrl) {
        this.ds = ds;
        this.jdbcUrl = jdbcUrl;
        loadParticipants();
    }

    private void loadParticipants() {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, jdbcUrl)) {
            Dao<Participant, Integer> participantDao = DaoManager.createDao(connectionSource, Participant.class);
            participants = participantDao.queryForAll();
        } catch (Exception e) {
            Bukkit.getLogger().severe("[HardcoreSeasons]: Failed to load participants: " + e.getMessage());
        }
    }

    public Participant getParticipant(UUID uuid, int seasonId) {
        for (Participant participant : participants) {
            if (participant.getSeasonId() == seasonId && participant.getPlayerId().equals(uuid)) {
                return participant;
            }
        }

        return null;
    }

    public List<Participant> getParticipants(int seasonId) {
        List<Participant> filteredParticipants = new ArrayList<>();

        participants.forEach(participant -> {
            if (participant.getSeasonId() == seasonId) {
                filteredParticipants.add(participant);
            }
        });

        return filteredParticipants;
    }

    public void setParticipant(Participant participant) {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, jdbcUrl)) {
            Dao<Participant, Integer> participantDao = DaoManager.createDao(connectionSource, Participant.class);
            participantDao.create(participant);
            participants.add(participant);
        } catch (Exception e) {
            Bukkit.getLogger().severe("[HardcoreSeasons]: Failed to set participant: " + e.getMessage());
        }
    }

    public void updateParticipant(Participant participant) {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, jdbcUrl)) {
            Dao<Participant, Integer> participantDao = DaoManager.createDao(connectionSource, Participant.class);
            participantDao.update(participant);

            for (Participant p : participants) {
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
}
