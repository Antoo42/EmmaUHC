package fr.anto42.emma.coreManager.worldManager;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.worldManager.giantCaves.GCConfig;
import fr.anto42.emma.coreManager.worldManager.giantCaves.GiantCavePopulator;
import fr.anto42.emma.utils.TimeUtils;
import fr.anto42.emma.utils.chat.Title;
import fr.anto42.emma.utils.gameSaves.EventType;
import fr.anto42.emma.utils.players.PlayersUtils;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Generator {
    private static final int CHUNK_SIZE = 16;
    private static final int DEFAULT_CHUNKS_PER_TICK = 50;
    private static final int MAX_CHUNKS_PER_TICK = 150;
    private static final int MIN_CHUNKS_PER_TICK = 5;
    private static final int PROGRESS_UPDATE_STEP = 50;
    private static final int BATCH_SIZE = 25;
    private static final double TPS_THRESHOLD_LOW = 15.0;
    private static final double TPS_THRESHOLD_HIGH = 18.0;
    private static final int THREAD_POOL_SIZE = Math.max(2, Runtime.getRuntime().availableProcessors() / 2);

    private final World world;
    private final int size;
    private final Queue<ChunkCoords> chunkQueue = new ConcurrentLinkedQueue<>();
    private final SurgarCanePopulator sugarCanePopulator = new SurgarCanePopulator(100);
    private final OrePopulator orePopulator = UHC.getInstance().getWorldManager().getOrePopulator();
    private final GiantCavePopulator giantCavePopulator = new GiantCavePopulator(UHC.getInstance(), new GCConfig());
    private final Random random = new Random();
    private final ExecutorService chunkProcessor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    private BukkitTask task;
    private final AtomicInteger completedChunks = new AtomicInteger(0);
    private long startTime;
    private int chunksPerTick = DEFAULT_CHUNKS_PER_TICK;
    private int stableTicks = 0;
    private int totalChunks;

    private static class ChunkCoords {
        final int x, z;
        ChunkCoords(int x, int z) { this.x = x; this.z = z; }
    }

    public Generator(World world) {
        this.size = UHC.getInstance().getUhcGame().getUhcConfig().getStartBorderSize() + 200;
        this.world = world;
        optimizeWorldSettings();
        prepareQueue();
        startGeneration();
    }

    private void optimizeWorldSettings() {
        world.setGameRuleValue("randomTickSpeed", "0");
        world.setGameRuleValue("doMobSpawning", "false");
        world.setGameRuleValue("doFireTick", "false");
        world.setGameRuleValue("doWeatherCycle", "false");
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setKeepSpawnInMemory(false);
    }

    private void prepareQueue() {
        List<ChunkCoords> coords = new ArrayList<>();

        int maxRadius = size / CHUNK_SIZE;
        for (int radius = 0; radius <= maxRadius; radius++) {
            if (radius == 0) {
                coords.add(new ChunkCoords(0, 0));
                continue;
            }

            for (int i = 0; i < radius * 8; i++) {
                int x, z;
                if (i < radius * 2) {
                    x = -radius + i;
                    z = -radius;
                } else if (i < radius * 4) {
                    x = radius;
                    z = -radius + (i - radius * 2);
                } else if (i < radius * 6) {
                    x = radius - (i - radius * 4);
                    z = radius;
                } else {
                    x = -radius;
                    z = radius - (i - radius * 6);
                }

                if (Math.abs(x) <= maxRadius && Math.abs(z) <= maxRadius) {
                    coords.add(new ChunkCoords(x * CHUNK_SIZE, z * CHUNK_SIZE));
                }
            }
        }

        chunkQueue.addAll(coords);
        totalChunks = coords.size();
    }

    private void adjustChunksPerTick(double tps) {
        if (tps < TPS_THRESHOLD_LOW && chunksPerTick > MIN_CHUNKS_PER_TICK) {
            chunksPerTick = Math.max(MIN_CHUNKS_PER_TICK, chunksPerTick - 10);
            stableTicks = 0;
        } else if (tps > TPS_THRESHOLD_HIGH && chunksPerTick < MAX_CHUNKS_PER_TICK) {
            stableTicks++;
            if (stableTicks >= 20) {
                chunksPerTick = Math.min(MAX_CHUNKS_PER_TICK, chunksPerTick + 5);
                stableTicks = 0;
            }
        }
    }

    private void startGeneration() {
        if (WorldManager.getInGeneration() != null) return;

        UHC.getInstance().getGameSave().registerEvent(EventType.CORE, "Lancement de la prégen du monde " + world.getName());
        PlayersUtils.broadcastMessage("§7Lancement de la prégénération optimisée du monde §a" + world.getName() + "§7...");
        PlayersUtils.broadcastMessage("§7Utilisation de §a" + THREAD_POOL_SIZE + "§7 threads pour accélérer le processus.");

        WorldManager.setInGeneration(world);
        this.startTime = System.currentTimeMillis();

        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                double tps = MinecraftServer.getServer().recentTps[0];
                adjustChunksPerTick(tps);

                List<ChunkCoords> batch = new ArrayList<>();
                for (int i = 0; i < Math.min(BATCH_SIZE, chunksPerTick) && !chunkQueue.isEmpty(); i++) {
                    ChunkCoords coords = chunkQueue.poll();
                    if (coords != null) {
                        batch.add(coords);
                    }
                }

                if (!batch.isEmpty()) {
                    processBatch(batch);
                }

                if (chunkQueue.isEmpty() && completedChunks.get() >= totalChunks) {
                    completeGeneration();
                }
            }
        }.runTaskTimer(UHC.getInstance(), 0L, 1L);
    }

    private void processBatch(List<ChunkCoords> batch) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (ChunkCoords coords : batch) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    generateChunkAsync(coords.x, coords.z);
                } catch (Exception e) {
                    Bukkit.getLogger().severe("§cErreur lors de la génération du chunk (" + coords.x + ", " + coords.z + "): " + e.getMessage());
                }
            }, chunkProcessor);

            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenRun(() -> {
                    int completed = completedChunks.addAndGet(batch.size());
                    if (completed % PROGRESS_UPDATE_STEP == 0) {
                        Bukkit.getScheduler().runTask(UHC.getInstance(), this::updateProgress);
                    }
                });
    }

    private void generateChunkAsync(int x, int z) {
        Bukkit.getScheduler().runTask(UHC.getInstance(), () -> {
            try {
                int chunkX = x >> 4;
                int chunkZ = z >> 4;

                if (world.isChunkLoaded(chunkX, chunkZ)) {
                    return;
                }

                Chunk chunk = world.getChunkAt(chunkX, chunkZ);

                if (!chunk.isLoaded()) {
                    chunk.load(true);
                }

                if (!chunk.isLoaded()) {
                    Bukkit.getLogger().warning("Chunk non chargé : " + chunkX + "," + chunkZ);
                    return;
                }

                try {
                    orePopulator.populate(chunk.getWorld(), random, chunk);
                    Bukkit.getLogger().info("Chunk populé avec succès : " + chunkX + "," + chunkZ);
                } catch (Exception e) {
                    Bukkit.getLogger().severe("Erreur population chunk " + chunkX + "," + chunkZ + ": " + e.getMessage());
                    e.printStackTrace();
                }

                Bukkit.getScheduler().runTaskLater(UHC.getInstance(), () -> {
                    if (chunk.isLoaded()) {
                        chunk.unload(true);
                    }
                }, 20L);

            } catch (Exception e) {
                Bukkit.getLogger().severe("Erreur critique génération chunk (" + x + ", " + z + "): " + e.getMessage());
                e.printStackTrace();
            }
        });
    }


    private void updateProgress() {
        int completed = completedChunks.get();
        int percentage = totalChunks > 0 ? (completed * 100 / totalChunks) : 0;

        long elapsedTime = System.currentTimeMillis() - startTime;
        long estimatedTimeMs = completed == 0 ? 0 : (elapsedTime / completed) * (totalChunks - completed);
        int minutes = (int) (estimatedTimeMs / 1000 / 60);
        int seconds = (int) (estimatedTimeMs / 1000 % 60);

        double tps = MinecraftServer.getServer().recentTps[0];
        String tpsColor = tps >= 18 ? "§a" : tps >= 15 ? "§e" : "§c";

        for (Player player : Bukkit.getOnlinePlayers()) {
            Title.sendActionBar(player, "§8§l» §3Prégéneration §a" + world.getName() +
                    "§3: §e" + percentage + "§3%" +
                    " §7(§e" + completed + "§7/§a" + totalChunks + "§7)" +
                    " §8§l» §3ETA: §a" + minutes + "§3m §a" + seconds + "§3s" +
                    " §8§l» §3TPS: " + tpsColor + String.format("%.1f", tps) +
                    " §8§l» §3Threads: §a" + THREAD_POOL_SIZE);
        }
    }

    private void completeGeneration() {
        task.cancel();
        chunkProcessor.shutdown();
        WorldManager.setInGeneration(null);

        world.setGameRuleValue("randomTickSpeed", "3");
        world.setGameRuleValue("doMobSpawning", "true");
        world.setGameRuleValue("doFireTick", "true");
        world.setGameRuleValue("doWeatherCycle", "true");
        world.setGameRuleValue("doDaylightCycle", "true");

        long totalTime = (System.currentTimeMillis() - startTime) / 1000L;
        double chunksPerSecond = totalChunks / (double) totalTime;

        PlayersUtils.broadcastMessage("§7Prégénération du monde §a" + world.getName() +
                "§7 terminée après §c" + totalTime + "§7s (§a" + String.format("%.1f", chunksPerSecond) + "§7 chunks/s)");

        if (totalTime < 120) {
            Bukkit.broadcastMessage("§7§o(Performance excellente !)");
        }

        UHC.getInstance().getGameSave().registerEvent(EventType.CORE, "Fin de la prégen du monde " + world.getName());

        if (world.getEnvironment() == World.Environment.NORMAL) {
            UHC.getInstance().getUhcGame().getUhcData().setPreloadFinished(true);
        } else if (world.getEnvironment() == World.Environment.NETHER) {
            UHC.getInstance().getUhcGame().getUhcData().setNetherPreload(true);
        } else if (world.getEnvironment() == World.Environment.THE_END) {
            UHC.getInstance().getUhcGame().getUhcData().setEndPreload(true);
        }

        if (!WorldManager.getToGenerate().isEmpty()) {
            World nextWorld = WorldManager.getToGenerate().remove(0);
            Bukkit.getScheduler().runTaskLater(UHC.getInstance(), () -> new Generator(nextWorld), TimeUtils.seconds(5));
        }
    }
}
