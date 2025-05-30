package fr.anto42.emma.coreManager.achievements.succes;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.achievements.Achievement;
import fr.anto42.emma.coreManager.achievements.AchievementManager;
import fr.anto42.emma.coreManager.listeners.customListeners.EndGameEvent;
import fr.anto42.emma.coreManager.listeners.customListeners.WinEvent;
import org.bukkit.event.EventHandler;

public abstract class ProgressiveKillsGameAchievement extends Achievement {
    public ProgressiveKillsGameAchievement(String id, String name, String description, int required) {
        super(id, name, description, required);
    }


    @EventHandler
    public void onEnd(EndGameEvent event) {
        UHC.getInstance().getUhcGame().getUhcData().getUhcPlayerList().forEach(uhcPlayer -> {
            AchievementManager.getPlayerData(uhcPlayer.getBukkitPlayer()).updateProgress(getId(), uhcPlayer.getKills());
        });
    }


    public static class ThreeKills extends ProgressiveKillsGameAchievement {
        public ThreeKills() {
            super("three_kills_one_game", "Triplé !", "Faire 3 kills dans une même partie", 3);
        }
    }
    public static class FiveKills extends ProgressiveKillsGameAchievement {
        public FiveKills() {
            super("five_kills_one_game", "Quintuple élimination", "Faire 5 kills dans une même partie", 5);
        }
    }
    public static class TenKills extends ProgressiveKillsGameAchievement {
        public TenKills() {
            super("ten_kills_one_game", "Massacre", "Faire 10 kills dans une même partie", 10);
        }
    }
    public static class FifteenKills extends ProgressiveKillsGameAchievement {
        public FifteenKills() {
            super("fifteen_kills_one_game", "Boucher", "Faire 15 kills dans une même partie", 15);
        }
    }
    public static class TwentyKills extends ProgressiveKillsGameAchievement {
        public TwentyKills() {
            super("twenty_kills_one_game", "Légende vivante", "Faire 20 kills dans une même partie", 20);
        }
    }
}
