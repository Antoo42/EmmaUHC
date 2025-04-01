package fr.anto42.emma.coreManager.scenarios.scFolder;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.scenarios.ScenarioManager;
import fr.anto42.emma.coreManager.scenarios.ScenarioType;
import fr.anto42.emma.coreManager.scenarios.UHCScenario;
import fr.anto42.emma.coreManager.worldManager.WorldManager;
import fr.anto42.emma.utils.players.PlayersUtils;
import fr.anto42.emma.utils.players.Title;
import fr.anto42.emma.utils.skulls.SkullList;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Random;

public class ChunkApocalypse extends UHCScenario {
    public ChunkApocalypse(ScenarioManager scenarioManager) {
        super("ChunkApocalypse", SkullList.BEDROCK.getItemStack(), scenarioManager);
        setDesc("§8┃ §fChaque chunk a 30% de chance d'être supprimé");
        setScenarioType(ScenarioType.WORLD);
    }


    private ArrayList<Location> locations = new ArrayList<Location>();
    private int totalChunks;

    public static BukkitRunnable task = null;
    @Override
    public void onEnable() {
        World world = WorldManager.getGameWorld();
        int radius = 1000;

        locations = new ArrayList<>();

        for (int x = -1 * radius; x < radius; x += 16) {
            for (int z = -1 * radius; z < radius; z += 16) {
                if (new Random().nextInt(99) < 30) {
                    locations.add(new Location(world, x, 1, z));
                }
            }
        }

        totalChunks = locations.size();

        PlayersUtils.broadcastMessage("§7Lancement de la génération de ChunkApocalypse.");

        task = new BukkitRunnable() {
            public void run() {
                if (locations.isEmpty()) {
                    cancel();
                    task = null;
                    return;
                }

                Location loc = locations.remove(locations.size() - 1);
                Chunk chunk = world.getChunkAt(loc);

                for (int y = 0; y < 128; y++) {
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            Block block = chunk.getBlock(x, y, z);
                            block.setType(Material.AIR);
                        }
                    }
                }

                int percentCompleted = ((totalChunks - locations.size()) * 100 / totalChunks);
                Title.sendActionBar("§8§l» §7Chunk supprimé en x:§e" + chunk.getX() + " §7z:§e" + chunk.getZ() + "§7, §a" + percentCompleted + "%");
            }
        };

        task.runTaskTimer(UHC.getInstance(), 1, 1);
    }

}
