package us.mkaulfers.hardcoreseasons.listeners;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.models.SurvivorContainer;
import us.mkaulfers.hardcoreseasons.storage.DatabaseManager;
import us.mkaulfers.hardcoreseasons.utils.BlockUtils;

public class SurvivorContainerPlace implements Listener {
    HardcoreSeasons plugin;

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (BlockUtils.isTrackable(block)) {
            SurvivorContainer tc = new SurvivorContainer(block);
//            databaseManager.save(tc);
        }
    }

    public SurvivorContainerPlace(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }
}