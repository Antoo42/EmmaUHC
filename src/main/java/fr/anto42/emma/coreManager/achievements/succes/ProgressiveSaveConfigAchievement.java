package fr.anto42.emma.coreManager.achievements.succes;

import fr.anto42.emma.coreManager.achievements.Achievement;
import fr.anto42.emma.coreManager.achievements.AchievementManager;
import fr.anto42.emma.coreManager.listeners.customListeners.ConfigSavedEvent;
import org.bukkit.event.EventHandler;

public abstract class ProgressiveSaveConfigAchievement extends Achievement {
    public ProgressiveSaveConfigAchievement(String id, String name, String description, int required) {
        super(id, name, description, required);
    }


    @EventHandler
    public void onSave(ConfigSavedEvent event) {
        AchievementManager.getPlayerData(event.getHost().getBukkitPlayer()).updateProgress(getId(), 1);
    }

    public static class OneSave extends ProgressiveSaveConfigAchievement {
        public OneSave() {
            super("one_save", "Première sauvegarde !", "Sauvegarder une configuration une fois", 1);
        }
    }
    public static class ThreeSaves extends ProgressiveSaveConfigAchievement {
        public ThreeSaves() {
            super("three_saves", "Sauveur régulier", "Sauvegarder une configuration 3 fois", 3);
        }
    }
    public static class FiveSaves extends ProgressiveSaveConfigAchievement {
        public FiveSaves() {
            super("five_saves", "Gardien de la config", "Sauvegarder une configuration 5 fois", 5);
        }
    }
    public static class TenSaves extends ProgressiveSaveConfigAchievement {
        public TenSaves() {
            super("ten_saves", "Archiviste", "Sauvegarder une configuration 10 fois", 10);
        }
    }

}
