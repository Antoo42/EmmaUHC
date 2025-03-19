package fr.anto42.emma.coreManager.worldManager;

import fr.anto42.emma.UHC;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

public class OrePopulator extends BlockPopulator {

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        generateOre(Material.IRON_ORE, world, chunk, random, 10, 5, 64);
        generateOre(Material.GOLD_ORE, world, chunk, random, 6, 5, 32);
        generateOre(Material.REDSTONE_ORE, world, chunk, random, 8, 5, 20);
        generateOre(Material.DIAMOND_ORE, world, chunk, random, 2, 4, 20);
        generateOre(Material.LAPIS_ORE, world, chunk, random, 3, 5, 32);
    }

    private void generateOre(Material material, World world, Chunk chunk, Random random, int baseChance, int veinSize, int maxY) {
        int chance = baseChance * ((UHC.getInstance().getUhcGame().getUhcConfig().getBoostMultiplier()-100)/100);

        for (int i = 0; i < chance; i++) {
            int x = random.nextInt(16);
            int y = random.nextInt(maxY);
            int z = random.nextInt(16);

            for (int j = 0; j < veinSize; j++) {
                int dx = x + random.nextInt(3) - 1;
                int dy = y + random.nextInt(3) - 1;
                int dz = z + random.nextInt(3) - 1;

                if (chunk.getBlock(dx, dy, dz).getType() == Material.STONE) {
                    chunk.getBlock(dx, dy, dz).setType(material);
                }
            }
        }
    }
}