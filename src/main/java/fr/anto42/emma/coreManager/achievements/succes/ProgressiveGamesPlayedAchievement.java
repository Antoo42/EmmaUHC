package fr.anto42.emma.coreManager.achievements.succes;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.achievements.Achievement;
import fr.anto42.emma.coreManager.achievements.AchievementManager;
import fr.anto42.emma.coreManager.listeners.customListeners.StartEvent;
import fr.anto42.emma.coreManager.listeners.customListeners.WinEvent;
import org.bukkit.event.EventHandler;

public abstract class ProgressiveGamesPlayedAchievement extends Achievement {

    public ProgressiveGamesPlayedAchievement(String id, String name, String description, int required) {
        super(id, name, description, required);
    }

    @EventHandler
    public void onStart(StartEvent event) {
        UHC.getInstance().getUhcGame().getUhcData().getUhcPlayerList().forEach(uhcPlayer -> {
            AchievementManager.getPlayerData(uhcPlayer.getBukkitPlayer()).updateProgress(getId(), 1);
        });
    }

    public static class OneGame extends ProgressiveGamesPlayedAchievement {
        public OneGame() { super("1_game", "Première partie", "Jouer 1 partie", 1); }
    }
    public static class TenGames extends ProgressiveGamesPlayedAchievement {
        public TenGames() { super("10_games", "Habitué", "Jouer 10 parties", 10); }
    }
    public static class TwentyFiveGames extends ProgressiveGamesPlayedAchievement {
        public TwentyFiveGames() { super("25_games", "Vétéran", "Jouer 25 parties", 25); }
    }
    public static class FiftyGames extends ProgressiveGamesPlayedAchievement {
        public FiftyGames() { super("50_games", "Fidèle", "Jouer 50 parties", 50); }
    }
    public static class HundredGames extends ProgressiveGamesPlayedAchievement {
        public HundredGames() { super("100_games", "Inconditionnel", "Jouer 100 parties", 100); }
    }
    public static class FiveHundredGames extends ProgressiveGamesPlayedAchievement {
        public FiveHundredGames() { super("500_games", "Mythique", "Jouer 500 parties", 500); }
    }
    public static class ThousandGames extends ProgressiveGamesPlayedAchievement {
        public ThousandGames() { super("1000_games", "Légende", "Jouer 1000 parties", 1000); }
    }
}
