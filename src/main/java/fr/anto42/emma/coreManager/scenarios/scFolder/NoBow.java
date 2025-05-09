package fr.anto42.emma.coreManager.scenarios.scFolder;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.scenarios.ScenarioManager;
import fr.anto42.emma.coreManager.scenarios.ScenarioType;
import fr.anto42.emma.coreManager.scenarios.UHCScenario;
import fr.anto42.emma.utils.materials.ItemCreator;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

public class NoBow extends UHCScenario implements Listener {
    public NoBow(ScenarioManager scenarioManager) {
        super("NoBow", new ItemCreator(Material.BOW).get(), scenarioManager);
        super.setDesc("§8┃ §fIl est maintenant §cimpossible §fde craft des arcs");
        setScenarioType(ScenarioType.PVP);
    }

    @EventHandler
    public void onCraft(PrepareItemCraftEvent event) {
        if (!isActivated())
            return;
        CraftingInventory inv = event.getInventory();

        if (inv.getResult() == null) return;

        if (inv.getResult().getType().equals(Material.BOW)) {
            inv.setResult(new ItemStack(Material.AIR));
        }
    }

    @EventHandler
    public void onPlayerDamaging(EntityDamageByEntityEvent entityDamageByEntityEvent){
        if (!isActivated())
            return;
        if (entityDamageByEntityEvent.getDamager() instanceof Arrow && entityDamageByEntityEvent.getEntity() instanceof Player && ((Arrow)entityDamageByEntityEvent.getDamager()).getShooter() instanceof Player){
           entityDamageByEntityEvent.setDamage(0);
        }
    }
}
