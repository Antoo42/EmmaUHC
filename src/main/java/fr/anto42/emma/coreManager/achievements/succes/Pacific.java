package fr.anto42.emma.coreManager.achievements.succes;

import fr.anto42.emma.coreManager.achievements.Achievement;
import fr.anto42.emma.coreManager.achievements.AchievementManager;
import fr.anto42.emma.coreManager.listeners.customListeners.WinEvent;
import org.bukkit.event.EventHandler;

public class Pacific extends Achievement {
    public Pacific() {
        super("pacifist", "Pacifique", "Gagner une partie sans tuer personne", 1);
        makeSecret();
    }

    @EventHandler
    public void onWin(WinEvent event) {
        if (event.getWinner().getKills() == 0) {
            AchievementManager.getPlayerData(event.getWinner().getBukkitPlayer()).updateProgress(getId(), 1);
        }
    }
}
