package fr.anto42.emma.coreManager.tasks;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.listeners.customListeners.BorderMovementEvent;
import fr.anto42.emma.coreManager.listeners.customListeners.PvPEvent;
import fr.anto42.emma.coreManager.listeners.customListeners.RolesEvent;
import fr.anto42.emma.coreManager.worldManager.WorldManager;
import fr.anto42.emma.game.GameState;
import fr.anto42.emma.game.UHCGame;
import fr.anto42.emma.utils.players.SoundUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class UHCTimer extends BukkitRunnable {
    private final UHCGame uhc;
    private boolean bedrockBorder = true;

    public UHCTimer(UHCGame uhc) {
        this.uhc = uhc;
    }

    public boolean isBedrockBorder() {
        return bedrockBorder;
    }

    public void setBedrockBorder(boolean bedrockBorder) {
        this.bedrockBorder = bedrockBorder;
    }

    @Override
    public void run() {
        if (uhc.getGameState() == GameState.FINISH)
            this.cancel();

        uhc.getUhcData().setTimer(uhc.getUhcData().getTimer() + 1);

        if (uhc.getUhcConfig().getPvp() * 60 == uhc.getUhcData().getTimer() && !uhc.getUhcData().isPvp()) {
            uhc.getUhcData().setPvp(true);
            Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §7Le PvP est désormais §aactif§7 !");
            SoundUtils.playSoundToAll(Sound.WOLF_GROWL);
            Bukkit.getServer().getPluginManager().callEvent(new PvPEvent());
        }

        if (uhc.getUhcConfig().getRoles() * 60 == uhc.getUhcData().getTimer() && !uhc.getUhcData().isRoles()) {
            uhc.getUhcData().setRoles(true);
            Bukkit.getServer().getPluginManager().callEvent(new RolesEvent());
        }

        if (uhc.getUhcConfig().getTimerBorder() * 60 == uhc.getUhcData().getTimer() && !uhc.getUhcData().isBorderMove()) {
            uhc.getUhcData().setBorderMove(true);
            long time = (long) ((uhc.getUhcConfig().getStartBorderSize() - uhc.getUhcConfig().getFinalBorderSize())
                    / uhc.getUhcConfig().getBlockPerS());
            WorldManager.getGameWorld().getWorldBorder().setSize(uhc.getUhcConfig().getFinalBorderSize() * 2, time);
            WorldManager.getNetherWorld().getWorldBorder().setSize(uhc.getUhcConfig().getFinalBorderSize() * 2, time);
            Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §7La bordure est en §amouvement§7 !");
            Bukkit.getServer().getPluginManager().callEvent(new BorderMovementEvent());

            if (bedrockBorder) {
                startBedrockVisualBorder();
                Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §8[§fBedrock§8] §7Une bordure visuelle en §cBedrock §7est désormais active !");
            }
        }
    }

    private void startBedrockVisualBorder() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Location loc = player.getLocation();
                    int px = loc.getBlockX();
                    int pz = loc.getBlockZ();
                    int y = loc.getBlockY();
                    int radius = 8;
                    int wallHeight = 5;
                    double border = uhc.getUhcConfig().getFinalBorderSize();

                    for (int dx = -radius; dx <= radius; dx++) {
                        for (int dz = -radius; dz <= radius; dz++) {
                            int x = px + dx;
                            int z = pz + dz;

                            if (Math.abs(x) >= border || Math.abs(z) >= border) {
                                for (int dy = 0; dy <= wallHeight; dy++) {
                                    Location bedrockLoc = new Location(loc.getWorld(), x, y + dy, z);
                                    player.sendBlockChange(bedrockLoc, Material.BEDROCK, (byte) 0);
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(UHC.getInstance(), 0L, 5L);
    }
}
