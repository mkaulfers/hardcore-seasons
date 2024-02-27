package usa.mkaulfers.hardcoreseasons.listeners;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import usa.mkaulfers.hardcoreseasons.models.SurvivorContainer;
import usa.mkaulfers.hardcoreseasons.storage.DBManager;
import usa.mkaulfers.hardcoreseasons.utils.BlockUtils;

public class SurvivorContainerPlace implements Listener {
    DBManager dbManager;

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (BlockUtils.isTrackable(block)) {
            SurvivorContainer tc = new SurvivorContainer(block);
            dbManager.save(tc);
        }
    }

    public SurvivorContainerPlace(DBManager dbManager) {
        this.dbManager = dbManager;
    }
}
