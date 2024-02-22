package com.xtwistedx.models;

import java.util.Date;
import java.util.UUID;

// Represents a player who has joined the Hardcore server
public class Survivor {
    UUID uuid;
    String name;
    Date joinDate;
    Date lastLogin;

    public Survivor(UUID uuid, String name, Date joinDate, Date lastLogin) {
        this.uuid = uuid;
        this.name = name;
        this.joinDate = joinDate;
        this.lastLogin = lastLogin;
    }
}
