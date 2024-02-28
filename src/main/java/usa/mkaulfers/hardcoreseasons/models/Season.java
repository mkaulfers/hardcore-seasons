package usa.mkaulfers.hardcoreseasons.models;

import java.util.Date;

public class Season {
    public Date startDate;
    public Date endDate;
    Survivor[] survivors;
    SurvivorContainer[] containers;
    SurvivorInventory[] inventories;
    SurvivorEndChest[] endChests;
}
