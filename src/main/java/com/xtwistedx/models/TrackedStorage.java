package com.xtwistedx.models;

import org.bukkit.block.Barrel;
import org.bukkit.block.Chest;
import org.bukkit.block.EnderChest;
import org.bukkit.block.ShulkerBox;

public class TrackedStorage {
    Barrel[] barrels;
    Chest[] chests;
    EnderChest[] enderChests;
    ShulkerBox[] shulkerBoxes;

    public TrackedStorage() {
        this.barrels = new Barrel[0];
        this.chests = new Chest[0];
        this.enderChests = new EnderChest[0];
        this.shulkerBoxes = new ShulkerBox[0];
    }

    public TrackedStorage(Barrel[] barrels, Chest[] chests, EnderChest[] enderChests, ShulkerBox[] shulkerBoxes) {
        this.barrels = barrels;
        this.chests = chests;
        this.enderChests = enderChests;
        this.shulkerBoxes = shulkerBoxes;
    }
}
