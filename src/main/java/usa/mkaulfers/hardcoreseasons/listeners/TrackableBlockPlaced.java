package usa.mkaulfers.hardcoreseasons.listeners;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.Inventory;
import usa.mkaulfers.hardcoreseasons.storage.SQLHandler;
import usa.mkaulfers.hardcoreseasons.utils.BlockUtils;
import usa.mkaulfers.hardcoreseasons.utils.InventoryUtils;
import java.io.IOException;

public class TrackableBlockPlaced implements Listener {
    SQLHandler sqlHandler;

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) throws IOException {
        if (BlockUtils.isTrackable(event.getBlock())) {

            // TODO: Refactor this into a method
            int activeSeasonId = sqlHandler.getActiveSeason().seasonId;
            Block block = event.getBlock();
            Location location = block.getLocation();
            int x = location.getBlockX();
            int y = location.getBlockY();
            int z = location.getBlockZ();

            Container container = (Container) event.getBlock().getState();
            Inventory inventory = container.getInventory();

            sqlHandler.insertContainer(activeSeasonId, x, y, z, InventoryUtils.inventoryToBase64(inventory));
        }
    }

    public TrackableBlockPlaced(SQLHandler sqlHandler) {
        this.sqlHandler = sqlHandler;
    }
}
