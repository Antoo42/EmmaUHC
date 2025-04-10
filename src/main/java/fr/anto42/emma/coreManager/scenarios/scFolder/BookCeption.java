package fr.anto42.emma.coreManager.scenarios.scFolder;

import fr.anto42.emma.coreManager.scenarios.ScenarioManager;
import fr.anto42.emma.coreManager.scenarios.ScenarioType;
import fr.anto42.emma.coreManager.scenarios.UHCScenario;
import fr.anto42.emma.utils.materials.ItemCreator;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class BookCeption extends UHCScenario {
    public BookCeption(ScenarioManager scenarioManager) {
        super("BookCeption", new ItemStack(Material.ENCHANTED_BOOK), scenarioManager);
        setScenarioType(ScenarioType.PVP);
    }

    Random random = new Random();
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if(!isActivated())
            return;
        Enchantment enchantment = Enchantment.values()[random.nextInt(Enchantment.values().length - 1)];
        event.getDrops().add(new ItemCreator(Material.ENCHANTED_BOOK).enchant(enchantment, random.nextInt(enchantment.getMaxLevel())).get());
    }
}
