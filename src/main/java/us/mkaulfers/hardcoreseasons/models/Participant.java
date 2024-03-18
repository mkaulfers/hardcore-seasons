package us.mkaulfers.hardcoreseasons.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Timestamp;
import java.util.UUID;

@DatabaseTable(tableName = "participants")
public class Participant {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "player_id")
    private UUID playerId;

    @DatabaseField(columnName = "season_id")
    private int seasonId;

    @DatabaseField(columnName = "join_date")
    private Timestamp joinDate;

    @DatabaseField(columnName = "last_online")
    private Timestamp lastOnline;

    @DatabaseField(columnName = "last_world")
    private String lastWorld;

    @DatabaseField(columnName = "last_x")
    private int lastX;

    @DatabaseField(columnName = "last_y")
    private int lastY;

    @DatabaseField(columnName = "last_z")
    private int lastZ;

    @DatabaseField(columnName = "is_dead")
    private boolean isDead;

    public Participant() {}

    public int getId() {
        return id;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public int getSeasonId() {
        return seasonId;
    }

    public Timestamp getJoinDate() {
        return joinDate;
    }

    public Timestamp getLastOnline() {
        return lastOnline;
    }

    public String getLastWorld() {
        return lastWorld;
    }

    public int getLastX() {
        return lastX;
    }

    public int getLastY() {
        return lastY;
    }

    public int getLastZ() {
        return lastZ;
    }

    public boolean isDead() {
        return isDead;
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

    public void setJoinDate(Timestamp joinDate) {
        this.joinDate = joinDate;
    }

    public void setLastOnline(Timestamp lastOnline) {
        this.lastOnline = lastOnline;
    }

    public void setLastWorld(String lastWorld) {
        this.lastWorld = lastWorld;
    }

    public void setLastX(int lastX) {
        this.lastX = lastX;
    }

    public void setLastY(int lastY) {
        this.lastY = lastY;
    }

    public void setLastZ(int lastZ) {
        this.lastZ = lastZ;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }
}
