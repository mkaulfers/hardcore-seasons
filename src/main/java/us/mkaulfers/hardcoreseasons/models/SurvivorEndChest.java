package us.mkaulfers.hardcoreseasons.models;

import java.util.UUID;

public class SurvivorEndChest {
    public UUID playerUUID;
    public int seasonId;
    public String contents;

    public SurvivorEndChest(UUID playerId, int activeSeason, String serializedEndChest) {
        this.playerUUID = playerId;
        this.seasonId = activeSeason;
        this.contents = serializedEndChest;
    }
}
