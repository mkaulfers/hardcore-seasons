package us.mkaulfers.hardcoreseasons.models;

import java.util.UUID;

public class SurvivorEndChest implements Comparable<SurvivorEndChest> {
    public UUID playerUUID;
    public int seasonId;
    public String contents;

    @Override
    public int compareTo(SurvivorEndChest o) {
        return this.playerUUID.compareTo(o.playerUUID);
    }

    public SurvivorEndChest(UUID playerId, int activeSeason, String serializedEndChest) {
        this.playerUUID = playerId;
        this.seasonId = activeSeason;
        this.contents = serializedEndChest;
    }
}
