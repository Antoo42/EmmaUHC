package fr.anto42.emma.coreManager.scenarios.scFolder;

import fr.anto42.emma.coreManager.listeners.customListeners.LateEvent;
import fr.anto42.emma.coreManager.listeners.customListeners.ReviveEvent;
import fr.anto42.emma.coreManager.listeners.customListeners.StartEvent;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.coreManager.scenarios.ScenarioManager;
import fr.anto42.emma.coreManager.scenarios.ScenarioType;
import fr.anto42.emma.coreManager.scenarios.UHCScenario;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.skulls.SkullList;
import org.bukkit.event.EventHandler;

public class DoubleHealth extends UHCScenario {
    public DoubleHealth(ScenarioManager scenarioManager) {
        super("DoubleHealth", new ItemCreator(SkullList.HEART.getItemStack()).get(), scenarioManager);
        setDesc("§8┃ §fJouez la partie avec §d20 coeurs §fau lieu de 10");
        setScenarioType(ScenarioType.PVP);
    }

    @EventHandler
    public void onStart(StartEvent event){
        if (!isActivated())
            return;
        getUhcGame().getUhcData().getUhcPlayerList().forEach(uhcPlayer -> {
            uhcPlayer.getBukkitPlayer().setMaxHealth(40);
            uhcPlayer.getBukkitPlayer().setHealth(40);
        });
    }

    @EventHandler
    public void onLate(LateEvent event){
        if (!isActivated())
            return;
        UHCPlayer uhcPlayer = event.getUhcPlayer();
        uhcPlayer.getBukkitPlayer().setMaxHealth(40);
        uhcPlayer.getBukkitPlayer().setHealth(40);
    }

    @EventHandler
    public void onRevive(ReviveEvent event){
        if (!isActivated())
            return;
        UHCPlayer uhcPlayer = event.getUHCPlayer();
        uhcPlayer.getBukkitPlayer().setMaxHealth(40);
        uhcPlayer.getBukkitPlayer().setHealth(40);
    }
}
