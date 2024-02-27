package usa.mkaulfers.hardcoreseasons.models;

import usa.mkaulfers.hardcoreseasons.interfaces.SQLManageable;

public class Session implements SQLManageable {
    Season activeSeason;
    Season[] history;


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
