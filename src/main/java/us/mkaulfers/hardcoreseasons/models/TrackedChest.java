package us.mkaulfers.hardcoreseasons.models;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import us.mkaulfers.hardcoreseasons.interfaceimpl.ChestDAOImpl;
import us.mkaulfers.hardcoreseasons.interfaces.ChestDAO;
import us.mkaulfers.hardcoreseasons.interfaces.RewardSource;
import us.mkaulfers.hardcoreseasons.utils.InventoryUtils;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TrackedChest implements Comparable<TrackedChest>, RewardSource {
    public int id;
    public int seasonId;
    public int x;
    public int y;
    public int z;
    public String world;
    public String type;
    public String contents;

    public TrackedChest(int id, int seasonId, int x, int y, int z, String world, String type, String contents) {
        this.id = id;
        this.seasonId = seasonId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.type = type;
        this.contents = contents;
    }

    public TrackedChest(Block block, int seasonId) {
        this.seasonId = seasonId;
        this.x = block.getX();
        this.y = block.getY();
        this.z = block.getZ();
        this.world = block.getWorld().getName();
        this.type = block.getType().toString();

        try {
            Container container = (Container) block.getState();
            Inventory inventory = container.getInventory();
            ItemStack[] contents = inventory.getContents();
            this.contents = InventoryUtils.itemStackArrayToBase64(contents);
        } catch (Exception e) {
            throw new IllegalArgumentException("Block is not a container");
        }
    }

    @Override
    public int compareTo(TrackedChest o) {
        int diff = Integer.compare(this.seasonId, o.seasonId);
        if (diff != 0) {
            return diff;
        }
        diff = Integer.compare(this.x, o.x);
        if (diff != 0) {
            return diff;
        }
        diff = Integer.compare(this.y, o.y);
        if (diff != 0) {
            return diff;
        }
        diff = Integer.compare(this.z, o.z);
        if (diff != 0) {
            return diff;
        }
        diff = this.world.compareTo(o.world);
        if (diff != 0) {
            return diff;
        }
        diff = this.type.compareTo(o.type);
        if (diff != 0) {
            return diff;
        }
        return this.contents.compareTo(o.contents);
    }

    @Override
    public String getContents() {
        return contents;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public CompletableFuture<List<ItemStack>> redeemReward(Database db) {
        return CompletableFuture.supplyAsync(() -> {
            List<ItemStack> items = new ArrayList<>();
            ChestDAO chestDAO = new ChestDAOImpl(db);

            try {
                TrackedChest chest = chestDAO.get(id).get();
                items = List.of(InventoryUtils.itemStackArrayFromBase64(chest.contents));
                chestDAO.delete(chest);
                return items;
            } catch (Exception e) {
                Bukkit.getLogger().severe("[Hardcore Seasons]: Failed to redeem tracked chest reward. " + e.getMessage());
            }

            return items;
        });
    }

    @Override
    public CompletableFuture<Boolean> updateRewardContents(List<ItemStack> items, Database db) {
        return CompletableFuture.supplyAsync(() -> {


            return null;
        });
    }
}
