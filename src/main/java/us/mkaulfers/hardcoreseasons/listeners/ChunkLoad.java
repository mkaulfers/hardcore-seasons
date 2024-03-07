package us.mkaulfers.hardcoreseasons.listeners;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.interfaceimpl.ChestDAOImpl;
import us.mkaulfers.hardcoreseasons.interfaces.ChestDAO;
import us.mkaulfers.hardcoreseasons.models.TrackedChest;
import us.mkaulfers.hardcoreseasons.utils.BlockUtils;
import us.mkaulfers.hardcoreseasons.utils.InventoryUtils;

import javax.sound.midi.Track;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ChunkLoad implements Listener {
    HardcoreSeasons plugin;

    public ChunkLoad(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        updateChestsInChunk(event);
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        updateChestsInChunk(event);
    }

    private void updateChestsInChunk(ChunkEvent event) {
        // execute asynchronous job
        ChestDAO chestDAO = new ChestDAOImpl(plugin.database);

        // Get the tracked chests in form of map
        CompletableFuture<Map<String, TrackedChest>> future = chestDAO.getAllForSeasonMap(plugin.activeSeason);

        // Wait for future's completion, make sure you are not on main thread here
        Map<String, TrackedChest> trackedChests = future.join();

        List<TrackedChest> chestsToUpdate = new ArrayList<>();

        BlockState[] tileEntities = event.getChunk().getTileEntities();

        for (BlockState tileEntity : tileEntities) {
            if (BlockUtils.isTrackable(tileEntity.getBlock())) {
                Block chest = tileEntity.getBlock();
                Container blockState = (Container) chest.getState();

                TrackedChest trackedChest = trackedChests.get(
                        chest.getWorld().getName() + ":"
                                + chest.getX() + ":"
                                + chest.getY() + ":"
                                + chest.getZ()
                );

                if (trackedChest != null) {
                    trackedChest.contents = InventoryUtils.itemStackArrayToBase64(blockState.getInventory().getContents());
                    chestsToUpdate.add(trackedChest);
                }
            }
        }

        if (!chestsToUpdate.isEmpty()) {
            chestDAO.updateBatch(chestsToUpdate).join();
        }
    }
}
