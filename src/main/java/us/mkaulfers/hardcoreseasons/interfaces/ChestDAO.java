package us.mkaulfers.hardcoreseasons.interfaces;

import us.mkaulfers.hardcoreseasons.models.TrackedChest;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public interface ChestDAO extends DAO<TrackedChest>{
    CompletableFuture<TrackedChest> get(int x, int y, int z, String world, String type);
}
