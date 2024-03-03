package us.mkaulfers.hardcoreseasons.models;

import java.sql.Timestamp;
import java.util.UUID;

public class Survivor implements Comparable<Survivor> {
    public UUID id;
    public int seasonId;
    public Timestamp joinDate;
    public Timestamp lastOnline;
    public boolean isDead;

    public Survivor(UUID id, int seasonId, Timestamp joinDate, Timestamp lastOnline, boolean isDead) {
        this.id = id;
        this.seasonId = seasonId;
        this.joinDate = joinDate;
        this.lastOnline = lastOnline;
        this.isDead = isDead;
    }

    @Override
    public int compareTo(Survivor o) {
        return this.id.compareTo(o.id);
    }
}
