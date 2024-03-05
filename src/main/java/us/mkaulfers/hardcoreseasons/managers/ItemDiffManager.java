package us.mkaulfers.hardcoreseasons.managers;

import org.bukkit.inventory.ItemStack;
import us.mkaulfers.hardcoreseasons.utils.InventoryUtils;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class ItemDiffManager {
    private final ReentrantLock lock = new ReentrantLock();
    private List<ItemStack> allItems;
    private List<ItemStack> guiItems;

    public ItemDiffManager(List<ItemStack> allItems, List<ItemStack> guiItems) {
        setAllItems(allItems);
        setGuiItems(guiItems);
    }

    public List<ItemStack> getAllItems() {
        lock.lock();
        try {
            return allItems;
        } finally {
            lock.unlock();
        }
    }

    public void setAllItems(List<ItemStack> allItems) {
        lock.lock();
        try {
            this.allItems = allItems;
        } finally {
            lock.unlock();
        }
    }

    public List<ItemStack> getGuiItems() {
        lock.lock();
        try {
            return guiItems;
        } finally {
            lock.unlock();
        }
    }

    public void setGuiItems(List<ItemStack> guiItems) {
        lock.lock();
        try {
            this.guiItems = guiItems;
        } finally {
            lock.unlock();
        }
    }

    public List<List<ItemStack>> fetchItemsOfType(ItemStack item) {
        lock.lock();
        try {
            List<List<ItemStack>> separatedItems = InventoryUtils.getItemsOfType(allItems, item.getType());
            List<ItemStack> fetchedItems = separatedItems.get(0);
            List<ItemStack> remainingItems = separatedItems.get(1);
            guiItems = InventoryUtils.getGUIItemsList(remainingItems);
            return separatedItems;
        } finally {
            lock.unlock();
        }
    }
}
