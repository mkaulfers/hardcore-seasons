package us.mkaulfers.hardcoreseasons.listeners;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.interfaceimpl.ChestDAOImpl;
import us.mkaulfers.hardcoreseasons.interfaces.ChestDAO;
import us.mkaulfers.hardcoreseasons.models.TrackedChest;
import us.mkaulfers.hardcoreseasons.utils.BlockUtils;

public class BlockPlace implements Listener {
    HardcoreSeasons plugin;

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (BlockUtils.isTrackable(block)) {
            ChestDAO chestDAO = new ChestDAOImpl(plugin.database);
            TrackedChest trackedChest = new TrackedChest(block, plugin.currentSeasonNum);
            chestDAO.save(trackedChest);
        }
    }

    public BlockPlace(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }
}
