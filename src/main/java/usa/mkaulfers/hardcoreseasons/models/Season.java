package usa.mkaulfers.hardcoreseasons.models;

import usa.mkaulfers.hardcoreseasons.interfaces.SQLManageable;

import java.util.Date;

public class Season implements SQLManageable {
    Date startDate;
    Date endDate;
    Survivor[] survivors;
    SurvivorContainer[] containers;
    SurvivorInventory[] inventories;
    SurvivorEndChest[] endChests;

    @Override
    public String saveQuery() {
        return null;
    }

    @Override
    public String deleteQuery() {
        return null;
    }

    @Override
    public String updateQuery() {
        return null;
    }

    @Override
    public String loadQuery() {
        return null;
    }
}
