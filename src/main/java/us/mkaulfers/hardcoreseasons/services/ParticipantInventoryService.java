package us.mkaulfers.hardcoreseasons.services;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.support.ConnectionSource;
import org.bukkit.Bukkit;
import us.mkaulfers.hardcoreseasons.models.ParticipantInventory;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ParticipantInventoryService {
    public List<ParticipantInventory> inventories;
    private final DataSource ds;
    private final String jdbcUrl;
    
    public ParticipantInventoryService(DataSource ds, String jdbcUrl) {
        this.ds = ds;
        this.jdbcUrl = jdbcUrl;
        loadInventories();
    }

    private void loadInventories() {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, jdbcUrl)) {
            Dao<ParticipantInventory, Integer> inventoryDao = DaoManager.createDao(connectionSource, ParticipantInventory.class);
            inventories = inventoryDao.queryForAll();
        } catch (Exception e) {
            Bukkit.getLogger().severe("[HardcoreSeasons]: Failed to load inventories: " + e.getMessage());
        }
    }

    public ParticipantInventory getInventory(UUID playerId, int seasonId) {
        for (ParticipantInventory inventory : inventories) {
            if (inventory.getSeasonId() == seasonId && inventory.getPlayerId().equals(playerId)) {
                return inventory;
            }
        }

        return null;
    }

    public List<ParticipantInventory> getInventories(int seasonId) {
        List<ParticipantInventory> filteredInventories = new ArrayList<>();

        inventories.forEach(inventory -> {
            if (inventory.getSeasonId() == seasonId) {
                filteredInventories.add(inventory);
            }
        });

        return filteredInventories;
    }

    public void setInventory(ParticipantInventory inventory) {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, jdbcUrl)) {
            Dao<ParticipantInventory, Integer> inventoryDao = DaoManager.createDao(connectionSource, ParticipantInventory.class);
            inventoryDao.create(inventory);
            inventories.add(inventory);
        } catch (Exception e) {
            Bukkit.getLogger().severe("[HardcoreSeasons]: Failed to set inventory: " + e.getMessage());
        }
    }

    public void updateInventory(ParticipantInventory inventory) {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, jdbcUrl)) {
            Dao<ParticipantInventory, Integer> inventoryDao = DaoManager.createDao(connectionSource, ParticipantInventory.class);
            inventoryDao.update(inventory);

            for (ParticipantInventory inv : inventories) {
                if (inv.getId() == inventory.getId()) {
                    inv.setId(inventory.getId());
                    inv.setSeasonId(inventory.getSeasonId());
                    inv.setPlayerId(inventory.getPlayerId());
                    inv.setContents(inventory.getContents());
                    break;
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().severe("[HardcoreSeasons]: Failed to update inventory: " + e.getMessage());
        }
    }

    public void deleteInventories(int seasonId) {
        try (ConnectionSource connectionSource = new DataSourceConnectionSource(ds, jdbcUrl)) {
            Dao<ParticipantInventory, Integer> inventoryDao = DaoManager.createDao(connectionSource, ParticipantInventory.class);

            DeleteBuilder<ParticipantInventory, Integer> deleteBuilder = inventoryDao.deleteBuilder();

            deleteBuilder.where()
                    .eq("season_id", seasonId);

            deleteBuilder.delete();

            inventories.removeIf(inventory -> inventory.getSeasonId() == seasonId);
        } catch (Exception e) {
            Bukkit.getLogger().severe("[HardcoreSeasons]: Failed to delete inventories: " + e.getMessage());
        }
    }
}
