package fr.anto42.emma.coreManager.scenarios.scFolder;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.scenarios.ScenarioManager;
import fr.anto42.emma.coreManager.scenarios.ScenarioType;
import fr.anto42.emma.coreManager.scenarios.UHCScenario;
import fr.anto42.emma.utils.players.SoundUtils;
import fr.anto42.emma.utils.materials.OreType;
import fr.anto42.emma.utils.materials.UniversalMaterial;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class VeinMiner extends UHCScenario {
    public VeinMiner(ScenarioManager scenarioManager) {
        super("VeinMiner", new ItemStack(Material.GOLD_ORE), scenarioManager);
        super.setDesc("§8┃ §fAccroupissez-vous lorsque vous minez pour détruire tout un filon d'un coup de pioche");
        setScenarioType(ScenarioType.MINNING);
    }

    private static final BlockFace[] BLOCK_FACES = new BlockFace[]{
            BlockFace.DOWN, BlockFace.UP, BlockFace.SOUTH,
            BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST
    };

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (!isActivated()) return;
        Player player = e.getPlayer();

        if (!player.isSneaking()) return;

        Block block = e.getBlock();
        ItemStack tool = player.getItemInHand();

        if (block.getType() == UniversalMaterial.GLOWING_REDSTONE_ORE.getType()) {
            block.setType(Material.REDSTONE_ORE);
        }

        Optional<OreType> oreType = OreType.valueOf(block.getType());
        if (!oreType.isPresent() || !oreType.get().isCorrectTool(tool.getType())) {
            return;
        }
        Vein vein = new Vein(block);

        List<Block> blocksToBreak = new ArrayList<>(vein.blocks);

        new BukkitRunnable() {
            int index = 0;

            @Override
            public void run() {
                if (index >= blocksToBreak.size()) {
                    cancel();
                    return;
                }

                Block b = blocksToBreak.get(index);
                SoundUtils.playSoundToPlayer(e.getPlayer(), Sound.DIG_STONE);
                b.setType(Material.AIR);
                index++;
            }
        }.runTaskTimer(UHC.getInstance(), 3L, 3L);

        int amount = blocksToBreak.size()-1;
        ItemStack drops = new ItemStack(oreType.get().getDrop(), amount);
        Location loc = player.getLocation().getBlock().getLocation().add(.5, .5, .5);
        loc.getWorld().dropItem(loc, drops);

        int xp = oreType.get().getXpPerBlock() * amount;
        if (xp != 0) {
            ExperienceOrb orb = (ExperienceOrb) block.getLocation().getWorld().spawnEntity(block.getLocation(), EntityType.EXPERIENCE_ORB);
            orb.setExperience(amount);
        }
    }

    private static class Vein {
        private final Set<Block> blocks = new HashSet<>();
        private final Material type;

        public Vein(Block startBlock) {
            this.type = startBlock.getType();
            findVein(startBlock);
        }

        private void findVein(Block block) {
            if (block.getType() != type || blocks.contains(block)) return;

            blocks.add(block);

            for (BlockFace face : BLOCK_FACES) {
                findVein(block.getRelative(face));
            }
        }
    }
}
