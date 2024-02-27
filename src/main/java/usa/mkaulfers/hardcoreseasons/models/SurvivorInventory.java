package usa.mkaulfers.hardcoreseasons.models;

import usa.mkaulfers.hardcoreseasons.interfaces.SQLManageable;

import java.util.UUID;

public class SurvivorInventory implements SQLManageable {
    UUID playerUUID;
    String contents;

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
