package us.mkaulfers.hardcoreseasons.managers;

import org.bukkit.World;
import org.bukkit.WorldCreator;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;

import java.io.File;
import java.util.List;

public class WorldManager {
    HardcoreSeasons plugin;

    public World seasonMainWorld;
    public World seasonNetherWorld;
    public World seasonEndWorld;

    public WorldManager(HardcoreSeasons plugin) {
        this.plugin = plugin;
        initWorlds(plugin.currentSeasonNum);
    }

    private void initWorlds(int seasonNum) {
        String baseDir = "./seasonal_worlds/";
        List<World> officialWorlds = plugin.getServer().getWorlds();

        // Ensure the base directory exists
        File baseDirectory = new File(baseDir);
        if (!baseDirectory.exists()) {
            baseDirectory.mkdirs();
        }

        // Check and set or create the main world
        World officialWorld = officialWorlds.stream().filter(w -> w.getEnvironment() == World.Environment.NORMAL).findFirst().orElse(null);
        if (officialWorld != null) {
            WorldCreator worldCreator = new WorldCreator(baseDir + "season_" + seasonNum);
            worldCreator.copy(officialWorld);
            worldCreator.seed(System.currentTimeMillis());
            seasonMainWorld = plugin.getServer().createWorld(worldCreator);
            plugin.getLogger().info("Is main world null? " + (seasonMainWorld == null));
        }

        // Check and set or create the Nether world
        World officialNetherWorld = officialWorlds.stream().filter(w -> w.getEnvironment() == World.Environment.NETHER).findFirst().orElse(null);
        if (officialNetherWorld != null) {
            WorldCreator worldCreator = new WorldCreator(baseDir + "season_" + seasonNum + "_nether");
            worldCreator.copy(officialNetherWorld);
            worldCreator.seed(System.currentTimeMillis());
            seasonNetherWorld = plugin.getServer().createWorld(worldCreator);
            plugin.getLogger().info("Is nether world null? " + (seasonNetherWorld == null));
        }

        // Check and set or create the End world
        World officialEndWorld = officialWorlds.stream().filter(w -> w.getEnvironment() == World.Environment.THE_END).findFirst().orElse(null);
        if (officialEndWorld != null) {
            WorldCreator worldCreator = new WorldCreator(baseDir + "season_" + seasonNum + "_end");
            worldCreator.copy(officialEndWorld);
            worldCreator.seed(System.currentTimeMillis());
            seasonEndWorld = plugin.getServer().createWorld(worldCreator);
            plugin.getLogger().info("Is end world null? " + (seasonEndWorld == null));
        }
    }

    private boolean doesDirectoryExist(String path) {
        return new File(path).exists();
    }
}
