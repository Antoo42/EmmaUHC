package fr.anto42.emma.coreManager.scenarios.scFolder;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.scenarios.ScenarioManager;
import fr.anto42.emma.coreManager.scenarios.ScenarioType;
import fr.anto42.emma.coreManager.scenarios.UHCScenario;
import fr.anto42.emma.coreManager.tasks.Tracking;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class BowAimBot extends UHCScenario {
    public BowAimBot(ScenarioManager scenarioManager) {
        super("BowAimBot", new ItemStack(Material.BOW), scenarioManager);
        setDesc("§8┃ §fPalie le manque de skill à l'arc");
        setScenarioType(ScenarioType.PVP);
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (!isActivated())
            return;
        if (event.getEntity() instanceof Player && event.getProjectile() instanceof org.bukkit.entity.Arrow) {
            Arrow arrow = (Arrow) event.getProjectile();
            Player player = (Player)event.getEntity();
            double minAng = 6.28D;
            Entity minEntity = null;
            for (Entity entity : player.getNearbyEntities(64.0D, 64.0D, 64.0D)) {
                if (player.hasLineOfSight(entity) && entity instanceof Player) {
                    Vector to = entity.getLocation().toVector().clone().subtract(player.getLocation().toVector());
                    double angle = event.getProjectile().getVelocity().angle(to);
                    if (angle < minAng) {
                        minAng = angle;
                        minEntity = entity;
                    }
                }
            }
            if (minEntity != null) {
                new Tracking(arrow, (LivingEntity) minEntity, UHC.getInstance());
            }
        }
    }
}
