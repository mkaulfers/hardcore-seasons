package us.mkaulfers.hardcoreseasons.listeners;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.models.SurvivorContainer;
import us.mkaulfers.hardcoreseasons.utils.BlockUtils;

public class BlockPlace implements Listener {
    HardcoreSeasons plugin;

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (BlockUtils.isTrackable(block)) {
            int activeSeason = plugin.databaseManager.seasonsManager.getActiveSeason().seasonId;
            SurvivorContainer survivorContainer = new SurvivorContainer(block, activeSeason);
            plugin.databaseManager.containersManager.saveContainer(survivorContainer);
        }
    }

    public BlockPlace(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }
}
