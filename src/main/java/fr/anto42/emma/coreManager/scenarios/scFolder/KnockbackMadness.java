package fr.anto42.emma.coreManager.scenarios.scFolder;

import fr.anto42.emma.coreManager.scenarios.ScenarioManager;
import fr.anto42.emma.coreManager.scenarios.ScenarioType;
import fr.anto42.emma.coreManager.scenarios.UHCScenario;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

public class KnockbackMadness extends UHCScenario {
    public KnockbackMadness(ScenarioManager scenarioManager) {
        super("KnockbackMadness", new ItemStack(Material.STICK), scenarioManager);
        setDesc("§8┃ §fLes coups infligent un knockback aléatoire (entre 0.5x et 5x) !");
        setScenarioType(ScenarioType.PVP);
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!isActivated())
            return;

        if (!(event.getEntity() instanceof Player))
            return;
        if (!(event.getDamager() instanceof Player))
            return;

        Player victim = (Player) event.getEntity();
        Player attacker = (Player) event.getDamager();

        double factor = ThreadLocalRandom.current().nextDouble(0.5, 5.0);

        Vector direction = victim.getLocation().toVector().subtract(attacker.getLocation().toVector()).normalize();

        Vector knockback = direction.multiply(0.4 * factor);
        knockback.setY(0.36 * factor);

        victim.setVelocity(knockback);
    }
}
