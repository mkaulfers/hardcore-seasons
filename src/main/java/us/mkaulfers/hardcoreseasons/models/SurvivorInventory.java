package us.mkaulfers.hardcoreseasons.models;

import java.util.UUID;

public class SurvivorInventory {
    public UUID playerUUID;
    public int seasonId;
    public String contents;

    public SurvivorInventory() {}
    public SurvivorInventory(UUID playerUUID, int seasonId, String contents) {
        this.playerUUID = playerUUID;
        this.seasonId = seasonId;
        this.contents = contents;
    }
}
