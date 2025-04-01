package fr.anto42.emma.coreManager.scenarios.scFolder;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.scenarios.ScenarioManager;
import fr.anto42.emma.coreManager.scenarios.ScenarioType;
import fr.anto42.emma.coreManager.scenarios.UHCScenario;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.inventory.ItemStack;

public class NoSprint extends UHCScenario {
    public NoSprint(ScenarioManager scenarioManager) {
        super("NoSprint", new ItemStack(Material.RABBIT_FOOT), scenarioManager);
        setDesc("§8┃ §fFinis la course !");
        setScenarioType(ScenarioType.FUN);
    }

    @EventHandler
    public void onPlayerToggleSprint(PlayerToggleSprintEvent event) {
        if (!isActivated()) return;
        final Player player = event.getPlayer();

        if (event.isSprinting()) {
            final int foodlevel = player.getFoodLevel();
            player.setFoodLevel(5);

            Bukkit.getServer().getScheduler().runTaskLater(UHC.getInstance(), new Runnable() {
                public void run() {
                    player.setFoodLevel(foodlevel);
                }
            }, 20);
        }
    }
}
