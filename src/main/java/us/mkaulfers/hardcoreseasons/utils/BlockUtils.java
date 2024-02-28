package us.mkaulfers.hardcoreseasons.utils;

import org.bukkit.Material;
import org.bukkit.block.Block;

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
}
