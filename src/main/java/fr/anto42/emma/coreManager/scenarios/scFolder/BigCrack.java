package fr.anto42.emma.coreManager.scenarios.scFolder;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.scenarios.ScenarioManager;
import fr.anto42.emma.coreManager.scenarios.ScenarioType;
import fr.anto42.emma.coreManager.scenarios.UHCScenario;
import fr.anto42.emma.coreManager.worldManager.WorldManager;
import fr.anto42.emma.utils.chat.Title;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.atomic.AtomicInteger;

public class BigCrack extends UHCScenario {
    private static final int CHUNK_HEIGHT_LIMIT = 128;
    private static final int BLOCKS_PER_CHUNK = 16;
    private boolean generation = false;

    public BigCrack(ScenarioManager scenarioManager) {
        super("BigCrack", new ItemStack(Material.BEDROCK), scenarioManager);
        setScenarioType(ScenarioType.WORLD);
        setDesc("§8┃ §fCréez un gouffre au milieu de la carte de jeu");
    }

    @EventHandler
    public void onFlow(BlockFromToEvent event) {
        if (generation) event.setCancelled(true);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        generate(WorldManager.getGameWorld(), 1200, 25, 6);
    }

    public void generate(final World world, final int length, final int width, int speed) {
        generation = true;

        int xChunkMin = -((length + BLOCKS_PER_CHUNK - 1) / BLOCKS_PER_CHUNK);
        int xChunkMax = (length + BLOCKS_PER_CHUNK - 1) / BLOCKS_PER_CHUNK;
        int zChunkMin = -((width + BLOCKS_PER_CHUNK - 1) / BLOCKS_PER_CHUNK);
        int zChunkMax = (width + BLOCKS_PER_CHUNK - 1) / BLOCKS_PER_CHUNK;

        AtomicInteger delayMultiplier = new AtomicInteger(0);

        for (int x = xChunkMin; x <= xChunkMax; x++) {
            for (int z = zChunkMin; z <= zChunkMax; z++) {
                final Chunk chunk = world.getChunkAt(x, z);
                new BukkitRunnable() {
                    public void run() {
                        populate(chunk, width, length);
                        Bukkit.getOnlinePlayers().forEach(player ->
                                Title.sendActionBar(player, "§8§l» §3BigCrack §a: §eChunk effacé en x= " + chunk.getX() + ", z= " + chunk.getZ())
                        );
                    }
                }.runTaskLater(UHC.getInstance(), (long) delayMultiplier.getAndIncrement() * speed);
            }
        }

        new BukkitRunnable() {
            public void run() {
                generation = false;
                Bukkit.getOnlinePlayers().forEach(player ->
                        Title.sendActionBar(player, "§8§l» §3BigCrack §a: §eRemplacement terminé")
                );
            }
        }.runTaskLater(UHC.getInstance(), (long) delayMultiplier.get() * speed);
    }

    public void populate(Chunk chunk, int width, int length) {
        chunk.load();
        for (int x = 0; x < BLOCKS_PER_CHUNK; x++) {
            for (int z = 0; z < BLOCKS_PER_CHUNK; z++) {
                for (int y = CHUNK_HEIGHT_LIMIT - 1; y >= 0; y--) {
                    Block block = chunk.getBlock(x, y, z);
                    int xLocation = block.getX();
                    int zLocation = block.getZ();
                    if (Math.abs(zLocation) <= width && Math.abs(xLocation) <= length) {
                        block.setType(Material.AIR);
                    }
                }
            }
        }
    }
}

