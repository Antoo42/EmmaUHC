package fr.anto42.emma.utils;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class ArmorStandUtils {

    public static void createAmorStand(String name, Location location){
        ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        stand.setCustomName(name);
        stand.setCustomNameVisible(true);
        stand.setVisible(false);
        stand.setGravity(false);
    }

    public static void updateArmorStandName(ArmorStand stand, String newName) {
        stand.setCustomName(newName);
        stand.setCustomNameVisible(true);
    }
    public static ArmorStand getArmorStandAtLocation(Location location) {
        for (Entity entity : location.getWorld().getEntities()) {
            if (entity instanceof ArmorStand && entity.getLocation().equals(location)) {
                return (ArmorStand) entity;
            }
        }
        return null;
    }

    public static void deleteArmorStand(Location location) {
        for (Entity entity : location.getWorld().getEntities()) {
            if (entity instanceof ArmorStand && entity.getLocation().equals(location)) {
                entity.remove();
            }
        }
    }
    public static void deleteArmorStand(ArmorStand stand) {
        stand.remove();
    }
}