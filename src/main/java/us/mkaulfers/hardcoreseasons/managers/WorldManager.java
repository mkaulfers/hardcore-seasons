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
        String mainWorldPath = baseDir + "season_" + seasonNum;
        String netherWorldPath = baseDir + "season_" + seasonNum + "_nether";
        String endWorldPath = baseDir + "season_" + seasonNum + "_end";

        List<World> officialWorlds = plugin.getServer().getWorlds();

        // Ensure the base directory exists
        File baseDirectory = new File(baseDir);
        if (!baseDirectory.exists()) {
            baseDirectory.mkdirs();
        }

        // Check and set or create the main world
        if (doesDirectoryExist(mainWorldPath)) {
            seasonMainWorld = plugin.getServer().getWorld("season_" + seasonNum);
        } else {
            World officialWorld = officialWorlds.stream().filter(w -> w.getEnvironment() == World.Environment.NORMAL).findFirst().orElse(null);
            if (officialWorld != null) {
                WorldCreator worldCreator = new WorldCreator(baseDir + "season_" + seasonNum);
                worldCreator.copy(officialWorld);
                seasonMainWorld = plugin.getServer().createWorld(worldCreator);
            }
        }

        // Check and set or create the Nether world
        if (doesDirectoryExist(netherWorldPath)) {
            seasonNetherWorld = plugin.getServer().getWorld("season_" + seasonNum + "_nether");
        } else {
            World officialNetherWorld = officialWorlds.stream().filter(w -> w.getEnvironment() == World.Environment.NETHER).findFirst().orElse(null);
            if (officialNetherWorld != null) {
                WorldCreator worldCreator = new WorldCreator(baseDir + "season_" + seasonNum + "_nether");
                worldCreator.copy(officialNetherWorld);
                seasonNetherWorld = plugin.getServer().createWorld(worldCreator);
            }
        }

        // Check and set or create the End world
        if (doesDirectoryExist(endWorldPath)) {
            seasonEndWorld = plugin.getServer().getWorld("season_" + seasonNum + "_end");
        } else {
            World officialEndWorld = officialWorlds.stream().filter(w -> w.getEnvironment() == World.Environment.THE_END).findFirst().orElse(null);
            if (officialEndWorld != null) {
                WorldCreator worldCreator = new WorldCreator(baseDir + "season_" + seasonNum + "_end");
                worldCreator.copy(officialEndWorld);
                seasonEndWorld = plugin.getServer().createWorld(worldCreator);
            }
        }
    }

    private boolean doesDirectoryExist(String path) {
        return new File(path).exists();
    }
}
