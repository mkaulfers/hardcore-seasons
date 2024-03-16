package us.mkaulfers.hardcoreseasons.orm;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.UUID;

@DatabaseTable(tableName = "end_chests")
public class HEndChest {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "season_id")
    private int seasonId;

    @DatabaseField(columnName = "player_id")
    private UUID playerId;

    @DatabaseField(dataType = DataType.LONG_STRING, columnName = "contents")
    private String contents;

    public HEndChest() {}

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

    public void setId(int id) {
        this.id = id;
    }

    public void setSeasonId(int seasonId) {
        this.seasonId = seasonId;
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}
