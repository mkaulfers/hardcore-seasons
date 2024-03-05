package us.mkaulfers.hardcoreseasons.interfaces;

import us.mkaulfers.hardcoreseasons.models.TrackedChest;
import us.mkaulfers.hardcoreseasons.models.TrackedEndChest;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface EndChestDAO extends DAO<TrackedEndChest>{
    CompletableFuture<List<TrackedEndChest>> getAll(int seasonId);
}
