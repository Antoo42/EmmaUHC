package fr.anto42.emma.coreManager.scenarios.scFolder;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.scenarios.ScenarioManager;
import fr.anto42.emma.coreManager.scenarios.ScenarioType;
import fr.anto42.emma.coreManager.scenarios.UHCScenario;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class NoAbso extends UHCScenario {
    public NoAbso(ScenarioManager scenarioManager) {
        super("NoAbso", new ItemStack(Material.GOLDEN_APPLE), scenarioManager);
        setScenarioType(ScenarioType.PVP);
    }

    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        if(!isActivated())
            return;
        Player player = event.getPlayer();

        player.removePotionEffect(PotionEffectType.ABSORPTION);
        Bukkit.getScheduler().runTaskLater(UHC.getInstance(), () -> {
            player.removePotionEffect(PotionEffectType.ABSORPTION);
        }, 1);

    }
}
