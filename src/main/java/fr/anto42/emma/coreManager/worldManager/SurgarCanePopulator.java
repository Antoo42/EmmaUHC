package fr.anto42.emma.coreManager.worldManager;

import fr.anto42.emma.utils.materials.UniversalMaterial;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

public class SurgarCanePopulator extends BlockPopulator{

    private final int percentage;

    public SurgarCanePopulator(int percentage){
        this.percentage = percentage;
    }

    @Override
    public void populate(World world, Random random, Chunk chunk){
        Material water = Material.WATER;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                Block block = world.getHighestBlockAt(chunk.getBlock(x, +1, z).getLocation());
                Block below = block.getRelative(BlockFace.DOWN);

                if (/*percentage > random.nextInt(100) && */(below.getType() == Material.SAND || below.getType() == Material.GRASS)){
                    if (
                            below.getRelative(BlockFace.NORTH).getType() == water ||
                                    below.getRelative(BlockFace.EAST).getType() == water ||
                                    below.getRelative(BlockFace.SOUTH).getType() == water ||
                                    below.getRelative(BlockFace.WEST).getType() == water
                    ){
                        if (block.getType() == Material.AIR){
                            //Bukkit.broadcastMessage(block.getLocation().toString());
                            int height = random.nextInt(3)+1;
                            Location location = block.getLocation();
                            while (height > 0){
                                location.getBlock().setType(Material.SUGAR_CANE_BLOCK);
                                Bukkit.broadcastMessage("Sugar Cane added in " + location.getBlockX() + " " + location.getBlockY() + " " + location.getZ());
                                location = location.add(0, 1, 0);
                                height--;
                            }
                        }
                    }
                }
            }
        }
    }

}
