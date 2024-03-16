package us.mkaulfers.hardcoreseasons.utils;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.inventory.InventoryType;

public class BlockUtils {
    public static boolean isTrackable(Block block) {
        Material type = block.getType();
        return type == Material.CHEST ||
                type == Material.TRAPPED_CHEST ||
                type == Material.BARREL ||
                type == Material.SHULKER_BOX ||
                type == Material.FURNACE ||
                type == Material.BLAST_FURNACE ||
                type == Material.SMOKER;
    }

    public static boolean isTrackable(InventoryType type) {
        return type == InventoryType.CHEST ||
                type == InventoryType.BARREL ||
                type == InventoryType.SHULKER_BOX ||
                type == InventoryType.FURNACE ||
                type == InventoryType.BLAST_FURNACE ||
                type == InventoryType.SMOKER;
    }
}
