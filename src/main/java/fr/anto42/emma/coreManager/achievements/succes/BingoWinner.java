package fr.anto42.emma.coreManager.achievements.succes;

import fr.anto42.emma.coreManager.achievements.Achievement;
import fr.anto42.emma.coreManager.achievements.AchievementManager;
import fr.anto42.emma.coreManager.listeners.customListeners.WinEvent;
import org.bukkit.event.EventHandler;

public class BingoWinner extends Achievement {
    public BingoWinner() {
        super("bingoWinner", "Fou du bingo", "Gagner une partie de bingo", 1);
        makeSecret();
    }


    @EventHandler
    public void onWin(WinEvent event) {
        AchievementManager.getPlayerData(event.getWinner().getBukkitPlayer()).updateProgress(getId(), 1);
    }
}
