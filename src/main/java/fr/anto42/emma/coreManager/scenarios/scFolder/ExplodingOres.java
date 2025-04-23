package fr.anto42.emma.coreManager.scenarios.scFolder;

import fr.anto42.emma.coreManager.scenarios.ScenarioManager;
import fr.anto42.emma.coreManager.scenarios.ScenarioType;
import fr.anto42.emma.coreManager.scenarios.UHCScenario;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class ExplodingOres extends UHCScenario {
    public ExplodingOres(ScenarioManager scenarioManager) {
        super("ExplodingOres", new ItemStack(Material.TNT), scenarioManager);
        setDesc("§8┃ §fLes minerais cassés ont 20% de chance d'exploser");
        setScenarioType(ScenarioType.MINNING);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (!isActivated())
            return;

        Material type = event.getBlock().getType();
        if (type == Material.DIAMOND_ORE ||
                type == Material.GOLD_ORE ||
                type == Material.IRON_ORE ||
                type == Material.COAL_ORE ||
                type == Material.EMERALD_ORE ||
                type == Material.LAPIS_ORE ||
                type == Material.REDSTONE_ORE) {

            double chance = 0.2;
            if (Math.random() < chance) {
                event.getBlock().getWorld().createExplosion(
                        event.getBlock().getLocation(),
                        3F,
                        false
                );
            }
        }
    }

}
