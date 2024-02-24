package usa.mkaulfers.hardcoreseasons.listeners;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import usa.mkaulfers.hardcoreseasons.storage.SQLHandler;
import usa.mkaulfers.hardcoreseasons.utils.BlockUtils;

public class TrackableBlockBroken implements Listener {
    SQLHandler sqlHandler;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (BlockUtils.isTrackable(event.getBlock())) {

            // TODO: Refactor this into a method
            int activeSeasonId = sqlHandler.getActiveSeason().seasonId;
            Block block = event.getBlock();
            Location location = block.getLocation();
            int x = location.getBlockX();
            int y = location.getBlockY();
            int z = location.getBlockZ();

            if (sqlHandler.isContainerTracked(activeSeasonId, x, y, z)) {
                event.getPlayer().sendMessage("You broke a tracked container!");
                sqlHandler.deleteContainer(activeSeasonId, x, y, z);
            }
        }
    }

    public TrackableBlockBroken(SQLHandler sqlHandler) {
        this.sqlHandler = sqlHandler;
    }
}
