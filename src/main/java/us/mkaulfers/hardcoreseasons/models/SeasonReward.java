package us.mkaulfers.hardcoreseasons.models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.UUID;

@DatabaseTable(tableName = "season_rewards")
public class SeasonReward {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "player_id")
    private UUID playerId;

    @DatabaseField(columnName = "season_id")
    private int seasonId;

    @DatabaseField(dataType = DataType.LONG_STRING, columnName = "contents")
    private String contents;

    public SeasonReward() {}

    public int getId() {
        return id;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public int getSeasonId() {
        return seasonId;
    }

    public String getContents() {
        return contents;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }

    public void setSeasonId(int seasonId) {
        this.seasonId = seasonId;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}
