package fr.anto42.emma.coreManager.worldManager;

import fr.anto42.emma.UHC;
import fr.anto42.emma.utils.chat.Title;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

public class WorldPopulator {
    private static final Logger LOGGER = Logger.getLogger(WorldPopulator.class.getName());

    private static final int RADIUS = 300;
    private static final int INITIAL_Y = 50;
    private static final int MAX_HEIGHT = 256;
    private static final int TREE_SPAWN_CHANCE = 6;
    private static final int BIRCH_TREE_CHANCE = 90;
    private static final int OAK_TREE_CHANCE = 91;
    private static final double PROGRESS_MULTIPLIER = 0.39;

    private World gameWorld;

    public void cleanWorld(boolean shouldAddSapling) {
        gameWorld = WorldManager.getGameWorld();
        if (gameWorld == null) {
            LOGGER.severe("Game world is null, cannot clean world");
            return;
        }

        new WorldCleaningTask(shouldAddSapling).runTaskTimer(UHC.getInstance(), 1L, 1L);
    }

    public void addSapling() {
        if (!WorldManager.isClean()) {
            cleanWorld(true);
            return;
        }

        gameWorld = WorldManager.getGameWorld();
        if (gameWorld == null) {
            LOGGER.severe("Game world is null, cannot add saplings");
            return;
        }

        Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §aDébut de la génération de la forêt noire au centre de la carte...");
        LOGGER.info("Starting roofed forest creation");

        new ForestGenerationTask().runTaskTimer(UHC.getInstance(), 1L, 1L);
    }

    private class WorldCleaningTask extends BukkitRunnable {
        private int currentY = INITIAL_Y;
        private int progress = 0;
        private final boolean shouldAddSapling;

        public WorldCleaningTask(boolean shouldAddSapling) {
            this.shouldAddSapling = shouldAddSapling;
        }

        @Override
        public void run() {
            WorldManager.setClean(true);

            if (progress == 0) {
                Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §aDébut du nettoyage du centre de la carte...");
            }

            cleanLayer(currentY);

            currentY++;
            progress++;

            updateProgress("§3Nettoyage du centre", progress);

            if (progress >= MAX_HEIGHT) {
                cancel();
                LOGGER.info("Finished cleaning the center");
                if (shouldAddSapling) {
                    addSapling();
                }
            }
        }

        private void cleanLayer(int y) {
            for (int x = -RADIUS; x <= RADIUS; x++) {
                for (int z = -RADIUS; z <= RADIUS; z++) {
                    Block block = gameWorld.getBlockAt(x, y, z);
                    cleanBlock(block);
                }
            }
        }

        private void cleanBlock(Block block) {
            Material type = block.getType();

            if (isVegetation(type)) {
                block.setType(Material.AIR);

                Block blockBelow = block.getRelative(0, -1, 0);
                if (blockBelow.getType() == Material.DIRT) {
                    blockBelow.setType(Material.GRASS);
                }
            }
        }

        private boolean isVegetation(Material material) {
            return material == Material.LEAVES ||
                    material == Material.LEAVES_2 ||
                    material == Material.LOG ||
                    material == Material.LOG_2 ||
                    material == Material.RED_MUSHROOM ||
                    material == Material.BROWN_MUSHROOM;
        }
    }

    private class ForestGenerationTask extends BukkitRunnable {
        private int currentY = INITIAL_Y;
        private int progress = gameWorld.getName().contains("flat") ? 0 : 50;

        @Override
        public void run() {
            generateTreesAtLayer(currentY);

            currentY++;
            progress++;

            updateProgress("§3Création de la roofed", progress);

            if (progress >= MAX_HEIGHT) {
                cancel();
                WorldManager.setRoofed(true);
                LOGGER.info("Finished creating roofed forest");
            }
        }

        private void generateTreesAtLayer(int y) {
            for (int x = -RADIUS; x <= RADIUS; x++) {
                for (int z = -RADIUS; z <= RADIUS; z++) {
                    Block block = gameWorld.getBlockAt(x, y, z);
                    attemptTreeGeneration(block);
                }
            }
        }

        private void attemptTreeGeneration(Block block) {
            if (!canPlaceTree(block)) {
                return;
            }

            int random = ThreadLocalRandom.current().nextInt(100);
            Location location = block.getLocation();

            if (random <= TREE_SPAWN_CHANCE) {
                gameWorld.generateTree(location, TreeType.DARK_OAK);
            } else if (random == BIRCH_TREE_CHANCE) {
                gameWorld.generateTree(location, TreeType.TREE);
            } else if (random == OAK_TREE_CHANCE) {
                gameWorld.generateTree(location, TreeType.BIRCH);
            }
        }

        private boolean canPlaceTree(Block block) {
            if (block.getType() != Material.AIR) {
                return false;
            }

            Block blockBelow = block.getRelative(0, -1, 0);
            Material belowType = blockBelow.getType();

            return belowType == Material.DIRT || belowType == Material.GRASS;
        }
    }

    private void updateProgress(String taskName, int progress) {
        int percentage = (int) (progress * PROGRESS_MULTIPLIER) + 1;
        String message = "§8§l» " + taskName + " §a: §e" + percentage + "§a%";

        for (Player player : Bukkit.getOnlinePlayers()) {
            Title.sendActionBar(player, message);
        }
    }
}
