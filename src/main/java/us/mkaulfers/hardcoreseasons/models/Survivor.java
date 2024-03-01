package us.mkaulfers.hardcoreseasons.models;

import java.sql.Timestamp;
import java.util.UUID;

public class Survivor {
    public UUID id;
    public int seasonId;
    public Timestamp joinDate;
    public Timestamp lastOnline;
    public boolean isDead;
}
