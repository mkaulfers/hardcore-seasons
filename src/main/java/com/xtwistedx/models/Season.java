package com.xtwistedx.models;
import org.bukkit.block.Container;
import java.util.Date;

public class Season {
    int seasonId;
    Date startDate;
    Date endDate;
    User[] users;
    Container[] trackedContainers;
}
