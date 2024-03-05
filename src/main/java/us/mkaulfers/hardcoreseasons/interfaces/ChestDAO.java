package us.mkaulfers.hardcoreseasons.interfaces;

import us.mkaulfers.hardcoreseasons.models.TrackedChest;

import java.sql.SQLException;

public interface ChestDAO extends DAO<TrackedChest>{
    TrackedChest get(int x, int y, int z, String world, String type) throws SQLException;
}
