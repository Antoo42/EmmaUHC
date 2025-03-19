package fr.anto42.emma.coreManager.worldManager;

import fr.anto42.emma.UHC;
import fr.anto42.emma.utils.TimeUtils;
import fr.anto42.emma.utils.chat.Title;
import fr.anto42.emma.utils.gameSaves.EventType;
import fr.anto42.emma.utils.players.PlayersUtils;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Generator {
    private final World world;
    private final int size;
    private BukkitTask task;
    private int completedChunks = 0;
    private long startTime;
    private final SurgarCanePopulator sugarCanePopulator = new SurgarCanePopulator(100);
    private final Queue<int[]> chunkQueue = new LinkedList<>();

    public Generator(World world) {
        this.size = UHC.getInstance().getUhcGame().getUhcConfig().getStartBorderSize() + 200;
        this.world = world;
        world.setGameRuleValue("randomTickSpeed", "0");
        prepareQueue();
        startGeneration();
    }

    private void prepareQueue() {
        for (int x = -size; x < size; x += 16) {
            for (int z = -size; z < size; z += 16) {
                chunkQueue.add(new int[]{x, z});
            }
        }
    }
    int chunksPerTick = 20;
    int limit = 50;

    public void setChunksPerTick(int chunksPerTick) {
        this.chunksPerTick = chunksPerTick;
    }

    public void addChunksPerTick() {
        this.chunksPerTick++;
    }
    public void removeChunksPerTick() {
        this.chunksPerTick--;
    }

    int stableTicks = 0;

    private void startGeneration() {
        if (WorldManager.getInGeneration() != null) {
            return;
        }
        UHC.getInstance().getGameSave().registerEvent(EventType.CORE, "Lancement de la prégen du monde " + world.getName());
        PlayersUtils.broadcastMessage("§8§l» §7Lancement de la prégénération du monde §a" + world.getName() + "§7...");
        PlayersUtils.broadcastMessage("§8§l» §cLe serveur peut subir des ralentissements ! Évitez de lancer la partie durant ce processus.");

        WorldManager.setInGeneration(world);
        this.startTime = System.currentTimeMillis();

        this.task = Bukkit.getScheduler().runTaskTimer(UHC.getInstance(), () -> {
            double tps = MinecraftServer.getServer().recentTps[0];

            if (tps < 16D && chunksPerTick > 0) {
                if (stableTicks < 30) {
                    stableTicks++;
                } else {
                    setChunksPerTick(chunksPerTick - 5);
                    stableTicks = 0;
                    //PlayersUtils.broadcastMessage("§8§l» §e⚠ Réduction du rythme de génération pour préserver les performances.");
                }
            } else if (tps > 18.5D && chunksPerTick < limit) {
                if (stableTicks < 50) {
                    stableTicks++;
                } else {
                    stableTicks = 0;
                    setChunksPerTick(chunksPerTick + 5);
                    //PlayersUtils.broadcastMessage("§8§l» §a✔ Augmentation du rythme de génération, serveur stable !");
                }
            }

            for (int i = 0; i < chunksPerTick && !chunkQueue.isEmpty(); i++) {
                int[] coords = chunkQueue.poll();
                if (coords != null) {
                    generateChunk(coords[0], coords[1]);
                }
            }

            if (chunkQueue.isEmpty()) {
                completeGeneration();
            }
        }, 0L, 1L);
    }
    OrePopulator orePopulator = UHC.getInstance().getWorldManager().getOrePopulator();
    private void generateChunk(int x, int z) {
        Bukkit.getScheduler().runTask(UHC.getInstance(), () -> {
            try {
                Chunk chunk = world.getChunkAt(world.getBlockAt(x, 64, z));
                chunk.load(true);
                chunk.load(false);
                orePopulator.populate(chunk.getWorld(), new Random(), chunk);
                completedChunks++;
                if (completedChunks % 100 == 0) {
                    updateProgress();
                }
            } catch (Exception e) {
                Bukkit.getLogger().severe("§cErreur lors de la génération du chunk (" + x + ", " + z + "): " + e.getMessage());
            }
        });
    }

    private void updateProgress() {
        int totalChunks = (size * 2 / 16) * (size * 2 / 16);
        int percentage = completedChunks * 100 / totalChunks;

        long elapsedTime = System.currentTimeMillis() - startTime;
        long estimatedTimeMs = (elapsedTime / completedChunks) * (totalChunks - completedChunks);
        int minutes = (int) (estimatedTimeMs / 1000 / 60);
        int seconds = (int) (estimatedTimeMs / 1000 % 60);

        for (Player player : Bukkit.getOnlinePlayers()) {
            Title.sendActionBar(player, "§8§l» §3Prégéneration du monde §a" + world.getName() +
                    "§3: §e" + percentage + "§3%" +
                    " §7(§e" + completedChunks + "§7/§a" + totalChunks + "§7 chunks)" +
                    " §8§l» §3Temps restant estimé: §a" + minutes + "§3m §a" + seconds + "§3s" +
                    " §8§l» §3Chunks/tick: §a" + chunksPerTick);
        }
    }

    private void completeGeneration() {
        task.cancel();
        WorldManager.setInGeneration(null);

        long totalTime = (System.currentTimeMillis() - startTime) / 1000L;
        PlayersUtils.broadcastMessage("§8§l» §7Prégénération du monde §a" + world.getName() + "§7 terminée après §c" + totalTime + " §7secondes.");
        if (totalTime < 120) {
            Bukkit.broadcastMessage("§7§o(wow c'était rapide !)");
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

    private String getServerHealthMessage() {
        double tps = MinecraftServer.getServer().recentTps[0];
        long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long maxMemory = Runtime.getRuntime().maxMemory();
        double usagePercent = (double) usedMemory / maxMemory * 100;

        String tpsStatus = (tps >= 18.5) ? "§a✔ TPS stables !" :
                (tps >= 16) ? "§e⚠ Légères baisses de performances." :
                        "§c❌ Risque de lag important !";

        String memoryStatus = (usagePercent < 50) ? "§a✔ Mémoire suffisante." :
                (usagePercent < 80) ? "§e⚠ Utilisation élevée, attention." :
                        "§c❌ Mémoire saturée, risque de crash !";

        return " " + tpsStatus + " §8§l» " + memoryStatus;
    }

}
