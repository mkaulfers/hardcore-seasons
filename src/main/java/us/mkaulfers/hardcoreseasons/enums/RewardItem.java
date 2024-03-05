package us.mkaulfers.hardcoreseasons.enums;

import org.bukkit.inventory.ItemStack;
import us.mkaulfers.hardcoreseasons.models.RewardItemType;

public class RewardItem {
    private final ItemStack item;
    private final int id;
    private final RewardItemType type;

    public RewardItem(ItemStack item, int id, RewardItemType type) {
        this.item = item;
        this.id = id;
        this.type = type;
    }

    public ItemStack getItem() {
        return item;
    }

    public int getId() {
        return id;
    }

    public RewardItemType getType() {
        return type;
    }
}

