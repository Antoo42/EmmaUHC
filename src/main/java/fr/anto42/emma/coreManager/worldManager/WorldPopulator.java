package fr.anto42.emma.coreManager.worldManager;

import fr.anto42.emma.UHC;
import fr.anto42.emma.utils.chat.Title;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ThreadLocalRandom;

public class WorldPopulator {
    private World gameWorld;

    public void cleanWorld(boolean rooft) {
        gameWorld = WorldManager.getGameWorld();
        (new BukkitRunnable() {
            int yinit = 50;
            int progress = 0;

            int YChange = this.yinit;

            @Override
            public void run() {
                int radius = 300;
                WorldManager.setClean(true);
                /*if (WorldManager.isClean()){
                    this.cancel();
                    addSapling();
                }*/
                if (this.progress == 0)
                    Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §aDébut du nettoyage du centre de la carte...");
                for (int x = -radius; x <= radius; x++) {
                    for (int z = -radius; z <= radius; z++) {
                        Block block = WorldPopulator.this.gameWorld.getBlockAt(x, this.YChange, z);
                        if (block.getType() == Material.LEAVES || block.getType() == Material.LEAVES_2 || block.getType() == Material.LOG || block.getType() == Material.LOG_2 || block.getType() == Material.RED_MUSHROOM || block.getType() == Material.BROWN_MUSHROOM) {
                            block.setType(Material.AIR);
                            if (block.getLocation().add(0.0D, -1.0D, 0.0D).getBlock().getType().equals(Material.DIRT))
                                block.getLocation().add(0.0D, -1.0D, 0.0D).getBlock().setType(Material.GRASS);
                        } /*else if (block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER || block.getType() == Material.LAVA || block.getType() == Material.STATIONARY_LAVA || block.getType() == Material.ICE || block.getType() == Material.PACKED_ICE) {
                            block.setType(Material.GRASS);
                        }*/
                        //block.setBiome(Biome.PLAINS);
                    }
                }
                this.YChange++;
                this.progress++;
                //System.out.println("Clean center: " + this.progress + "%");
                for (Player player : Bukkit.getOnlinePlayers())
                    Title.sendActionBar(player, "§8§l» §3Nettoyage du centre §a" + "§3: §e" + ((int) (progress * 0.39) + 1) + "§a%");
                if (this.progress >= 256) {
                    cancel();
                    System.out.println("Finish to clean the center");
                    if (rooft)
                        addSapling();
                }
            }
        }).runTaskTimer(UHC.getInstance(), 1L, 1L);
    }



    public void addSapling() {
        if (!WorldManager.isClean()){
            cleanWorld(true);
            return;
        }
        gameWorld = WorldManager.getGameWorld();
        Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §aDébut de la génération de la forêt noire au centre de la carte...");
        System.out.println("Start the creation of the roofed forest");
        int yInicial = 50;
        final int[] progress = {(gameWorld.getName().contains("flat") ? 0 : 50)};
        (new Thread(() -> (new BukkitRunnable() {


            int YChange = yInicial;
            @Override
            public void run() {
                for (int radius = 300, x = -radius; x <= radius; x++) {
                    for (int z = -radius; z <= radius; z++) {
                        Block block = WorldPopulator.this.gameWorld.getBlockAt(x, this.YChange, z);
                        if (block.getType() == Material.AIR && (WorldPopulator.this.gameWorld.getBlockAt(x, this.YChange - 1, z).getType().equals(Material.DIRT) || WorldPopulator.this.gameWorld.getBlockAt(x, this.YChange - 1, z).getType().equals(Material.GRASS))) {
                            int i = ThreadLocalRandom.current().nextInt(100);
                            if (i <= 6)
                                block.getWorld().generateTree(block.getLocation(), TreeType.DARK_OAK);
                            if (i == 90) {
                                block.getWorld().generateTree(block.getLocation(), TreeType.TREE);
                            } else if (i == 91) {
                                block.getWorld().generateTree(block.getLocation(), TreeType.BIRCH);
                            }
                        }
                    }
                }
                this.YChange++;
                progress[0]++;
                for (Player player : Bukkit.getOnlinePlayers())
                    Title.sendActionBar(player, "§8§l» §3Création de la roofed §a" + "§3: §e" + ((int) (progress[0] * 0.39) + 1) + "%");
                if (progress[0] >= 256){
                    this.cancel();
                    WorldManager.setRoofed(true);
                }
            }
        }).runTaskTimer(UHC.getInstance(), 1L, 1L))).start();
    }

     private void taiga(){
        if (!WorldManager.isClean()){
            cleanWorld(true);
            return;
        }
        System.out.println("Start the creation of the taiga");
        (new Thread(() -> (new BukkitRunnable() {
            int yInicial = 50;

            int progress = 0;

            int YChange = this.yInicial;
            @Override
            public void run() {
                for (int radius = 250, x = 0 - radius; x <= radius; x++) {
                    for (int z = 0 - radius; z <= radius; z++) {
                        Block block = WorldPopulator.this.gameWorld.getBlockAt(x, this.YChange, z);
                        if (block.getType() == Material.AIR && (WorldPopulator.this.gameWorld.getBlockAt(x, this.YChange - 1, z).getType().equals(Material.DIRT) || WorldPopulator.this.gameWorld.getBlockAt(x, this.YChange - 1, z).getType().equals(Material.GRASS))) {
                            int i = ThreadLocalRandom.current().nextInt(36);
                            if (i <= 34)
                                block.getWorld().generateTree(block.getLocation(), TreeType.REDWOOD);
                        }
                    }
                }
                this.YChange++;
                this.progress++;
                for (Player player : Bukkit.getOnlinePlayers())
                    Title.sendActionBar(player, ChatColor.YELLOW + "Création de la TAIGA :" + ChatColor.GREEN + this.progress + "%");
                if (this.progress >= 100){
                    this.cancel();
                    WorldManager.setRoofed(true);
                }
            }
        }).runTaskTimer(UHC.getInstance(), 1L, 5L))).run();
    }
}