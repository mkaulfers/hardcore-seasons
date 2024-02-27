package usa.mkaulfers.hardcoreseasons.storage;

import org.bukkit.block.Block;
import org.bukkit.block.Container;
import usa.mkaulfers.hardcoreseasons.interfaces.SQLManageable;
import usa.mkaulfers.hardcoreseasons.storage.DBManager;
import usa.mkaulfers.hardcoreseasons.utils.InventoryUtils;

public class TrackedContainer implements SQLManageable {
    public int containerId;
    public int seasonId;
    public int x;
    public int y;
    public int z;
    public String world;
    public String type;
    public String contents;

    public TrackedContainer(Block block) {
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

    @Override
    public String saveQuery() {
        return "INSERT INTO `tracked_containers` (`season_id`, `container_x`, `container_y`, `container_z`, `container_contents`) " +
                "VALUES (" + seasonId + ", " + x + ", " + y + ", " + z + ", '" + contents + "')";
    }

    @Override
    public String deleteQuery() {
        return "DELETE FROM `tracked_containers` WHERE `season_id` = " + seasonId +
                " AND `container_x` = " + x + " AND `container_y` = " + y + " AND `container_z` = " + z;
    }

    @Override
    public String updateQuery() {
        return "UPDATE `tracked_containers` SET `container_contents` = '" + contents + "' WHERE `season_id` = " + seasonId +
                " AND `container_x` = " + x + " AND `container_y` = " + y + " AND `container_z` = " + z;
    }

    @Override
    public String loadQuery() {
        return "SELECT * FROM `tracked_containers` WHERE `season_id` = " + seasonId +
                " AND `container_x` = " + x + " AND `container_y` = " + y + " AND `container_z` = " + z;
    }
}
