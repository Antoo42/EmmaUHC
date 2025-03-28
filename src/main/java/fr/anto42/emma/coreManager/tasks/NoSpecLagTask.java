package fr.anto42.emma.coreManager.tasks;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.worldManager.WorldManager;
import fr.anto42.emma.utils.players.SoundUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class NoSpecLagTask extends BukkitRunnable {
    @Override
    public void run() {
        Location centerLoc = WorldManager.getCenterLoc();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            GameMode mode = onlinePlayer.getGameMode();
            if (mode != GameMode.SPECTATOR) {
                continue;
            }

            WorldBorder worldBorder = onlinePlayer.getWorld().getWorldBorder();
            double halfBorderSize = worldBorder.getSize() / 2;
            Location playerLocation = onlinePlayer.getLocation();
            double playerX = playerLocation.getX();
            double playerZ = playerLocation.getZ();

            if (Math.abs(playerX) > halfBorderSize || Math.abs(playerZ) > halfBorderSize) {
                onlinePlayer.teleport(centerLoc);
                UHC.getUHCPlayer(onlinePlayer).sendClassicMessage("§cHop hop hop, reviens par ici toi !");
                SoundUtils.playSoundToPlayer(onlinePlayer, Sound.VILLAGER_NO);
            }
        }
    }

}
