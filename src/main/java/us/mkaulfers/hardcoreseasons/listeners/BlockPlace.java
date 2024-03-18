package us.mkaulfers.hardcoreseasons.listeners;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.models.TrackedContainer;
import us.mkaulfers.hardcoreseasons.utils.BlockUtils;

public class BlockPlace implements Listener {
    HardcoreSeasons plugin;

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!plugin.configManager.config.trackingEnabled) {
            return;
        }

        Block block = event.getBlock();
        if (BlockUtils.isTrackable(block)) {
            TrackedContainer trackedContainer = new TrackedContainer();
            trackedContainer.setSeasonId(plugin.activeSeason.getSeasonId());
            trackedContainer.setPosX(block.getX());
            trackedContainer.setPosY(block.getY());
            trackedContainer.setPosZ(block.getZ());
            trackedContainer.setWorld(block.getWorld().getName());
            trackedContainer.setType(block.getType().name());
            trackedContainer.setContents("");
            plugin.db.containers.setTrackedContainer(trackedContainer);
        }
    }

    public BlockPlace(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }
}
