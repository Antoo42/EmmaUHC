package fr.anto42.emma.coreManager.achievements.succes;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.achievements.Achievement;
import fr.anto42.emma.coreManager.achievements.AchievementManager;
import fr.anto42.emma.coreManager.listeners.customListeners.DeathEvent;
import fr.anto42.emma.game.GameState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public abstract class ProgressiveKillAchievement extends Achievement {
    private final GameState requiredState = GameState.PLAYING;

    public ProgressiveKillAchievement(String id, String name, String description, int requiredKills) {
        super(id, name, description, requiredKills);
    }

    @EventHandler
    public void onKill(DeathEvent event) {
        if (!UHC.getInstance().getUhcGame().getGameState().equals(requiredState))
            return;
        if (event.getKiller() == null)
            return;
        Player killer = event.getKiller().getBukkitPlayer();
        AchievementManager.getPlayerData(killer).updateProgress(getId(), 1);
    }

    public static class OneKill extends ProgressiveKillAchievement {
        public OneKill(){
            super("1_kill", "Le début de tout", "Effectuer 1 kill", 1);
        }
    }

    public static class TwentyFiveKills extends ProgressiveKillAchievement {
        public TwentyFiveKills() {
            super("25_kills", "Chasseur novice", "Effectuer 25 kills", 25);
        }
    }

    public static class FiftyKills extends ProgressiveKillAchievement {
        public FiftyKills() {
            super("50_kills", "Chasseur confirmé", "Effectuer 50 kills", 50);
        }
    }

    public static class HundredKills extends ProgressiveKillAchievement {
        public HundredKills() {
            super("100_kills", "Machine à tuer", "Effectuer 100 kills", 100);
        }
    }

    public static class FiveHundredKills extends ProgressiveKillAchievement {
        public FiveHundredKills() {
            super("500_kills", "Génocide", "Effectuer 500 kills", 500);
        }
    }

    public static class ThousandKills extends ProgressiveKillAchievement {
        public ThousandKills() {
            super("1000_kills", "Légende vivante", "Effectuer 1000 kills", 1000);
        }
    }

    public static class TenKills extends Achievement {
        public TenKills()
        {
            super("10_kills", "La suite logique", "Effectuer 10 kills", 10);
        }
    }
}
