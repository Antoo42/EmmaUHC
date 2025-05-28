package fr.anto42.emma.coreManager.achievements;

import org.bukkit.Bukkit;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerAchievementData {
    private final Map<String, AchievementProgress> progressMap = new HashMap<>();
    private final UUID playerUUID;

    public PlayerAchievementData(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public void updateProgress(String achievementId, int amount) {
        AchievementProgress progress = progressMap.computeIfAbsent(
                achievementId,
                k -> new AchievementProgress()
        );

        if (!progress.isCompleted()) {
            progress.increment(amount, AchievementManager.getAchievementById(achievementId).get().getRequired());
            AchievementManager.savePlayerData(playerUUID);
            if (progress.completed) {
                AchievementManager.getAchievementById(achievementId).get().notifyAchievement(Bukkit.getPlayer(playerUUID));
            }
        }
    }

    public AchievementProgress getProgress(String achievementId) {
        return progressMap.getOrDefault(achievementId, new AchievementProgress());
    }

    public static class AchievementProgress {
        private int status;
        private Date completionDate;
        private boolean completed;

        public void increment(int amount, int required) {
            if (!completed) {
                status = Math.min(status + amount, required);
                if (status >= required) {
                    completed = true;
                    completionDate = new Date();
                }
            }
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public Date getCompletionDate() {
            return completionDate;
        }

        public void setCompletionDate(Date completionDate) {
            this.completionDate = completionDate;
        }

        public boolean isCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }
    }

    public Map<String, AchievementProgress> getProgressMap() {
        return progressMap;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }
}
