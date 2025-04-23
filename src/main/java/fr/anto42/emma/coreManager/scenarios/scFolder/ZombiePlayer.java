package fr.anto42.emma.coreManager.scenarios.scFolder;

import fr.anto42.emma.coreManager.scenarios.ScenarioManager;
import fr.anto42.emma.coreManager.scenarios.ScenarioType;
import fr.anto42.emma.coreManager.scenarios.UHCScenario;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ZombiePlayer extends UHCScenario {
    public ZombiePlayer(ScenarioManager scenarioManager) {
        super("ZombiePlayer", new ItemStack(Material.NETHER_FENCE), scenarioManager);
        setDesc("§8┃ §fQuand un joueur meurt, un zombie apparaît à sa place avec son stuff !");
        setScenarioType(ScenarioType.FUN);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!isActivated())
            return;

        Player player = event.getEntity();

        Zombie zombie = player.getWorld().spawn(player.getLocation(), Zombie.class);
        zombie.setCustomName("§a" + player.getName());
        zombie.setCustomNameVisible(true);

        PlayerInventory inv = player.getInventory();
        zombie.getEquipment().setHelmet(inv.getHelmet());
        zombie.getEquipment().setChestplate(inv.getChestplate());
        zombie.getEquipment().setLeggings(inv.getLeggings());
        zombie.getEquipment().setBoots(inv.getBoots());

        zombie.getEquipment().setItemInHand(inv.getItemInHand());

        zombie.setBaby(false);
        zombie.setCanPickupItems(false);
    }
}
