package fr.anto42.emma.coreManager.worldManager;

import fr.anto42.emma.utils.PerlinNoise;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

import org.bukkit.generator.ChunkGenerator;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator.ChunkData;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

public class CavesWorldGenerator extends ChunkGenerator {

    // La méthode pour générer les cavernes
    @Override
    public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {
        ChunkData chunkData = createChunkData(world);

        // Coordonnées de départ pour les cavernes
        int startX = x * 16;
        int startZ = z * 16;

        // Générer les cavernes dans le chunk
        generateCaves(chunkData, random, startX, startZ);

        return chunkData;
    }

    // Méthode pour générer les cavernes dans un chunk
    private void generateCaves(ChunkData chunkData, Random random, int startX, int startZ) {
        double scale = 0.1;  // Ajuster pour la taille des cavernes
        double threshold = 0.7;  // Ajuster pour la fréquence des cavernes
        PerlinNoise noise = new PerlinNoise(random.nextInt());  // Bruit Perlin pour créer des cavernes naturelles

        // Parcours de tous les blocs dans le chunk
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 60; y < 120; y++) {  // Limiter à la profondeur souhaitée
                    // Générer un bruit pour chaque bloc du chunk
                    double noiseValue = noise.getNoise((startX + x) * scale, (startZ + z) * scale);

                    // Si la valeur du bruit est inférieure au seuil, on place un bloc d'air (caverne)
                    if (noiseValue < threshold) {
                        chunkData.setBlock(x, y, z, Material.AIR);
                    } else {
                        chunkData.setBlock(x, y, z, Material.STONE);
                    }
                }
            }
        }
    }

}
