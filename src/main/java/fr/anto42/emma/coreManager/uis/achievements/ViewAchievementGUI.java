package fr.anto42.emma.coreManager.uis.achievements;

import fr.anto42.emma.coreManager.achievements.Achievement;
import fr.anto42.emma.coreManager.achievements.AchievementManager;
import fr.anto42.emma.coreManager.achievements.PlayerAchievementData;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.skulls.SkullList;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.*;

public class ViewAchievementGUI {
    private final KInventory kInventory;
    private static final int PLAYERS_PER_PAGE = 27;

    public ViewAchievementGUI(Achievement achievement, Player viewer, int page) {
        this.kInventory = new KInventory(54, "§3§lJoueurs - " + achievement.getName() + " §7(Page " + (page + 1) + ")");

        KItem ach = getkItem(AchievementManager.getPlayerData(viewer).getProgress(achievement.getId()), achievement);
        this.kInventory.setElement(4, ach);

        List<PlayerSuccessInfo> playersWithAchievement = new ArrayList<>();
        for (UUID uuid : getAllSavedPlayerUUIDs()) {
            PlayerAchievementData data = AchievementManager.loadPlayerAchievementData(uuid);
            PlayerAchievementData.AchievementProgress progress = data.getProgress(achievement.getId());
            if (progress != null && progress.isCompleted() && progress.getCompletionDate() != null) {
                playersWithAchievement.add(new PlayerSuccessInfo(uuid, progress.getCompletionDate()));
            }
        }

        playersWithAchievement.sort(Comparator.comparing(PlayerSuccessInfo::getCompletionDate).reversed());

        int totalPages = (int) Math.ceil(playersWithAchievement.size() / (double) PLAYERS_PER_PAGE);
        int start = page * PLAYERS_PER_PAGE;
        int end = Math.min(start + PLAYERS_PER_PAGE, playersWithAchievement.size());

        KItem glass = new KItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 3));
        for (int i = 0; i < 9; i++) this.kInventory.setElement(i, glass);
        for (int i = 36; i < 54; i++) this.kInventory.setElement(i, glass);

        int slot = 9;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        for (int i = start; i < end; i++) {
            PlayerSuccessInfo info = playersWithAchievement.get(i);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(info.getUuid());

            KItem skull = new KItem(new ItemCreator(Material.PAPER).get());
            skull.setName("§8┃ §e" + offlinePlayer.getName());
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add("§8§l» §7Débloqué le : §b" + sdf.format(info.getCompletionDate()));
            lore.add("");
            lore.add("§8§l» §6Cliquez §fpour ouvrir les succès de ce joueur.");
            lore.add("");
            skull.setDescription(lore);
            skull.addCallback((kInventory1, item, player, clickContext) -> {
                new PlayerAchievementsGUI(offlinePlayer, player, 0).getkInventory().open(player);
            });
            kInventory.setElement(slot++, skull);
        }

        if (page > 0) {
            KItem prev = new KItem(new ItemStack(Material.ARROW));
            prev.setName("§8┃ §6Page précédente");
            prev.addCallback((inv, item, p, ctx) -> new ViewAchievementGUI(achievement, viewer, page - 1).getkInventory().open(viewer));
            kInventory.setElement(48, prev);
        }
        if (page < totalPages - 1) {
            KItem next = new KItem(new ItemStack(Material.ARROW));
            next.setName("§8┃ §6Page suivante");
            next.addCallback((inv, item, p, ctx) -> new ViewAchievementGUI(achievement, viewer, page + 1).getkInventory().open(viewer));
            kInventory.setElement(50, next);
        }

        KItem back = new KItem(new ItemStack(Material.ARROW));
        back.setName("§8┃ §6Retour aux succès");
        back.addCallback((inv, item, p, ctx) -> new PlayerAchievementsGUI(viewer, viewer, 0).getkInventory().open(viewer));
        kInventory.setElement(49, back);
    }

    public KInventory getkInventory() {
        return kInventory;
    }


    private static List<UUID> getAllSavedPlayerUUIDs() {
        List<UUID> list = new ArrayList<>();
        java.io.File folder = new java.io.File("plugins/UHC/achievements/");
        java.io.File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
        if (files != null) {
            for (java.io.File f : files) {
                try {
                    list.add(UUID.fromString(f.getName().replace(".json", "")));
                } catch (Exception ignored) {}
            }
        }
        return list;
    }

    private static class PlayerSuccessInfo {
        private final UUID uuid;
        private final Date completionDate;

        public PlayerSuccessInfo(UUID uuid, Date completionDate) {
            this.uuid = uuid;
            this.completionDate = completionDate;
        }

        public UUID getUuid() {
            return uuid;
        }

        public Date getCompletionDate() {
            return completionDate;
        }
    }


    private static @NotNull KItem getkItem(PlayerAchievementData.AchievementProgress progress, Achievement achievement) {
        KItem achItem = new KItem(progress != null && progress.isCompleted() ? SkullList.GREEN_BALL.getItemStack() : SkullList.RED_BALL.getItemStack());
        achItem.setName((progress != null && progress.isCompleted() ? "§8┃ §a" : "§8┃ §c")  + (achievement.isSecret() && progress != null || !Objects.requireNonNull(progress).isCompleted() && achievement.isSecret() ? "§k" : "") + achievement.getName());
        List<String> lore = new ArrayList<>();
        lore.add("");
        if (progress != null) {
            lore.add("§8§l» §7Progression : §e" + progress.getStatus() + "§7/§b" + achievement.getRequired() + " §8┃ " + getProgressBar(progress.getStatus(), achievement.getRequired(), 10, "§a", "§7"));
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
        if (achievement.isSecret()) {
            if (progress.isCompleted()) {
                lore.add("§8┃ §f" + achievement.getDescription());
            }
            else lore.add("§8┃ §c§k" + achievement.getDescription());
        }
        else lore.add("§8┃ §f" + achievement.getDescription());

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
