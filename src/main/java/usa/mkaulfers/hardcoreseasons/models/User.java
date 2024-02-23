package usa.mkaulfers.hardcoreseasons.models;

import java.util.Date;
import java.util.UUID;

// Represents a player who has joined the Hardcore server
public class User {
    UUID uuid;
    String name;
    Date joinDate;
    Date lastLogin;

    public User(UUID uuid, String name, Date joinDate, Date lastLogin) {
        this.uuid = uuid;
        this.name = name;
        this.joinDate = joinDate;
        this.lastLogin = lastLogin;
    }
}
