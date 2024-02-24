package usa.mkaulfers.hardcoreseasons.utils;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.io.BukkitObjectOutputStream;
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
}

