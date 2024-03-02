package us.mkaulfers.hardcoreseasons.models;

import java.util.UUID;

public class SurvivorInventory implements  Comparable<SurvivorInventory> {
    public UUID playerUUID;
    public int seasonId;
    public String contents;

    @Override
    public int compareTo(SurvivorInventory o) {
        return this.playerUUID.compareTo(o.playerUUID);
    }

    public SurvivorInventory(UUID playerUUID, int seasonId, String contents) {
        this.playerUUID = playerUUID;
        this.seasonId = seasonId;
        this.contents = contents;
    }
}
