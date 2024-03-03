package us.mkaulfers.hardcoreseasons.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class InventoryUtils {
    /**
     * Converts the player inventory to a Base64 encoded string.
     *
     * @param playerInventory to turn into an array of strings.
     * @return string with serialized Inventory
     * @throws IllegalStateException
     */
    public static String playerInventoryToBase64(PlayerInventory playerInventory) throws IllegalStateException {
        // This contains contents, armor and offhand (contents are indexes 0 - 35, armor 36 - 39, offhand - 40)
        return itemStackArrayToBase64(playerInventory.getContents());
    }

    /**
     * A method to serialize an {@link ItemStack} array to Base64 String.
     *
     * @param items to turn into a Base64 String.
     * @return Base64 string of the items.
     * @throws IllegalStateException
     */
    public static String itemStackArrayToBase64(ItemStack[] items) throws IllegalStateException {
        try {
            items = Arrays
                    .stream(items)
                    .filter(item -> item != null && item.getType() != Material.AIR)
                    .toArray(ItemStack[]::new);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeInt(items.length);

            for (ItemStack item : items) {
                if (item != null) {
                    dataOutput.writeObject(item.serialize());
                } else {
                    dataOutput.writeObject(null);
                }
            }

            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    /**
     * Gets an array of ItemStacks from Base64 string.
     *
     * @param data Base64 string to convert to ItemStack array.
     * @return ItemStack array created from the Base64 string.
     * @throws IOException
     */
    public static ItemStack[] itemStackArrayFromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            for (int Index = 0; Index < items.length; Index++) {
                Map<String, Object> stack = (Map<String, Object>) dataInput.readObject();

                if (stack != null) {
                    items[Index] = ItemStack.deserialize(stack);
                } else {
                    items[Index] = null;
                }
            }

            dataInput.close();

            items = Arrays
                    .stream(items)
                    .filter(item -> item != null && item.getType() != Material.AIR)
                    .toArray(ItemStack[]::new);

            return items;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }

    /**
     * Gets one {@link ItemStack} from Base64 string.
     *
     * @param data Base64 string to convert to {@link ItemStack}.
     * @return {@link ItemStack} created from the Base64 string.
     * @throws IOException
     */
    public static ItemStack itemStackFromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decode(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack item;

            // Read the serialized inventory
            item = (ItemStack) dataInput.readObject();

            dataInput.close();
            return item;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }

    /**
     * A method to serialize one {@link ItemStack} to Base64 String.
     *
     * @param item to turn into a Base64 String.
     * @return Base64 string of the item.
     * @throws IllegalStateException
     */
    public static String itemStackToBase64(ItemStack item) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Save every element
            dataOutput.writeObject(item);

            // Serialize that array
            dataOutput.close();
            return new String(Base64Coder.encode(outputStream.toByteArray()));
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    /**
     *  Returns a list of unique items with lore containing the total count of each material
     *  @param items to be sorted and counted
     *  @return list of unique items with lore
     *
     */
    public static List<ItemStack> getGUIItemsList(ItemStack[] items) {
        // TreeMap to automatically sort items by display name
        TreeMap<String, ItemStack> uniqueItemsMap = new TreeMap<>();

        // HashMap to store total count of each material
        HashMap<Material, Integer> itemCount = new HashMap<>();

        for (ItemStack item : items) {
            if (item != null) {
                Material material = item.getType();
                int amount = item.getAmount();

                // Update total count of material
                itemCount.put(material, itemCount.getOrDefault(material, 0) + amount);

                // Create unique item with lore
                ItemStack newItem = item.clone();
                ItemMeta meta = newItem.getItemMeta();
                if (meta != null) {
                    meta.setLore(Arrays.asList(
                            ChatColor.GOLD + "Redeemable: " + ChatColor.AQUA + itemCount.get(material) + "x"
                    ));
                    newItem.setItemMeta(meta);
                    newItem.setAmount(1);
                }

                // Add unique item to map
                uniqueItemsMap.put(material.name(), newItem);
            }
        }

        // Construct list sorted by display name
        return new ArrayList<>(uniqueItemsMap.values());
    }
}
