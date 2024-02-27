package usa.mkaulfers.hardcoreseasons.models;

import usa.mkaulfers.hardcoreseasons.interfaces.SQLManageable;

import java.util.Date;
import java.util.UUID;

public class Survivor implements SQLManageable {
    UUID id;
    Date joinDate;
    Date lastLogin;
    boolean isDead;

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
