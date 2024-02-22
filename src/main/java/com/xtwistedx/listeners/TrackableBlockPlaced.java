package com.xtwistedx.listeners;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class TrackableBlockPlaced implements Listener {
    // We only care about placing Chests, Trapped Chests, Barrels, Shulker Boxes, and Ender Chests
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (isTrackable(event.getBlock())) {
            event.getPlayer().sendMessage("You placed a trackable block!");
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
}
