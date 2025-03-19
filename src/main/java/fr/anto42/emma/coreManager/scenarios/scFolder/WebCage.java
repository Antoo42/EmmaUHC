package fr.anto42.emma.coreManager.scenarios.scFolder;

import fr.anto42.emma.coreManager.listeners.customListeners.DeathEvent;
import fr.anto42.emma.coreManager.scenarios.ScenarioManager;
import fr.anto42.emma.coreManager.scenarios.ScenarioType;
import fr.anto42.emma.coreManager.scenarios.UHCScenario;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class WebCage extends UHCScenario {
    public WebCage(ScenarioManager scenarioManager) {
        super("WebCage", new ItemStack(Material.WEB), scenarioManager);
        setDesc("§8┃ §fA la mort d'un joueur, une cage de toiles d'araignée apparaîtra autour de ce dernier");
        setScenarioType(ScenarioType.PVP);
    }

    @EventHandler
    public void onDeath(DeathEvent event){
        if(!isActivated())
            return;
        if (event.getVictim().getBukkitPlayer() == null)
            return;
        circle(event.getVictim().getBukkitPlayer().getLocation(), 7.0, 7.0, true, true, 3);
    }


    public static List<Block> circle(Location loc, Double r, Double h, Boolean hollow, Boolean sphere, int plus_y) {
        List<Block> circleblocks = new ArrayList<>();
        double cx = loc.getX();
        double cy = loc.getY();
        double cz = loc.getZ();

        for (double x = cx - r; x <= cx + r; ++x) {
            for (double z = cz - r; z <= cz + r; ++z) {
                for (double y = sphere ? cy - r : cy; y < (sphere ? cy + r : cy + h); ++y) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0.0D);
                    if (dist < r * r && (!hollow || dist >= (r - 1.0D) * (r - 1.0D))) {
                        Location l = new Location(loc.getWorld(), x, y + (double) plus_y, z);
                        l.getWorld().getBlockAt((int) l.getX(), (int) l.getY(), (int) l.getZ()).setType(Material.WEB);
                    }
                }
            }
        }

        return circleblocks;
    }
}
