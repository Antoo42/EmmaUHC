package fr.anto42.emma.coreManager.worldManager;

import fr.anto42.emma.UHC;
import fr.anto42.emma.utils.Cuboid;
import me.daddychurchill.CityWorld.CityWorldGenerator;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class WorldManager {
    private static World gameWorld;
    private static World netherWorld;
    private static World endWorld;
    private static Location centerLoc;
    private static Location spawnLocation;
    private static int randomSeed;
    private static boolean clean = false;
    private static boolean roofed = false;

    private static final List<World> worldList = new ArrayList<>();
    private static World inGeneration = null;
    private static final List<World> toGenerate = new ArrayList<>();

    private final FileConfiguration fileConfiguration = UHC.getInstance().getConfig();
    private final OrePopulator orePopulator = new OrePopulator();

    // ====== INIT ======
    public void init() {
        if (fileConfiguration.getBoolean("customSpawn")){
            createCustomSpawn();
        }
        createGameWorld();
    }


    // ====== WORLD CREATION METHODS ======
    public static void createGameWorld() {
        resetWorlds();
        String worldName = "worldUHC-world-" + randomSeed;

        gameWorld = createWorld(worldName, null, null);
        worldList.add(gameWorld);

        netherWorld = createWorld("worldUHC-nether-" + randomSeed, World.Environment.NETHER, null);
        worldList.add(netherWorld);

        endWorld = createWorld("worldUHC-end-" + randomSeed, World.Environment.THE_END, null);
        worldList.add(endWorld);

        setupWorlds(gameWorld, netherWorld, endWorld);
        teleportAllToSpawn();
    }

    public static void createSuperflatWorld() {
        resetWorlds();
        String worldName = "worldUHC-superflat-" + randomSeed;

        gameWorld = createWorld(worldName, World.Environment.NORMAL, null, WorldType.FLAT);
        worldList.add(gameWorld);

        netherWorld = createWorld("worldUHC-superflat-nether-" + randomSeed, World.Environment.NETHER, null, WorldType.FLAT);
        worldList.add(netherWorld);

        endWorld = createWorld("worldUHC-superflat-end-" + randomSeed, World.Environment.THE_END, null, WorldType.FLAT);
        worldList.add(endWorld);

        setupWorlds(gameWorld, netherWorld, endWorld);
        teleportAllToSpawn();
    }

    public static void createAmplifiedWorld() {
        resetWorlds();
        String worldName = "worldUHC-amplified-" + randomSeed;

        gameWorld = createWorld(worldName, World.Environment.NORMAL, null, WorldType.AMPLIFIED);
        worldList.add(gameWorld);

        netherWorld = createWorld("worldUHC-amplified-nether-" + randomSeed, World.Environment.NETHER, null, WorldType.AMPLIFIED);
        worldList.add(netherWorld);

        endWorld = createWorld("worldUHC-amplified-end-" + randomSeed, World.Environment.THE_END, null, WorldType.AMPLIFIED);
        worldList.add(endWorld);

        setupWorlds(gameWorld, netherWorld, endWorld);
        teleportAllToSpawn();
    }

    public static void createRoofedWorld() {
        resetWorlds();
        String worldName = "worldUHC-roofed-" + randomSeed;

        ChunkGenerator roofedForestGenerator = new ChunkGenerator() {
            @Override
            public short[][] generateExtBlockSections(World world, Random random, int chunkX, int chunkZ, BiomeGrid biomes) {
                int worldX = chunkX << 4;
                int worldZ = chunkZ << 4;
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        int absX = worldX + x;
                        int absZ = worldZ + z;
                        if (Math.abs(absX) <= 300 && Math.abs(absZ) <= 300) {
                            biomes.setBiome(x, z, Biome.ROOFED_FOREST);
                        }
                    }
                }
                return null;
            }
        };

        gameWorld = createWorld(worldName, World.Environment.NORMAL, roofedForestGenerator);
        worldList.add(gameWorld);

        netherWorld = createWorld("worldUHC-nether-" + randomSeed, World.Environment.NETHER, null);
        worldList.add(netherWorld);

        endWorld = createWorld("worldUHC-end-" + randomSeed, World.Environment.THE_END, null);
        worldList.add(endWorld);

        setupWorlds(gameWorld, netherWorld, endWorld);
        teleportAllToSpawn();
    }

    public static void createCityWorld(CityWorldGenerator.WorldStyle worldStyle) {
        resetWorlds();
        String baseName = "worldUHC-cityWorld_" + worldStyle + "-" + randomSeed;

        gameWorld = createCityWorldInstance(baseName, World.Environment.NORMAL, worldStyle);
        worldList.add(gameWorld);

        netherWorld = createCityWorldInstance("worldUHC-netherCityWorld_" + worldStyle + "-" + randomSeed, World.Environment.NETHER, worldStyle);
        worldList.add(netherWorld);

        endWorld = createCityWorldInstance("worldUHC-endCityWorld_" + worldStyle + "-" + randomSeed, World.Environment.THE_END, worldStyle);
        worldList.add(endWorld);

        setupWorlds(gameWorld, netherWorld, endWorld);
        teleportAllToSpawn();
    }

    // ====== UTILITIES ======

    private static void resetWorlds() {
        worldList.clear();
        randomSeed = new Random().nextInt(99999);
    }

    private static World createWorld(String name, World.Environment env, ChunkGenerator generator) {
        return createWorld(name, env, generator, WorldType.NORMAL);
    }

    private static World createWorld(String name, World.Environment env, ChunkGenerator generator, WorldType type) {
        WorldCreator wc = new WorldCreator(name);
        if (env != null) wc.environment(env);
        if (generator != null) wc.generator(generator);
        if (type != null) wc.type(type);
        return wc.createWorld();
    }

    private static World createCityWorldInstance(String name, World.Environment env, CityWorldGenerator.WorldStyle style) {
        WorldCreator wc = new WorldCreator(name);
        wc.environment(env);
        wc.generator(new CityWorldGenerator(UHC.getInstance().getCityWorldPlugin(), name, style.toString()));
        return Bukkit.getServer().createWorld(wc);
    }

    private static void setupWorlds(World gameWorld, World netherWorld, World endWorld) {
        setInGeneration(null);
        getToGenerate().clear();
        setCenterLoc(new Location(gameWorld, 0.5 , 80, 0.5, 0F, 0F));

        setGameRules(gameWorld);
        setGameRules(netherWorld);
        setGameRules(endWorld);

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

    private static void setGameRules(World world) {
        if (world == null) return;
        world.setGameRuleValue("doFireTick", "false");
        world.setGameRuleValue("naturalRegeneration", "false");
        if (world.getEnvironment() == World.Environment.NORMAL) {
            world.setGameRuleValue("doDaylightCycle", "false");
        }
    }

    private static void generateWorldBorder(World world) {
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
        teleportAllToSpawn();
    }

    private static void teleportAllToSpawn() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(getSpawnLocation());
        }
    }

    public void createCustomSpawn() {
        World world = Bukkit.getWorld(fileConfiguration.getString("worldName"));
        if (world == null) throw new IllegalStateException("Custom world not found!");
        Bukkit.getLogger().info("UHC: Custom spawn set to the world: " + world.getName());
        double x = fileConfiguration.getDouble("x");
        double y = fileConfiguration.getDouble("y");
        double z = fileConfiguration.getDouble("z");
        float yaw = (float) fileConfiguration.getDouble("yaw");
        float pitch = (float) fileConfiguration.getDouble("pitch");
        setSpawnLocation(new Location(world, x, y, z, yaw, pitch));
    }

    // ====== GETTERS / SETTERS / UTILS ======

    public static String getWorldType() {
        String[] strings = getGameWorld().getName().split("-");
        return strings.length > 1 ? strings[1] : "unknown";
    }

    public static String getWorldType(World world) {
        String[] strings = world.getName().split("-");
        return strings.length > 1 ? strings[1] : "unknown";
    }

    public static ChatColor translateWorldType(World world) {
        if (world.getEnvironment() == World.Environment.NORMAL) return ChatColor.GREEN;
        if (world.getEnvironment() == World.Environment.NETHER) return ChatColor.RED;
        else return ChatColor.DARK_AQUA;
    }

    public static String getPregenStatus(World world) {
        if (inGeneration != null && Objects.equals(inGeneration.getName(), world.getName()))
            return "§6en cours de prégénération";
        else if (getToGenerate().contains(world))
            return "§6dans la file d'attente";
        else if (world.getEnvironment() == World.Environment.NORMAL && UHC.getInstance().getUhcGame().getUhcData().isPreloadFinished())
            return "§a✔";
        else if (world.getEnvironment() == World.Environment.NETHER && UHC.getInstance().getUhcGame().getUhcData().isNetherPreload())
            return "§a✔";
        else if (world.getEnvironment() == World.Environment.THE_END && UHC.getInstance().getUhcGame().getUhcData().isEndPreload())
            return "§a✔";
        else return "§c✘";
    }

    public OrePopulator getOrePopulator() { return orePopulator; }
    public FileConfiguration getFileConfiguration() { return fileConfiguration; }

    public static World getGameWorld() { return gameWorld; }
    public static World getNetherWorld() { return netherWorld; }
    public static World getEndWorld() { return endWorld; }
    public static Location getCenterLoc() { return centerLoc; }
    public static Location getSpawnLocation() { return spawnLocation; }
    public static void setCenterLoc(Location loc) { centerLoc = loc; }
    public static void setSpawnLocation(Location loc) { spawnLocation = loc; }
    public static World getInGeneration() { return inGeneration; }
    public static void setInGeneration(World w) { inGeneration = w; }
    public static List<World> getToGenerate() { return toGenerate; }
    public static List<World> getWorldList() { return worldList; }
    public static int getRandomSeed() { return randomSeed; }
    public static void setRandomSeed(int seed) { randomSeed = seed; }
    public static boolean isClean() { return clean; }
    public static void setClean(boolean value) { clean = value; }
    public static boolean isRoofed() { return roofed; }
    public static void setRoofed(boolean value) { roofed = value; }
    public static void setGameWorld(World gameWorld) {
        WorldManager.gameWorld = gameWorld;
    }
    public static void setNetherWorld(World netherWorld) {
        WorldManager.netherWorld = netherWorld;
    }
    public static void setEndWorld(World endWorld) {
        WorldManager.endWorld = endWorld;
    }

}
