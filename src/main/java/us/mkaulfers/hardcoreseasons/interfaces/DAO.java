package us.mkaulfers.hardcoreseasons.interfaces;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface DAO<T> {
    CompletableFuture<T> get(int id);
    CompletableFuture<List<T>> getAll();
    CompletableFuture<Integer> save(T t);
    CompletableFuture<Integer> insert(T t);
    CompletableFuture<Integer> update(T t);
    CompletableFuture<Integer> delete(T t);
}
