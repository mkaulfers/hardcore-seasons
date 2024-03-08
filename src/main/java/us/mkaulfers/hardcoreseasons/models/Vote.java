package us.mkaulfers.hardcoreseasons.models;

import java.util.Date;

public class Vote {
    public int id;
    public int seasonId;
    public String playerName;
    public Date lastNotified;
    public boolean shouldEndSeason;

    public Vote(int id, int seasonId, String playerName, Date lastNotified, boolean shouldEndSeason) {
        this.id = id;
        this.seasonId = seasonId;
        this.playerName = playerName;
        this.lastNotified = lastNotified;
        this.shouldEndSeason = shouldEndSeason;
    }
}
