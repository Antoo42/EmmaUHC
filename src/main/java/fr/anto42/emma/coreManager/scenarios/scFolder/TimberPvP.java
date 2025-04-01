package fr.anto42.emma.coreManager.scenarios.scFolder;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.scenarios.ScenarioManager;
import fr.anto42.emma.coreManager.scenarios.ScenarioType;
import fr.anto42.emma.coreManager.scenarios.UHCScenario;
import fr.anto42.emma.utils.players.SoundUtils;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.materials.UniversalMaterial;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TimberPvP extends UHCScenario implements Listener{
    public TimberPvP(ScenarioManager scenarioManager) {
        super("TimberPvP", new ItemCreator(Material.IRON_AXE).get(), scenarioManager);
        setDesc("§8┃ §fLes arbres sont coupés au premier coup de poing jusqu'au PvP");
        setScenarioType(ScenarioType.MINNING);
    }

    private static final BlockFace[] BLOCK_FACES = {
            BlockFace.UP, BlockFace.NORTH, BlockFace.SOUTH,
            BlockFace.EAST, BlockFace.WEST, BlockFace.DOWN
    };

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (!isActivated()) return;
        if (UHC.getInstance().getUhcGame().getUhcData().isPvp()) return;
        Block block = e.getBlock();
        Player player = e.getPlayer();

        if (!UniversalMaterial.isLog(block.getType())) return;

        TimberTree tree = new TimberTree(block);

        List<Block> blocksToBreak = new ArrayList<>(tree.blocks);

        new BukkitRunnable() {
            int index = 0;

            @Override
            public void run() {
                if (index >= blocksToBreak.size()) {
                    cancel();
                    return;
                }

                Block b = blocksToBreak.get(index);
                b.breakNaturally();
                index++;
            }
        }.runTaskTimer(UHC.getInstance(), 3L, 3L);
    }

    private static class TimberTree {
        private final Set<Block> blocks = new HashSet<>();
        private final Material type;

        public TimberTree(Block startBlock) {
            this.type = startBlock.getType();
            findTreeBlocks(startBlock);
        }

        private void findTreeBlocks(Block block) {
            if (block.getType() != type || blocks.contains(block)) return;
            blocks.add(block);

            for (BlockFace face : BLOCK_FACES) {
                findTreeBlocks(block.getRelative(face));
            }
        }
    }
}
