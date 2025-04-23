package fr.anto42.emma.coreManager.scenarios.scFolder;

import fr.anto42.emma.coreManager.scenarios.ScenarioManager;
import fr.anto42.emma.coreManager.scenarios.ScenarioType;
import fr.anto42.emma.coreManager.scenarios.UHCScenario;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class CompressedOres extends UHCScenario {
    public CompressedOres(ScenarioManager scenarioManager) {
        super("CompressedOres", new ItemStack(Material.IRON_BLOCK), scenarioManager);
        setDesc("§8┃ §fLes minerais minés donnent directement leur bloc compressé !");
        setScenarioType(ScenarioType.MINNING);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (!isActivated())
            return;

        Material type = event.getBlock().getType();
        ItemStack drop = null;

        switch (type) {
            case IRON_ORE:
                drop = new ItemStack(Material.IRON_BLOCK);
                break;
            case GOLD_ORE:
                drop = new ItemStack(Material.GOLD_BLOCK);
                break;
            case DIAMOND_ORE:
                drop = new ItemStack(Material.DIAMOND_BLOCK);
                break;
            case EMERALD_ORE:
                drop = new ItemStack(Material.EMERALD_BLOCK);
                break;
            case COAL_ORE:
                drop = new ItemStack(Material.COAL_BLOCK);
                break;
            case LAPIS_ORE:
                drop = new ItemStack(Material.LAPIS_BLOCK);
                break;
            case REDSTONE_ORE:
                drop = new ItemStack(Material.REDSTONE_BLOCK);
                break;
            default:
                return;
        }
        event.setCancelled(true);
        event.getBlock().setType(Material.AIR);

        event.getPlayer().getInventory().addItem(drop).values()
                .forEach(item -> event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), item));
    }
}
