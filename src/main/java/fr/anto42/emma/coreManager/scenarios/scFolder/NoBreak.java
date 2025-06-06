package fr.anto42.emma.coreManager.scenarios.scFolder;

import fr.anto42.emma.coreManager.scenarios.ScenarioManager;
import fr.anto42.emma.coreManager.scenarios.ScenarioType;
import fr.anto42.emma.coreManager.scenarios.UHCScenario;
import fr.anto42.emma.utils.materials.ItemCreator;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemDamageEvent;

public class NoBreak extends UHCScenario {
    public NoBreak(ScenarioManager scenarioManager) {
        super("NoBreak", new ItemCreator(Material.BLAZE_ROD).get(), scenarioManager);
        setDesc("§8┃ §fLes outils et les armes sont désormais incassables");
        setScenarioType(ScenarioType.STUFF);
        setAvaible(false);
    }

    @EventHandler
    public void onDamage(PlayerItemDamageEvent event){
        if (!isActivated())
            return;
        event.setCancelled(true);
    }
}
