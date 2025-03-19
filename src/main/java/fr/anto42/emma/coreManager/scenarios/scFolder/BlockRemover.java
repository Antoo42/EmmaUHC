package fr.anto42.emma.coreManager.scenarios.scFolder;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.scenarios.ScenarioManager;
import fr.anto42.emma.coreManager.scenarios.ScenarioType;
import fr.anto42.emma.coreManager.scenarios.UHCScenario;
import fr.anto42.emma.coreManager.scenarios.uis.BlockRemoverGUI;
import fr.anto42.emma.coreManager.worldManager.WorldManager;
import fr.anto42.emma.utils.TimeUtils;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.materials.UniversalMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;
import java.util.List;

public class BlockRemover extends UHCScenario {
    private final List<Material> materialList = new ArrayList<>();
    public BlockRemover(ScenarioManager scenarioManager) {
        super("BlockRemover", new ItemCreator(Material.COBBLESTONE).get(), scenarioManager);
        setDesc("§8┃ §fSupprime les blocks posés par les joueurs après x secondes");
        setConfigurable(true);
        setkInventory(new BlockRemoverGUI(this).getkInventory());
        setScenarioType(ScenarioType.WORLD);
        materialList.add(Material.COBBLESTONE);
        materialList.add(Material.DIRT);
        materialList.add(Material.GRASS);
        materialList.add(Material.STONE);
        materialList.add(Material.WOOD);
        materialList.add(Material.SAND);
        materialList.add(Material.GRAVEL);
        materialList.add(Material.NETHERRACK);
        materialList.add(Material.ENDER_STONE);
        materialList.add(Material.SANDSTONE);
        materialList.add(Material.LOG);
        materialList.add(Material.LOG_2);
        materialList.add(Material.WOOD);
        materialList.add(Material.WOOD_STEP);
        materialList.add(Material.WOOD_DOUBLE_STEP);
        materialList.add(Material.WOOL);
        materialList.add(Material.LEAVES);
        materialList.add(Material.LEAVES_2);
        materialList.add(UniversalMaterial.OAK_PLANKS.getType());
        materialList.add(UniversalMaterial.END_PORTAL_FRAME.getType());
        materialList.add(UniversalMaterial.OAK_LOG.getType());
        materialList.add(UniversalMaterial.SPRUCE_LOG.getType());
        materialList.add(UniversalMaterial.BIRCH_LOG.getType());
    }

    private int cooldown = 30;

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public void increaseCooldown (int sec) {
        this.cooldown += sec;
    }

    public void decreaseCooldown (int sec) {
        this.cooldown -= sec;
    }



    @EventHandler
    public void onBlock(BlockPlaceEvent event) {
        if (!isActivated())
            return;
        if (materialList.contains(event.getBlock().getType())) {
            Bukkit.getScheduler().runTaskLater(UHC.getInstance(), () -> {
                /*if (event.getBlock().getLocation().getBlock().getWorld().getBlockAt(event.getBlock().getLocation()) != event.getBlock())
                    return;*/
                event.getBlock().setType(Material.AIR);
            }, TimeUtils.seconds(cooldown));
        }
    }
}
