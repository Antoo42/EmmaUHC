package fr.anto42.emma.utils;

import org.bukkit.Location;
import org.bukkit.World;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

public class SchematicUtils {

    public static void loadSchematic(File file, Location loc) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             GZIPInputStream gis = new GZIPInputStream(fis);
             DataInputStream dis = new DataInputStream(gis)) {

            World world = loc.getWorld();
            int width = dis.readInt();
            int height = dis.readInt();
            int depth = dis.readInt();

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    for (int z = 0; z < depth; z++) {
                        int typeId = dis.readInt();
                        byte data = dis.readByte();
                        world.getBlockAt(loc.getBlockX() + x, loc.getBlockY() + y, loc.getBlockZ() + z)
                                .setTypeIdAndData(typeId, data, false);
                    }
                }
            }
        }
    }
}
