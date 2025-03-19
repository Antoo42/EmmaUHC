package fr.anto42.emma.game.modes.deathNote.utils;

import fr.anto42.emma.UHC;
import fr.anto42.emma.game.UHCGame;
import fr.anto42.emma.game.modes.deathNote.impl.DNRole;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class NearHealthView {
    private final HashMap<UUID, ArmorStand> healthTags = new HashMap<>();
    private final List<Player> playerList = new ArrayList<>();

    UHCGame uhcGame = UHC.getInstance().getUhcGame();
    public void startHealthUpdater() {
        new BukkitRunnable() {
            @Override
            public void run() {
                playerList.clear();
                uhcGame.getUhcData().getUhcPlayerList().stream().filter(uhcPlayer -> uhcPlayer.getBukkitPlayer() != null && uhcPlayer.getRole() != null && ((DNRole) uhcPlayer.getRole()).isSeeLife()).forEach(uhcPlayer -> {
                    playerList.add(uhcPlayer.getBukkitPlayer());
                });
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!healthTags.containsKey(player.getUniqueId()) || healthTags.get(player.getUniqueId()).isDead()) {
                        spawnHealthTag(player);
                    }
                    updateHealthTag(player);
                }
            }
        }.runTaskTimer(UHC.getInstance(), 0L, 1L);
    }


    private void updateHealthTag(Player player) {
        ArmorStand armorStand = healthTags.get(player.getUniqueId());
        if (armorStand == null || armorStand.isDead()) return;

        String newHealthText = getHealthText(player);
        if (!armorStand.getCustomName().equals(newHealthText)) {
            armorStand.setCustomName(newHealthText);
        }

        Location targetLocation = player.getLocation().add(0, 2.2, 0);
        if (!armorStand.getLocation().equals(targetLocation)) {
            armorStand.teleport(targetLocation);
        }

        updateVisibility(player, armorStand);
    }

    private void spawnHealthTag(Player player) {
        Location loc = player.getLocation().add(0, 2.2, 0); // Pas de 20000 blocs
        ArmorStand armorStand = player.getWorld().spawn(loc, ArmorStand.class);

        armorStand.setGravity(false);
        armorStand.setSmall(true);
        armorStand.setCustomNameVisible(true);
        armorStand.setCustomName(getHealthText(player));
        armorStand.setMarker(true);
        armorStand.setVisible(false);

        healthTags.put(player.getUniqueId(), armorStand);
        updateVisibility(player, armorStand);
    }


    private void updateVisibility(Player player, ArmorStand armorStand) {
        boolean shouldBeVisible = !player.isSneaking();

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (playerList.contains(p) && shouldBeVisible) {
                sendSpawnPacket(p, armorStand);
            } else {
                sendDestroyPacket(p, armorStand);
            }
        }
    }

    private String getHealthText(Player player) {
        int health = (int) player.getHealth();
        return ChatColor.RED + "❤ " + health;
    }

    private void sendSpawnPacket(Player viewer, ArmorStand armorStand) {
        EntityArmorStand nmsArmorStand = ((CraftArmorStand) armorStand).getHandle();
        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(nmsArmorStand);
        ((CraftPlayer) viewer).getHandle().playerConnection.sendPacket(packet);
    }

    private void sendDestroyPacket(Player viewer, ArmorStand armorStand) {
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(armorStand.getEntityId());
        ((CraftPlayer) viewer).getHandle().playerConnection.sendPacket(packet);
    }

}
