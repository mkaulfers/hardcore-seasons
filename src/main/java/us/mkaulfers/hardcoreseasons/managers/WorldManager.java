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
        seasonMainWorld = initializeWorld(seasonNum, World.Environment.NORMAL, "season_", "");
        seasonNetherWorld = initializeWorld(seasonNum, World.Environment.NETHER, "season_", "_nether");
        seasonEndWorld = initializeWorld(seasonNum, World.Environment.THE_END, "season_", "_end");
    }

    private World initializeWorld(int seasonNum, World.Environment environment, String namePrefix, String nameSuffix) {
        String baseDir = "./seasonal_worlds/";
        List<World> officialWorlds = plugin.getServer().getWorlds();

        // Ensure the base directory exists
        File baseDirectory = new File(baseDir);
        if (!baseDirectory.exists()) {
            baseDirectory.mkdirs();
        }

        // Check and set or create the main world
        World officialWorld = officialWorlds.stream().filter(w -> w.getEnvironment() == environment).findFirst().orElse(null);
        if (officialWorld != null) {
            WorldCreator worldCreator = new WorldCreator(baseDir + namePrefix + seasonNum + nameSuffix);
            worldCreator.copy(officialWorld);
            worldCreator.seed(System.currentTimeMillis());
            return worldCreator.createWorld();
        }
        return null;
    }
}
