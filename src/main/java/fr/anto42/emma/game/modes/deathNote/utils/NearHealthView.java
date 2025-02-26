package fr.anto42.emma.game.modes.deathNote.utils;

import fr.anto42.emma.UHC;
import fr.anto42.emma.game.modes.deathNote.impl.DNRole;
import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class NearHealthView {
    private final HashMap<UUID, ArmorStand> healthTags = new HashMap<>();

    private void startHealthUpdater() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!healthTags.containsKey(player.getUniqueId()) || healthTags.get(player.getUniqueId()).isDead()) {
                        spawnHealthTag(player);
                    }
                    updateHealthTag(player);
                }
            }
        }.runTaskTimer(UHC.getInstance(), 0L, 10L);
    }

    private void spawnHealthTag(Player player) {
        Location loc = player.getLocation().add(0, 2.3, 0);
        ArmorStand armorStand = player.getWorld().spawn(loc, ArmorStand.class);

        armorStand.setGravity(false);
        armorStand.setSmall(true);
        armorStand.setCustomNameVisible(true);
        armorStand.setCustomName(getHealthText(player));

        makeArmorStandInvisible(armorStand);

        healthTags.put(player.getUniqueId(), armorStand);
    }

    private void updateHealthTag(Player player) {
        ArmorStand armorStand = healthTags.get(player.getUniqueId());
        if (armorStand == null || armorStand.isDead()) return;

        armorStand.setCustomName(getHealthText(player));
        armorStand.teleport(player.getLocation().add(0, 2.3, 0));
    }

    private String getHealthText(Player player) {
        int health = (int) player.getHealth();
        return ChatColor.RED + "❤ " + health;
    }

    private void makeArmorStandInvisible(ArmorStand armorStand) {
        EntityArmorStand nmsArmorStand = (EntityArmorStand) ((org.bukkit.craftbukkit.v1_8_R3.entity.CraftArmorStand) armorStand).getHandle();
        DataWatcher watcher = nmsArmorStand.getDataWatcher();

        watcher.watch(0, (byte) 0x20);

        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(nmsArmorStand.getId(), watcher, true);
        UHC.getInstance().getUhcGame().getUhcData().getUhcPlayerList().stream().filter(uhcPlayer -> uhcPlayer.getBukkitPlayer() != null && uhcPlayer.getRole() != null && ((DNRole) uhcPlayer.getRole()).isSeeLife()).forEach(uhcPlayer -> {
            ((CraftPlayer) uhcPlayer.getBukkitPlayer()).getHandle().playerConnection.sendPacket(packet);
        });
    }
}
