package com.xtwistedx.models;
import org.bukkit.block.Lockable;
import java.util.Date;

public class Season {
    int seasonId;
    Date startDate;
    Date endDate;
    Survivor[] survivors;
    Lockable[] trackedContainers;
}
