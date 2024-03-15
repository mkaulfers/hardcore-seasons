package us.mkaulfers.hardcoreseasons.listeners;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.utils.BlockUtils;

public class BlockBreak implements Listener {
    HardcoreSeasons plugin;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!plugin.configManager.config.trackingEnabled) {
            return;
        }


        Block block = event.getBlock();
        if (BlockUtils.isTrackable(block)) {
            Location loc = block.getLocation();
            plugin.hDataSource.deleteTrackedContainer(
                    plugin.currentSeasonNum,
                    loc.getBlockX(),
                    loc.getBlockY(),
                    loc.getBlockZ()
            );
        }
    }

    public BlockBreak(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }
}
