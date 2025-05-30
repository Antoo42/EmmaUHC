package fr.anto42.emma.coreManager.achievements.succes;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.achievements.Achievement;
import fr.anto42.emma.coreManager.achievements.AchievementManager;
import fr.anto42.emma.coreManager.listeners.customListeners.DeathEvent;
import fr.anto42.emma.game.GameState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class FirstKill extends Achievement {
    public FirstKill() {
        super("first_blood", "Premier Sang", "Faire le premier kill de la partie", 1);
    }
    private boolean firstBloodGiven = false;
    @EventHandler
    public void onPlayerKill(DeathEvent event) {
        if (!UHC.getInstance().getUhcGame().getGameState().equals(GameState.PLAYING))
            return;

        if (firstBloodGiven)
            return;

        Player killer = event.getKiller().getBukkitPlayer();
        if (killer == null)
            return;

        AchievementManager.getPlayerData(killer).updateProgress(getId(), 1);
        firstBloodGiven = true;
    }
}
