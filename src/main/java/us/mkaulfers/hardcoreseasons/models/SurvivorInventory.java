package us.mkaulfers.hardcoreseasons.models;

import java.util.UUID;

public class SurvivorInventory implements  Comparable<SurvivorInventory> {
    public int id;
    public int seasonId;
    public UUID playerId;
    public String contents;

    @Override
    public int compareTo(SurvivorInventory o) {
        return this.playerId.compareTo(o.playerId);
    }

    public SurvivorInventory(int id, UUID playerId, int seasonId, String contents) {
        this.id = id;
        this.seasonId = seasonId;
        this.playerId = playerId;
        this.contents = contents;
    }
}
