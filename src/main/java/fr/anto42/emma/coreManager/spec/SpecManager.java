package fr.anto42.emma.coreManager.spec;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class SpecManager {
    private final FightDetectionSystem fightDetectionSystem;
    public SpecManager() {
        fightDetectionSystem = new FightDetectionSystem();
        Bukkit.getPluginManager().registerEvents(fightDetectionSystem, UHC.getInstance());
    }
    public List<UHCPlayer> getSpectators() {
        return UHC.getInstance().getUhcGame().getUhcData().getSpecList();
    }
    public boolean isSpectator(Player player) {
        return getSpectators().contains(UHC.getUHCPlayer(player));
    }

    public FightDetectionSystem getFightDetectionSystem() {
        return fightDetectionSystem;
    }
}
