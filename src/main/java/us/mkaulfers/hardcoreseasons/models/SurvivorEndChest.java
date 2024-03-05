package us.mkaulfers.hardcoreseasons.models;

import java.util.UUID;

public class SurvivorEndChest implements Comparable<SurvivorEndChest> {
    public int id;
    public int seasonId;
    public UUID playerId;
    public String contents;

    @Override
    public int compareTo(SurvivorEndChest o) {
        return this.playerId.compareTo(o.playerId);
    }

    public SurvivorEndChest(int id, UUID playerId, int activeSeason, String serializedEndChest) {
        this.id = id;
        this.seasonId = activeSeason;
        this.playerId = playerId;
        this.contents = serializedEndChest;
    }
}
