package us.mkaulfers.hardcoreseasons.managers;

import org.bukkit.World;
import org.bukkit.WorldCreator;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.utils.FileUtils;

import java.io.File;
import java.util.List;

public class WorldManager {
    HardcoreSeasons plugin;
    public World seasonMainWorld;
    public World seasonNetherWorld;
    public World seasonEndWorld;

    public WorldManager(HardcoreSeasons plugin) {
        this.plugin = plugin;
        initWorlds(plugin.activeSeason.getSeasonId());
    }

    private void initWorlds(int seasonNum) {
        seasonMainWorld = initializeWorld(seasonNum, World.Environment.NORMAL, "season_", "");
        seasonNetherWorld = initializeWorld(seasonNum, World.Environment.NETHER, "season_", "_nether");
        seasonEndWorld = initializeWorld(seasonNum, World.Environment.THE_END, "season_", "_end");
    }

    private World initializeWorld(int seasonNum, World.Environment environment, String namePrefix, String nameSuffix) {
        String baseDir = "./seasonal_worlds/";
        List<World> officialWorlds = plugin.getServer().getWorlds();

        if (plugin.configManager.config.unloadPastSeasons) {

            String currentSeasonMainWorldName = baseDir + namePrefix + seasonNum;
            String currentSeasonNetherWorldName = baseDir + namePrefix + seasonNum + "_nether";
            String currentSeasonEndWorldName = baseDir + namePrefix + seasonNum + "_end";

            List<World> worlds = plugin.getServer().getWorlds();
            for (World world : worlds) {
                String worldName = world.getName();

                if (!worldName.equals(currentSeasonMainWorldName) &&
                        !worldName.equals(currentSeasonNetherWorldName) &&
                        !worldName.equals(currentSeasonEndWorldName) &&
                        worldName.contains("./seasonal_worlds/")) {

                    plugin.getServer().unloadWorld(world, true);

                    if (!plugin.configManager.config.persistSeasonWorlds) {
                        FileUtils.deleteRecursive(new File(world.getName())); // Ensure path is correctly specified
                    }
                }
            }
        }

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

            World generatedWorld = worldCreator.createWorld();

            if (generatedWorld == null) {
                plugin.getLogger().severe("[HardcoreSeasons]: Failed to create world " + namePrefix + seasonNum + nameSuffix);
                return null;
            }

            generatedWorld.setHardcore(officialWorld.isHardcore());
            generatedWorld.setDifficulty(officialWorld.getDifficulty());
            generatedWorld.setPVP(officialWorld.getPVP());

            return generatedWorld;
        }
        return null;
    }
}
