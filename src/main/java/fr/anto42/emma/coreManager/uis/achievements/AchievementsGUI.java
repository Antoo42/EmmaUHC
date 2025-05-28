package fr.anto42.emma.coreManager.uis.achievements;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.achievements.Achievement;
import fr.anto42.emma.coreManager.achievements.AchievementManager;
import fr.anto42.emma.coreManager.achievements.PlayerAchievementData;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.skulls.SkullList;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class AchievementsGUI {
    private enum FilterType {
        ALL("Tous", 4),
        COMPLETED("Complétés", 5),
        UNCOMPLETED("Non terminés", 14);

        private final String name;
        private final int colorData;

        FilterType(String name, int colorData) {
            this.name = name;
            this.colorData = colorData;
        }

        public String getName() { return name; }
        public int getColorData() { return colorData; }

        public FilterType next() {
            switch (this) {
                case ALL:
                    return COMPLETED;
                case COMPLETED:
                    return UNCOMPLETED;
                case UNCOMPLETED:
                    return ALL;
                default:
                    return ALL;
            }
        }
    }

    private final KInventory kInventory;
    private static final int MAX_PER_PAGE = 27;
    private final FilterType currentFilter;

    public AchievementsGUI(Player player, int page) {
        this(player, page, FilterType.ALL);
    }

    public AchievementsGUI(Player player, int page, FilterType filter) {
        this.currentFilter = filter;
        this.kInventory = new KInventory(54, UHC.getInstance().getPrefix() + "§6§lSuccès UHC");

        KItem glass = new KItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 3));
        for (int i = 0; i < 9; i++) {
            this.kInventory.setElement(i, glass);
            this.kInventory.setElement(45 + i, glass);
        }
        for (int i = 36; i < 45; i++) {
            this.kInventory.setElement(i, glass);
        }

        KItem back = new KItem(new ItemCreator(SkullList.LEFT_AROOW.getItemStack())
                .name("§8┃ §cFermer le menu")
                .lore("", "§8§l» §6Cliquez §fpour fermer.").get());
        back.addCallback((inv, item, p, ctx) -> p.closeInventory());
        this.kInventory.setElement(49, back);

        List<Achievement> achievements = AchievementManager.getAllAchievements();
        PlayerAchievementData data = AchievementManager.getPlayerData(player);

        List<AchievementWithProgress> filteredAchievementsWithProgress = new ArrayList<>();
        for (Achievement achievement : achievements) {
            PlayerAchievementData.AchievementProgress progress = data.getProgress(achievement.getId());
            boolean completed = progress != null && progress.isCompleted();
            switch (filter) {
                case ALL:
                    filteredAchievementsWithProgress.add(new AchievementWithProgress(achievement, progress));
                    break;
                case COMPLETED:
                    if (completed) filteredAchievementsWithProgress.add(new AchievementWithProgress(achievement, progress));
                    break;
                case UNCOMPLETED:
                    if (!completed) filteredAchievementsWithProgress.add(new AchievementWithProgress(achievement, progress));
                    break;
            }
        }

        switch (filter) {
            case ALL:
                trierTous(filteredAchievementsWithProgress);
                break;
            case COMPLETED:
                trierCompletes(filteredAchievementsWithProgress);
                break;
            case UNCOMPLETED:
                trierNonCompletes(filteredAchievementsWithProgress);
                break;
        }

        List<Achievement> sortedAchievements = new ArrayList<>();
        for (AchievementWithProgress awp : filteredAchievementsWithProgress) {
            sortedAchievements.add(awp.achievement);
        }

        int totalPages = (sortedAchievements.size() + MAX_PER_PAGE - 1) / MAX_PER_PAGE;
        int start = page * MAX_PER_PAGE;
        int end = Math.min(start + MAX_PER_PAGE, sortedAchievements.size());

        if (sortedAchievements.isEmpty()) {
            KItem empty = new KItem(new ItemStack(Material.BOOK));
            empty.setName("§cAucun succès trouvé");
            this.kInventory.setElement(22, empty);
        } else {
            int slot = 9;
            for (int i = start; i < end; i++) {
                Achievement achievement = sortedAchievements.get(i);
                PlayerAchievementData.AchievementProgress progress = data.getProgress(achievement.getId());
                KItem achItem = getkItem(progress, achievement);
                this.kInventory.setElement(slot++, achItem);
            }
        }

        if (page > 0) {
            KItem prev = new KItem(new ItemStack(Material.ARROW));
            prev.setName("§6Page précédente");
            prev.addCallback((inv, item, p, ctx) -> new AchievementsGUI(p, page - 1, filter).getkInventory().open(p));
            this.kInventory.setElement(3, prev);
        }
        if (page < totalPages - 1) {
            KItem next = new KItem(new ItemStack(Material.ARROW));
            next.setName("§6Page suivante");
            next.addCallback((inv, item, p, ctx) -> new AchievementsGUI(p, page + 1, filter).getkInventory().open(p));
            this.kInventory.setElement(5, next);
        }

        addFilterSwitchButton(player, page);
    }

    private void addFilterSwitchButton(Player player, int page) {
        FilterType nextFilter = currentFilter.next();
        KItem filterItem = new KItem(new ItemStack(Material.PAPER, 1, (byte) currentFilter.getColorData()));
        filterItem.setName("§8┃ §fFiltre");
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§8§l» §fStatut: §b" + currentFilter.getName());
        lore.add("");
        lore.add("§8§l» §6Cliquez §fpour passer à " + nextFilter.getName() + "§f.");
        filterItem.setDescription(lore);
        filterItem.addCallback((inv, item, p, ctx) ->
                new AchievementsGUI(p, 0, nextFilter).getkInventory().open(p));
        this.kInventory.setElement(7, filterItem);
    }

    private void trierTous(List<AchievementWithProgress> list) {
        List<AchievementWithProgress> completes = new ArrayList<>();
        List<AchievementWithProgress> nonCompletes = new ArrayList<>();

        for (AchievementWithProgress awp : list) {
            if (awp.progress != null && awp.progress.isCompleted()) {
                completes.add(awp);
            } else {
                nonCompletes.add(awp);
            }
        }

        completes.sort((a1, a2) -> {
            Date d1 = a1.progress.getCompletionDate();
            Date d2 = a2.progress.getCompletionDate();
            if (d1 == null && d2 == null) return 0;
            if (d1 == null) return 1;
            if (d2 == null) return -1;
            return d2.compareTo(d1);
        });

        list.sort(Comparator.comparingInt(a -> a.achievement.getRequired()));

        list.clear();
        list.addAll(completes);
        list.addAll(nonCompletes);
    }

    private void trierCompletes(List<AchievementWithProgress> list) {
        list.sort((a1, a2) -> {
            Date d1 = a1.progress.getCompletionDate();
            Date d2 = a2.progress.getCompletionDate();
            if (d1 == null && d2 == null) return 0;
            if (d1 == null) return 1;
            if (d2 == null) return -1;
            return d2.compareTo(d1);
        });
    }

    private void trierNonCompletes(List<AchievementWithProgress> list) {
        list.sort((a1, a2) ->
                a1.achievement.getName().compareToIgnoreCase(a2.achievement.getName()));
    }

    private static class AchievementWithProgress {
        final Achievement achievement;
        final PlayerAchievementData.AchievementProgress progress;

        AchievementWithProgress(Achievement achievement, PlayerAchievementData.AchievementProgress progress) {
            this.achievement = achievement;
            this.progress = progress;
        }
    }

    private static @NotNull KItem getkItem(PlayerAchievementData.AchievementProgress progress, Achievement achievement) {
        KItem achItem = new KItem(progress != null && progress.isCompleted() ? SkullList.GREEN_BALL.getItemStack() : SkullList.RED_BALL.getItemStack());
        achItem.setName((progress != null && progress.isCompleted() ? "§8┃ §a" : "§8┃ §c") + achievement.getName());
        List<String> lore = new ArrayList<>();
        lore.add("");
        if (progress != null) {
            lore.add("§8§l» §7Progression : §e" + progress.getStatus() + "§7/§b" + achievement.getRequired());
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
        lore.add("§8┃ §f" + achievement.getDescription());
        lore.add("");
        achItem.setDescription(lore);
        return achItem;
    }

    public KInventory getkInventory() {
        return kInventory;
    }
}
