package usa.mkaulfers.hardcoreseasons.listeners;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import usa.mkaulfers.hardcoreseasons.models.SurvivorContainer;
import usa.mkaulfers.hardcoreseasons.storage.DatabaseManager;
import usa.mkaulfers.hardcoreseasons.utils.BlockUtils;

public class SurvivorContainerBreak implements Listener {
    DatabaseManager databaseManager;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (BlockUtils.isTrackable(block)) {
            SurvivorContainer tc = new SurvivorContainer(block);
//            databaseManager.delete(tc);
        }
    }

    public SurvivorContainerBreak(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }
}
