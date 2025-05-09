package fr.anto42.emma.coreManager.worldManager.giantCaves;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.plugin.Plugin;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Created by ryanmichela on 10/7/16.
 *
 * Note: This class is not thread safe, but the Spigot
 * game loop is single threaded, so it doesn't matter. :)
 */
public class BlockToucher {
    private final BlockFace[] faces = { BlockFace.SELF, BlockFace.UP, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST };
    private static final int TOUCHES_PER_TICK = 50;

    private final Plugin plugin;
    private final Queue<Block> needsTouching = new ArrayDeque<>();
    private boolean running;

    public BlockToucher(Plugin plugin) {
        this.plugin = plugin;
    }

    public void touch(Block block) {
        needsTouching.add(block);

        if (!running && plugin.isEnabled()) {
            running = true;
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new TouchTask());
        }
    }

    private class TouchTask implements Runnable {
        @Override
        public void run() {
            if (needsTouching.isEmpty()) {
                running = false;
            } else {
                for (int i = 0; i < TOUCHES_PER_TICK; i++) {
                    if (!needsTouching.isEmpty()) {
                        Block block = needsTouching.remove();
                        for (BlockFace face : faces) {
                            block.getRelative(face).getState().update(true, true);
                        }
                    }
                }

                if (plugin.isEnabled()) {
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, this);
                }
            }
        }
    }
}