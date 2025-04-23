package fr.anto42.emma.coreManager.scenarios.scFolder;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.scenarios.ScenarioManager;
import fr.anto42.emma.coreManager.scenarios.ScenarioType;
import fr.anto42.emma.coreManager.scenarios.UHCScenario;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;

public class BloodEnchant extends UHCScenario {
    public BloodEnchant(ScenarioManager scenarioManager) {
        super("BloodEnchant", new ItemStack(Material.ENCHANTMENT_TABLE), scenarioManager);
        setDesc("§8┃ §fVous perdez de la vie à chaque enchantement effectué !");
        setScenarioType(ScenarioType.STUFF);
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        if (!isActivated())
            return;

        Player player = event.getEnchanter();
        int levelsUsed = event.getExpLevelCost();

        double damage = 1.0 * levelsUsed;
        player.damage(damage);
        UHC.getUHCPlayer(player).sendClassicMessage("§cTu as perdu " + (damage / 2) + " coeur(s) en enchantant !");
    }
}
