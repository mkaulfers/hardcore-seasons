package us.mkaulfers.hardcoreseasons.models;

import java.sql.Date;
import java.util.UUID;

public class Vote {
    public int id;
    public int seasonId;
    public UUID playerId;
    public Date dateLastVoted;
    public boolean shouldEndSeason;

    public Vote(int id, int seasonId, UUID playerId, Date dateLastVoted, boolean shouldEndSeason) {
        this.id = id;
        this.seasonId = seasonId;
        this.playerId = playerId;
        this.dateLastVoted = dateLastVoted;
        this.shouldEndSeason = shouldEndSeason;
    }
}
