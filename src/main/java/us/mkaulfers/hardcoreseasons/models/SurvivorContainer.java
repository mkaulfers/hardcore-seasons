package us.mkaulfers.hardcoreseasons.models;

import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import us.mkaulfers.hardcoreseasons.utils.InventoryUtils;

public class SurvivorContainer {
    public int seasonId;
    public int x;
    public int y;
    public int z;
    public String world;
    public String type;
    public String contents;

    public SurvivorContainer(int seasonId, int x, int y, int z, String world, String type, String contents) {
        this.seasonId = seasonId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.type = type;
        this.contents = contents;
    }

    public SurvivorContainer(Block block, int seasonId) {
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
}
