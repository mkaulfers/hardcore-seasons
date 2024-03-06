package us.mkaulfers.hardcoreseasons.models;

import java.util.UUID;

public class SeasonReward {
    private final int id;
    private final int seasonId;
    private final UUID playerId;
    private final String contents;
    private final boolean redeemed;

    public SeasonReward(int id, int seasonId, UUID playerId, String contents, boolean redeemed) {
        this.id = id;
        this.seasonId = seasonId;
        this.playerId = playerId;
        this.contents = contents;
        this.redeemed = redeemed;
    }

    public int getId() {
        return id;
    }

    public int getSeasonId() {
        return seasonId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public String getContents() {
        return contents;
    }
}
