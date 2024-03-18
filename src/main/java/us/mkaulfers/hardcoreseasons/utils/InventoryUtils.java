package us.mkaulfers.hardcoreseasons.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.models.EndChest;
import us.mkaulfers.hardcoreseasons.models.ParticipantInventory;

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

    public static int countOfItemStacksInBase64(String contents) {
        try {
            ItemStack[] items = itemStackArrayFromBase64(contents);
            int count = 0;

            for (ItemStack item : items) {
                if (item != null) {
                    count += item.getAmount();
                }
            }

            return count;
        } catch (IOException e) {
            return 0;
        }
    }

    public static void updatePlayerInventories(Player player, HardcoreSeasons plugin) {
        int currentSeasonNum = plugin.activeSeason.getSeasonId();
        UUID playerId = player.getUniqueId();
        EndChest endChest = plugin.db.endChests.getEndChest(playerId, currentSeasonNum);

        if (endChest != null) {
            // Update
            endChest.setSeasonId(currentSeasonNum);
            endChest.setPlayerId(playerId);
            endChest.setContents(InventoryUtils.itemStackArrayToBase64(player.getEnderChest().getContents()));
            plugin.db.endChests.updateEndChest(endChest);
        } else {
            // Insert
            endChest = new EndChest();
            endChest.setSeasonId(currentSeasonNum);
            endChest.setPlayerId(playerId);
            endChest.setContents(InventoryUtils.itemStackArrayToBase64(player.getEnderChest().getContents()));
            plugin.db.endChests.setEndChest(endChest);
        }

        // Do the same for Inventory
        ParticipantInventory participantInventory = plugin.db.inventories.getInventory(playerId, currentSeasonNum);

        if (participantInventory != null) {
            // Update
            participantInventory.setSeasonId(currentSeasonNum);
            participantInventory.setPlayerId(playerId);
            participantInventory.setContents(InventoryUtils.playerInventoryToBase64(player.getInventory()));
            plugin.db.inventories.updateInventory(participantInventory);
        } else {
            // Insert
            participantInventory = new ParticipantInventory();
            participantInventory.setSeasonId(currentSeasonNum);
            participantInventory.setPlayerId(playerId);
            participantInventory.setContents(InventoryUtils.playerInventoryToBase64(player.getInventory()));
            plugin.db.inventories.setInventory(participantInventory);
        }
    }
}
