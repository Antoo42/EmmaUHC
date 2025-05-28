package fr.anto42.emma.coreManager.achievements.succes;

import fr.anto42.emma.coreManager.achievements.Achievement;
import fr.anto42.emma.coreManager.achievements.AchievementManager;
import fr.anto42.emma.coreManager.listeners.customListeners.WinEvent;
import fr.anto42.emma.game.GameState;
import org.bukkit.event.EventHandler;

public abstract class ProgressiveWinAchievement extends Achievement {
    public ProgressiveWinAchievement(String id, String name, String description, int requiredKills) {
        super(id, name, description, requiredKills);
    }

    @EventHandler
    public void onWin(WinEvent event) {
        if (event.getWinner().getBukkitPlayer() == null)
            return;
        AchievementManager.getPlayerData(event.getWinner().getBukkitPlayer()).updateProgress(getId(), 1);
    }

    public static class OneWin extends ProgressiveWinAchievement {
        public OneWin() {
            super("one_win", "Première victoire", "Remporter 1 partie", 1);
        }
    }

    public static class FiveWins extends ProgressiveWinAchievement {
        public FiveWins() {
            super("5_wins", "Vétéran", "Remporter 5 parties", 5);
        }
    }

    public static class TenWins extends ProgressiveWinAchievement {
        public TenWins() {
            super("10_wins", "Champion", "Remporter 10 parties", 10);
        }
    }

    public static class TwentyFiveWins extends ProgressiveWinAchievement {
        public TwentyFiveWins() {
            super("25_wins", "Légende", "Remporter 25 parties", 25);
        }
    }

    public static class FiftyWins extends ProgressiveWinAchievement {
        public FiftyWins() {
            super("50_wins", "Maître du UHC", "Remporter 50 parties", 50);
        }
    }

    public static class HundredWins extends ProgressiveWinAchievement {
        public HundredWins() {
            super("100_wins", "Dieu du UHC", "Remporter 100 parties", 100);
        }
    }

    public static class FiveHundredWins extends ProgressiveWinAchievement {
        public FiveHundredWins() {
            super("500_wins", "Titan invincible", "Remporter 500 parties", 500);
        }
    }

    public static class ThousandWins extends ProgressiveWinAchievement {
        public ThousandWins() {
            super("1000_wins", "Mythologie vivante", "Remporter 1000 parties", 1000);
        }
    }

}
