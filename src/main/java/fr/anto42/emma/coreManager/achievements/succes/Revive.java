package fr.anto42.emma.coreManager.achievements.succes;

import fr.anto42.emma.coreManager.achievements.Achievement;
import fr.anto42.emma.coreManager.achievements.AchievementManager;
import fr.anto42.emma.coreManager.listeners.customListeners.ReviveEvent;
import org.bukkit.event.EventHandler;

public class Revive extends Achievement {
    public Revive() {
        super("revived", "Revenant", "Se faire réanimer durant une partie", 1);
    }

    @EventHandler
    public void onRevive(ReviveEvent event) {
        AchievementManager.getPlayerData(event.getUHCPlayer().getBukkitPlayer()).updateProgress(getId(), 1);
    }
}
