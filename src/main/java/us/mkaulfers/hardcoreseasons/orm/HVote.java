package us.mkaulfers.hardcoreseasons.orm;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Timestamp;
import java.util.UUID;

@DatabaseTable(tableName = "votes")
public class HVote {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "season_id")
    private int seasonId;

    @DatabaseField(columnName = "player_id")
    private UUID playerId;

    @DatabaseField(columnName = "date_last_voted")
    private Timestamp dateLastVoted;

    @DatabaseField(columnName = "should_end_season")
    private boolean shouldEndSeason;

    public HVote() {}

    public int getId() {
        return id;
    }

    public int getSeasonId() {
        return seasonId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public Timestamp getDateLastVoted() {
        return dateLastVoted;
    }

    public boolean isShouldEndSeason() {
        return shouldEndSeason;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSeasonId(int seasonId) {
        this.seasonId = seasonId;
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }

    public void setDateLastVoted(Timestamp dateLastVoted) {
        this.dateLastVoted = dateLastVoted;
    }

    public void setShouldEndSeason(boolean shouldEndSeason) {
        this.shouldEndSeason = shouldEndSeason;
    }
}
