package us.mkaulfers.hardcoreseasons.models;

import java.util.UUID;

public class ResRequest {
    public final String playerName;
    public final UUID playerId;

    public ResRequest(String playerName, UUID playerId) {
        this.playerName = playerName;
        this.playerId = playerId;
    }
}
