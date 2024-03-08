package us.mkaulfers.hardcoreseasons.interfaceimpl;

import us.mkaulfers.hardcoreseasons.interfaces.VoteDAO;
import us.mkaulfers.hardcoreseasons.models.Vote;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class VoteDAOImpl implements VoteDAO {
    @Override
    public CompletableFuture<Vote> get(int id) {
        return null;
    }

    @Override
    public CompletableFuture<List<Vote>> getAll() {
        return null;
    }

    @Override
    public CompletableFuture<Integer> save(Vote vote) {

        return null;
    }

    @Override
    public CompletableFuture<Integer> insert(Vote vote) {
        return null;
    }

    @Override
    public CompletableFuture<Integer> update(Vote vote) {

        return null;
    }

    @Override
    public CompletableFuture<Integer> delete(Vote vote) {

        return null;
    }
}
