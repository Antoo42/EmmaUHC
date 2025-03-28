package fr.anto42.emma.coreManager.worldManager;

import fr.anto42.emma.UHC;
import fr.anto42.emma.utils.Cuboid;
import me.daddychurchill.CityWorld.CityWorldGenerator;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldManager {
    private static World gameWorld;
    private static World netherWorld;
    private static World endWorld;
    private static Location centerLoc;
    private static Location spawnLocation;
    private static int random;
    private static boolean clean = false;
    private static boolean roofed = false;

    private static List<World> worldList = new ArrayList<>();
    private static World inGeneration = null;
    private static List<World> toGenerate = new ArrayList<>();

    private final FileConfiguration fileConfiguration = UHC.getInstance().getConfig();

    public void init() {
        if (UHC.getInstance().getConfig().getBoolean("customSpawn")){
            createCustomSpawn();
        }
        createGameWorld();
    }

    public static String getWorldType() {
        String string = getGameWorld().getName();
        String[] strings = string.split("-");
        return strings[1];
    }

    public void createCustomSpawn() {
        World world = Bukkit.getWorld((String) fileConfiguration.get("worldName"));
        Bukkit.getLogger().info("UHC: Custom spawn set to the world: " + world.getName());
        double x = fileConfiguration.getDouble("x");
        double y = fileConfiguration.getDouble("y");
        double z = fileConfiguration.getDouble("z");
        int yaw = fileConfiguration.getInt("yaw");
        int pitch = fileConfiguration.getInt("pitch");
        setSpawnLocation(new Location(world, x, y, z, yaw, pitch));
    }

    private World createWorld(String worldName, World.Environment environment) {
        WorldCreator worldCreator = new WorldCreator(worldName);
        if (environment != null) {
            worldCreator.environment(environment);
        }
        return worldCreator.createWorld();
    }

    public void createGameWorld() {
        random = new Random().nextInt(99999);
        String worldName = "worldUHC-world-" + random;

        gameWorld = createWorld(worldName, null);
        worldList.add(gameWorld);

        netherWorld = createWorld("worldUHC-nether-" + random, World.Environment.NETHER);
        worldList.add(netherWorld);

        endWorld = createWorld("worldUHC-end-" + random, World.Environment.THE_END);
        worldList.add(endWorld);

        setupWorlds(gameWorld, netherWorld, endWorld);

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(getSpawnLocation());
        }
    }

    private void setupWorlds(World gameWorld, World netherWorld, World endWorld) {
        setInGeneration(null);
        getToGenerate().clear();
        setCenterLoc(new Location(gameWorld, 0.5 , 80, 0.5, 0F, 0F));

        gameWorld.setGameRuleValue("doFireTick", "false");
        gameWorld.setGameRuleValue("naturalRegeneration", "false");
        gameWorld.setGameRuleValue("doDaylightCycle", "false");
        netherWorld.setGameRuleValue("naturalRegeneration", "false");
        endWorld.setGameRuleValue("naturalRegeneration", "false");
        netherWorld.setGameRuleValue("doFireTick", "false");
        endWorld.setGameRuleValue("doFireTick", "false");

        gameWorld.setTime(6000);
        gameWorld.getWorldBorder().setCenter(0, 0);
        setGameWorld(gameWorld);
        setNetherWorld(netherWorld);
        setEndWorld(endWorld);
        if (!UHC.getInstance().getConfig().getBoolean("customSpawn")) {
            setSpawnLocation(new Location(gameWorld, 0.5, 201, 0.5, 0F, 0F));
            generateWorldBorder(gameWorld);
        }

    }



    private void generateWorldBorder(World world) {
        Cuboid cuboid = new Cuboid(world, -20, 200, 20, 20, 200, -20);
        cuboid.forEach(block -> block.setType(Material.BARRIER));
        cuboid = new Cuboid(world, -20, 201, 20, 20, 203, 20);
        cuboid.forEach(block -> block.setType(Material.STAINED_GLASS_PANE));
        cuboid = new Cuboid(world, 20, 201, -20, 20, 203, 20);
        cuboid.forEach(block -> block.setType(Material.STAINED_GLASS_PANE));
        cuboid = new Cuboid(world, 20, 201, -20, -20, 203, -20);
        cuboid.forEach(block -> block.setType(Material.STAINED_GLASS_PANE));
        cuboid = new Cuboid(world, -20, 201, -20, -20, 203, 20);
        cuboid.forEach(block -> block.setType(Material.STAINED_GLASS_PANE));
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(getSpawnLocation());
        }
    }

    public void createSuperflatWorld() {
        random = new Random().nextInt(99999);
        String worldName = "worldUHC-superflat-" + random;

        World superflatWorld = new WorldCreator(worldName).environment(World.Environment.NORMAL)
                .type(WorldType.FLAT).createWorld();
        worldList.add(superflatWorld);

        World superflatNetherWorld = new WorldCreator("worldUHC-superflat-nether-" + random)
                .environment(World.Environment.NETHER)
                .type(WorldType.FLAT)
                .createWorld();
        worldList.add(superflatNetherWorld);

        World superflatEndWorld = new WorldCreator("worldUHC-superflat-end-" + random)
                .environment(World.Environment.THE_END)
                .type(WorldType.FLAT)
                .createWorld();
        worldList.add(superflatEndWorld);

        setupWorlds(superflatWorld, superflatNetherWorld, superflatEndWorld);
    }

    public void createAmplifiedWorld() {
        random = new Random().nextInt(99999);
        String worldName = "worldUHC-amplified-" + random;

        World amplifiedWorld = new WorldCreator(worldName).environment(World.Environment.NORMAL)
                .type(WorldType.AMPLIFIED).createWorld();
        worldList.add(amplifiedWorld);

        World amplifiedNetherWorld = new WorldCreator("worldUHC-amplified-nether-" + random)
                .environment(World.Environment.NETHER)
                .type(WorldType.AMPLIFIED)
                .createWorld();
        worldList.add(amplifiedNetherWorld);

        World amplifiedEndWorld = new WorldCreator("worldUHC-amplified-end-" + random)
                .environment(World.Environment.THE_END)
                .type(WorldType.AMPLIFIED)
                .createWorld();
        worldList.add(amplifiedEndWorld);

        setupWorlds(amplifiedWorld, amplifiedNetherWorld, amplifiedEndWorld);
    }

    public void createRoofedWorld() {
        random = new Random().nextInt(99999);
        String worldName = "worldUHC-roofed-" + random;

        World world = new WorldCreator(worldName).environment(World.Environment.NORMAL).createWorld();
        new BukkitRunnable() {
            @Override
            public void run() {
                // Modifie les biomes de la surface
                for (int x = 0; x < 1000; x++) {  // Ex. Modification d'une portion du monde
                    for (int z = 0; z < 1000; z++) {
                        int y = world.getHighestBlockYAt(x, z);
                        world.getBlockAt(x, y, z).setBiome(Biome.ROOFED_FOREST);

                        // Modifier le terrain sous la surface si nécessaire
                        for (int yCoord = y - 1; yCoord >= 0; yCoord--) {
                            world.getBlockAt(x, yCoord, z).setType(Material.DIRT);  // Remplace par de la terre sous la surface
                        }
                    }
                }

                Bukkit.getLogger().info("Le monde a été modifié avec succès !");
            }
        }.runTaskLater(UHC.getInstance(), 1L);
        worldList.add(world);

        World netherWorld = new WorldCreator("worldUHC-roofed-nether-" + random)
                .environment(World.Environment.NETHER)
                .createWorld();
        worldList.add(netherWorld);

        World endWorld = new WorldCreator("worldUHC-roofed-end-" + random)
                .environment(World.Environment.THE_END)
                .createWorld();
        worldList.add(endWorld);

        setupWorlds(world, netherWorld, endWorld);
    }

    public void createCityWorld(CityWorldGenerator.WorldStyle worldStyle) {
        random = new Random().nextInt(99999);
        String worldName = "worldUHC-cityWorld_" + worldStyle + "-" + random;

        WorldCreator worldCreator = new WorldCreator(worldName);
        worldCreator.generator(new CityWorldGenerator(UHC.getInstance().getCityWorldPlugin(), worldName, worldStyle.toString()));
        gameWorld = Bukkit.getServer().createWorld(worldCreator);
        worldList.add(gameWorld);

        worldName = "worldUHC-netherCityWorld_" + worldStyle + "-" + random;
        worldCreator = new WorldCreator(worldName);
        worldCreator.environment(World.Environment.NETHER);
        worldCreator.generator(new CityWorldGenerator(UHC.getInstance().getCityWorldPlugin(), worldName, worldStyle.toString()));
        netherWorld = Bukkit.getServer().createWorld(worldCreator);
        worldList.add(netherWorld);

        worldName = "worldUHC-endCityWorld_" + worldStyle + "-" + random;
        worldCreator = new WorldCreator(worldName);
        worldCreator.environment(World.Environment.THE_END);
        worldCreator.generator(new CityWorldGenerator(UHC.getInstance().getCityWorldPlugin(), worldName, worldStyle.toString()));
        endWorld = Bukkit.getServer().createWorld(worldCreator);
        worldList.add(endWorld);

        setupWorlds(gameWorld, netherWorld, endWorld);

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(getSpawnLocation());
        }
    }

    private final OrePopulator orePopulator = new OrePopulator();

    public OrePopulator getOrePopulator() {
        return orePopulator;
    }

    public static World getGameWorld() { return gameWorld; }
    public static World getNetherWorld() { return netherWorld; }
    public static World getEndWorld() { return endWorld; }
    public static Location getCenterLoc() { return centerLoc; }
    public static Location getSpawnLocation() { return spawnLocation; }
    public static void setCenterLoc(Location centerLoc) { WorldManager.centerLoc = centerLoc; }
    public static void setSpawnLocation(Location spawnLocation) { WorldManager.spawnLocation = spawnLocation; }
    public static World getInGeneration() { return inGeneration; }
    public static void setInGeneration(World inGeneration) { WorldManager.inGeneration = inGeneration; }
    public static List<World> getToGenerate() { return toGenerate; }
    public static void setToGenerate(List<World> toGenerate) { WorldManager.toGenerate = toGenerate; }

    public static void setGameWorld(World gameWorld) {
        WorldManager.gameWorld = gameWorld;
    }

    public static void setNetherWorld(World netherWorld) {
        WorldManager.netherWorld = netherWorld;
    }

    public static void setEndWorld(World endWorld) {
        WorldManager.endWorld = endWorld;
    }

    public static int getRandom() {
        return random;
    }

    public static void setRandom(int random) {
        WorldManager.random = random;
    }

    public static boolean isClean() {
        return clean;
    }

    public static void setClean(boolean clean) {
        WorldManager.clean = clean;
    }

    public static boolean isRoofed() {
        return roofed;
    }

    public static void setRoofed(boolean roofed) {
        WorldManager.roofed = roofed;
    }

    public static List<World> getWorldList() {
        return worldList;
    }

    public static void setWorldList(List<World> worldList) {
        WorldManager.worldList = worldList;
    }

    public FileConfiguration getFileConfiguration() {
        return fileConfiguration;
    }
}
