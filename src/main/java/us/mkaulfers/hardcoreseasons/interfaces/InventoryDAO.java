package us.mkaulfers.hardcoreseasons.interfaces;

import us.mkaulfers.hardcoreseasons.models.SurvivorInventory;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface InventoryDAO extends DAO<SurvivorInventory>{
    CompletableFuture<List<SurvivorInventory>> getAll(int seasonId);
}
