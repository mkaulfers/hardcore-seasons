package usa.mkaulfers.hardcoreseasons.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class InventoryUtils {
    public static String inventoryToBase64(Inventory inventory) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

        // Write the size of the inventory
        dataOutput.writeInt(inventory.getSize());

        // Save all the ItemStacks to the stream
        for (int i = 0; i < inventory.getSize(); i++) {
            dataOutput.writeObject(inventory.getItem(i));
        }

        // For PlayerInventory, save the armor contents as well
        if (inventory instanceof PlayerInventory) {
            for (ItemStack armor : ((PlayerInventory) inventory).getArmorContents()) {
                dataOutput.writeObject(armor);
            }
        }

        // Close the data output and convert the byte array output stream to Base64
        dataOutput.close();
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    public static Inventory base64ToInventory(String data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
        BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
        Inventory inventory = Bukkit.getServer().createInventory(null, dataInput.readInt());

        // Read the serialized inventory
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, (ItemStack) dataInput.readObject());
        }

        dataInput.close();
        return inventory;
    }
}

