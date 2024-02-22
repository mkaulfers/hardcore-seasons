package com.xtwistedx.listeners;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {
    // We only care about placing Chests, Trapped Chests, Barrels, Shulker Boxes, and Ender Chests
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        // Your code here
    }
}
