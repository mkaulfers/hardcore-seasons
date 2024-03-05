package us.mkaulfers.hardcoreseasons.managers;

import org.bukkit.inventory.ItemStack;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.utils.InventoryUtils;

import java.util.ArrayList;
import java.util.List;

class WonSeason {
    public String seasonName;
    public int seasonId;

    public WonSeason(int seasonId) {
        this.seasonName = "Season " + seasonId;
        this.seasonId = seasonId;
    }
}


public class RewardManager {
    HardcoreSeasons plugin;

    public RewardManager(HardcoreSeasons plugin) {
        this.plugin = plugin;
    }

    public WonSeason[] getWonSeasons() {
        return new WonSeason[] {
                new WonSeason(1),
                new WonSeason(2),
                new WonSeason(3),
                new WonSeason(4),
                new WonSeason(5),
                new WonSeason(6),
                new WonSeason(7),
                new WonSeason(8),
                new WonSeason(9),
                new WonSeason( 10)
        };
    }

    public List<ItemStack> getRewards(int seasonId) {
        return new ArrayList<>();
    }
}
