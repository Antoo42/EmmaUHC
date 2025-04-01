package fr.anto42.emma.coreManager.scenarios.scFolder;

import fr.anto42.emma.coreManager.scenarios.ScenarioManager;
import fr.anto42.emma.coreManager.scenarios.ScenarioType;
import fr.anto42.emma.coreManager.scenarios.UHCScenario;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class Depths extends UHCScenario {
    public Depths(ScenarioManager scenarioManager) {
        super("Profondeurs abyssales", new ItemStack(Material.IRON_DOOR), scenarioManager);
        setDesc("§8┃ §fPlus vous descendez dans les grottes, plus les mobs feront des dêgats");
        setScenarioType(ScenarioType.PVE);
    }


    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!isActivated())
            return;
        if (event.getDamager() != null || (event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof Entity)) {
            if (event.getDamager() instanceof Player || (event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof Player)) {
                return;
            }

            Entity e = event.getEntity();

            if (e.getLocation().getBlockY() <= 15) {
                event.setDamage(event.getDamage() * 5);
            }
            else if (e.getLocation().getBlockY() <= 30) {
                event.setDamage(event.getDamage() * 3);
            }
            else if (e.getLocation().getBlockY() <= 45) {
                event.setDamage(event.getDamage() * 2);
            }
            else if (e.getLocation().getBlockY() <= 60) {
                event.setDamage(event.getDamage() * 1.5);
            }
        }
    }
}
