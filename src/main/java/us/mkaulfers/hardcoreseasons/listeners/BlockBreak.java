package us.mkaulfers.hardcoreseasons.listeners;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.interfaceimpl.ChestDAOImpl;
import us.mkaulfers.hardcoreseasons.interfaces.ChestDAO;
import us.mkaulfers.hardcoreseasons.models.TrackedChest;
import us.mkaulfers.hardcoreseasons.utils.BlockUtils;

public class BlockBreak implements Listener {
    HardcoreSeasons plugin;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (BlockUtils.isTrackable(block)) {
            ChestDAO chestDAO = new ChestDAOImpl(plugin.database);
            TrackedChest trackedChest = new TrackedChest(block, plugin.currentSeasonNum);
            chestDAO.delete(trackedChest);
        }
    }

    public BlockBreak(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }
}
