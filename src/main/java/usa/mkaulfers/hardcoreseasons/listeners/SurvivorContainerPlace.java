package usa.mkaulfers.hardcoreseasons.listeners;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import usa.mkaulfers.hardcoreseasons.models.SurvivorContainer;
import usa.mkaulfers.hardcoreseasons.storage.DatabaseManager;
import usa.mkaulfers.hardcoreseasons.utils.BlockUtils;

public class SurvivorContainerPlace implements Listener {
    DatabaseManager databaseManager;

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (BlockUtils.isTrackable(block)) {
            SurvivorContainer tc = new SurvivorContainer(block);
//            databaseManager.save(tc);
        }
    }

    public SurvivorContainerPlace(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }
}
