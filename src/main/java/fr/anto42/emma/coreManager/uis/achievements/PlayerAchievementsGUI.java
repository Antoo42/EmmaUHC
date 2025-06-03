package fr.anto42.emma.coreManager.uis.achievements;

import fr.anto42.emma.coreManager.achievements.Achievement;
import fr.anto42.emma.coreManager.achievements.AchievementManager;
import fr.anto42.emma.coreManager.achievements.PlayerAchievementData;
import fr.anto42.emma.utils.skulls.SkullList;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayerAchievementsGUI {
    private final KInventory kInventory;
    private static final int ACHIEVEMENTS_PER_PAGE = 27;

    public PlayerAchievementsGUI(OfflinePlayer target, Player viewer, int page) {
        this.kInventory = new KInventory(54, "§3§lSuccès de " + target.getName() + " §7(Page " + (page + 1) + ")");

        PlayerAchievementData data = AchievementManager.loadPlayerAchievementData(target.getUniqueId());
        List<Achievement> achievements = AchievementManager.getAllAchievements();

        KItem glass = new KItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 3));
        for (int i = 0; i < 9; i++) {
            this.kInventory.setElement(i, glass);
        }
        for (int i = 36; i < 54; i++) {
            this.kInventory.setElement(i, glass);
        }

        int totalPages = (int) Math.ceil(achievements.size() / (double) ACHIEVEMENTS_PER_PAGE);
        int start = page * ACHIEVEMENTS_PER_PAGE;
        int end = Math.min(start + ACHIEVEMENTS_PER_PAGE, achievements.size());

        int slot = 9;
        for (int i = start; i < end; i++) {
            Achievement achievement = achievements.get(i);
            PlayerAchievementData.AchievementProgress progress = data.getProgress(achievement.getId());
            KItem achItem = getkItem(progress, achievement, AchievementManager.loadPlayerAchievementData(target.getUniqueId()).getProgress(achievement.getId()));
            kInventory.setElement(slot++, achItem);
        }

        if (page > 0) {
            KItem prev = new KItem(new ItemStack(Material.ARROW));
            prev.setName("§8┃ §6Page précédente");
            prev.addCallback((inv, item, p, ctx) -> new PlayerAchievementsGUI(target, viewer, page - 1).getkInventory().open(viewer));
            kInventory.setElement(48, prev);
        }

        if (page < totalPages - 1) {
            KItem next = new KItem(new ItemStack(Material.ARROW));
            next.setName("§8┃ §6Page suivante");
            next.addCallback((inv, item, p, ctx) -> new PlayerAchievementsGUI(target, viewer, page + 1).getkInventory().open(viewer));
            kInventory.setElement(50, next);
        }

        KItem back = new KItem(new ItemStack(SkullList.LEFT_AROOW.getItemStack()));
        back.setName("§8┃ §6Retour à la liste");
        back.addCallback((inv, item, p, ctx) -> new PlayersListAchievementsGUI(viewer, 0).getkInventory().open(viewer));
        kInventory.setElement(49, back);
    }

    public KInventory getkInventory() {
        return kInventory;
    }

    private static KItem getkItem(PlayerAchievementData.AchievementProgress progress, Achievement achievement, PlayerAchievementData.AchievementProgress viewerAchievement) {
        KItem achItem = new KItem(progress != null && progress.isCompleted() ?
                SkullList.GREEN_BALL.getItemStack() : SkullList.RED_BALL.getItemStack());
        achItem.setName((progress != null && progress.isCompleted() ? "§8┃ §a" : "§8┃ §c")  + (achievement.isSecret() && progress != null && !viewerAchievement.isCompleted() || !Objects.requireNonNull(progress).isCompleted() && achievement.isSecret() && !viewerAchievement.isCompleted() ? "§k" : "") + achievement.getName());
        List<String> lore = new ArrayList<>();
        lore.add("");
        if (progress != null) {
            lore.add("§8§l» §7Progression : §e" + progress.getStatus() + "§7/§b" + achievement.getRequired() +
                    " §8┃ " + getProgressBar(progress.getStatus(), achievement.getRequired(), 10, "§a", "§7"));
            if (progress.isCompleted()) {
                lore.add("§8§l» §7Statut: §a✔ Succès débloqué !");
                if (progress.getCompletionDate() != null)
                    lore.add("§8§l» §7Date de complétion : §b" + progress.getCompletionDate());
            } else {
                lore.add("§8§l» §7Statut: §c✘ Non débloqué");
            }
        } else {
            lore.add("§8§l» §7Progression : §e0§7/§b" + achievement.getRequired());
            lore.add("§8§l» §7Statut: §c✘ Non débloqué");
        }
        lore.add("");
        if (achievement.isSecret()) {
            if (viewerAchievement.isCompleted()) {
                lore.add("§8┃ §f" + achievement.getDescription());
            }
            else lore.add("§8┃ §c§k" + achievement.getDescription());
        }
        else lore.add("§8┃ §f" + achievement.getDescription());
        lore.add("");

        int totalPlayers = AchievementManager.countAllSavedPlayers();
        int playersWithAchievement = AchievementManager.countPlayersWithAchievementAll(achievement.getId());
        double percentage = totalPlayers > 0 ? (playersWithAchievement * 100.0 / totalPlayers) : 0;

        lore.add("§8§l» §7Débloqué par : §e" + playersWithAchievement + "§7 joueur(s)");
        String progressBar = getProgressBar(percentage, 10, '■', "§a", "§7");
        lore.add("§8§l» " + "§b" + String.format("%.2f", percentage) + "% §7des joueurs §8┃ " + progressBar);
        lore.add("");
        achItem.setDescription(lore);
        return achItem;
    }

    public static String getProgressBar(double percent, int barLength, char symbol, String colorFull, String colorEmpty) {
        int filledLength = (int) Math.round(barLength * percent / 100.0);
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < barLength; i++) {
            if (i < filledLength) {
                bar.append(colorFull).append(symbol);
            } else {
                bar.append(colorEmpty).append(symbol);
            }
        }
        return bar.toString();
    }

    public static String getProgressBar(int current, int max, int barLength, String colorFull, String colorEmpty) {
        double percent = max > 0 ? (double) current / max : 0;
        int filledLength = (int) Math.round(barLength * percent);
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < barLength; i++) {
            if (i < filledLength) {
                bar.append(colorFull).append("■");
            } else {
                bar.append(colorEmpty).append("■");
            }
        }
        return bar.toString();
    }
}
