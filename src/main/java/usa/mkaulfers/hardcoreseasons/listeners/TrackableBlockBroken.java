package usa.mkaulfers.hardcoreseasons.listeners;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import usa.mkaulfers.hardcoreseasons.storage.TrackedContainer;
import usa.mkaulfers.hardcoreseasons.storage.DBManager;
import usa.mkaulfers.hardcoreseasons.utils.BlockUtils;

public class TrackableBlockBroken implements Listener {
    DBManager dbManager;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (BlockUtils.isTrackable(block)) {
            TrackedContainer tc = new TrackedContainer(block);
            dbManager.delete(tc);
        }
    }

    public TrackableBlockBroken(DBManager dbManager) {
        this.dbManager = dbManager;
    }
}
