package usa.mkaulfers.hardcoreseasons.models;

import org.bukkit.block.Block;
import org.bukkit.block.Container;
import usa.mkaulfers.hardcoreseasons.utils.InventoryUtils;

public class SurvivorContainer {
    public int seasonId;
    public int x;
    public int y;
    public int z;
    public String world;
    public String type;
    public String contents;

    public SurvivorContainer() {}

    public SurvivorContainer(Block block) {
        this.x = block.getX();
        this.y = block.getY();
        this.z = block.getZ();
        this.world = block.getWorld().getName();
        this.type = block.getType().toString();

        try {
            Container container = (Container) block.getState();
            this.contents = InventoryUtils.inventoryToBase64(container.getInventory());
        } catch (Exception e) {
            throw new IllegalArgumentException("Block is not a container");
        }
    }
}
