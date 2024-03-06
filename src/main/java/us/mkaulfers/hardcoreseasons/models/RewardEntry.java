package us.mkaulfers.hardcoreseasons.models;

import org.bukkit.inventory.ItemStack;
import us.mkaulfers.hardcoreseasons.interfaces.RewardSource;

public class RewardEntry {
    private final ItemStack guiItem;
    private final RewardSource rewardSource;

    public RewardEntry(ItemStack guiItem, RewardSource rewardSource) {
        this.guiItem = guiItem;
        this.rewardSource = rewardSource;
    }

    public ItemStack getGuiItem() {
        return guiItem;
    }

    public RewardSource getRewardSource() {
        return rewardSource;
    }
}
