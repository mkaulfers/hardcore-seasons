package us.mkaulfers.hardcoreseasons.interfaces;

import org.bukkit.block.Block;
import us.mkaulfers.hardcoreseasons.models.TrackedChest;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface ChestDAO extends DAO<TrackedChest>{
    CompletableFuture<TrackedChest> get(int x, int y, int z, String world, String type);
    CompletableFuture<List<TrackedChest>> getAll(int seasonId);
    CompletableFuture<Map<String, TrackedChest>> getAllForSeasonMap(int seasonId);
    CompletableFuture<Void> updateBatch(List<TrackedChest> chestsToUpdate);
}
