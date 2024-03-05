package us.mkaulfers.hardcoreseasons.models;

import java.sql.Timestamp;
import java.util.UUID;

public class Player implements Comparable<Player> {
    public int id;
    public UUID playerId;
    public int seasonId;
    public Timestamp joinDate;
    public Timestamp lastOnline;
    public boolean isDead;

    public Player(int id, UUID playerId, int seasonId, Timestamp joinDate, Timestamp lastOnline, boolean isDead) {
        this.id = id;
        this.playerId = playerId;
        this.seasonId = seasonId;
        this.joinDate = joinDate;
        this.lastOnline = lastOnline;
        this.isDead = isDead;
    }

    @Override
    public int compareTo(Player o) {
        return this.playerId.compareTo(o.playerId);
    }
}
