package fr.anto42.emma.coreManager.scenarios.scFolder;

import fr.anto42.emma.coreManager.scenarios.ScenarioManager;
import fr.anto42.emma.coreManager.scenarios.ScenarioType;
import fr.anto42.emma.coreManager.scenarios.UHCScenario;
import org.bukkit.Material;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.ItemStack;

public class Overcook extends UHCScenario {
    public Overcook(ScenarioManager scenarioManager) {
        super("Overcook", new ItemStack(Material.FURNACE), scenarioManager);
        setDesc("§8┃ §fLes fours explosent après avoir cuit un item !");
        setScenarioType(ScenarioType.FUN);
    }

    @EventHandler
    public void onSmelt(FurnaceSmeltEvent event) {
        if (!isActivated())
            return;

        Furnace furnace = (Furnace) event.getBlock().getState();
        event.setCancelled(true);

        furnace.getWorld().dropItemNaturally(furnace.getLocation(), event.getResult());

        furnace.getWorld().createExplosion(furnace.getLocation(), 2.0F, false);
        furnace.getBlock().setType(Material.AIR);
    }
}
