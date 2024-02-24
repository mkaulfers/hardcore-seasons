package usa.mkaulfers.hardcoreseasons.listeners;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import usa.mkaulfers.hardcoreseasons.storage.SQLHandler;
import usa.mkaulfers.hardcoreseasons.utils.InventoryUtils;

import javax.sound.midi.Track;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;

public class TrackableBlockPlaced implements Listener {
    // We only care about placing Chests, Trapped Chests, Barrels, Shulker Boxes, and Ender Chests
    SQLHandler sqlHandler;

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) throws IOException {
        if (isTrackable(event.getBlock())) {
            event.getPlayer().sendMessage("You placed a trackable block!");
        }

        if (event.getBlock().getState() instanceof Container) {
            Container container = (Container) event.getBlock().getState();
            Inventory inventory = container.getInventory();

            sqlHandler.insertContainer(container.getBlock().getLocation().getBlockX(),
                    container.getBlock().getLocation().getBlockY(),
                    container.getBlock().getLocation().getBlockZ(),
                    InventoryUtils.inventoryToBase64(inventory)
            );
        }
    }

    private boolean isTrackable(Block block) {
        Material type = block.getType();
        return type == Material.CHEST ||
                type == Material.TRAPPED_CHEST ||
                type == Material.BARREL ||
                type == Material.SHULKER_BOX ||
                type == Material.FURNACE ||
                type == Material.BLAST_FURNACE ||
                type == Material.SMOKER;
    }

    public TrackableBlockPlaced(SQLHandler sqlHandler) {
        this.sqlHandler = sqlHandler;
    }
}
