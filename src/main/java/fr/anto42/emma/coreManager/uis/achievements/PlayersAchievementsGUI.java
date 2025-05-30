package fr.anto42.emma.coreManager.uis.achievements;

import fr.anto42.emma.UHC;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PlayersAchievementsGUI{
    private final KInventory kInventory;

    public PlayersAchievementsGUI(Player viewer, int page) {
        this.kInventory = new KInventory(54, UHC.getInstance().getPrefix() + "§3§lSuccès des joueurs");
        int slot = 9;
        int perPage = 27;

        List<UUID> allUUIDs = getAllSavedPlayerUUIDs();
        int totalPages = (allUUIDs.size() + perPage - 1) / perPage;
        int start = page * perPage;
        int end = Math.min(start + perPage, allUUIDs.size());
        KItem glass = new KItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 3));
        for (int i = 0; i < 9; i++) {
            this.kInventory.setElement(i, glass);
            this.kInventory.setElement(45 + i, glass);
        }
        for (int i = 36; i < 45; i++) {
            this.kInventory.setElement(i, glass);
        }
        for (int i = start; i < end; i++) {
            UUID uuid = allUUIDs.get(i);
            OfflinePlayer off = Bukkit.getOfflinePlayer(uuid);

            KItem kItem = new KItem(new ItemCreator(Material.PAPER).name("§8┃ §e " + off.getName()).get());
            int completed = getCompletedAchievementsCount(uuid);
            int total = AchievementManager.getAllAchievements().size();
            kItem.setDescription(Arrays.asList("", "§8§l» §7Succès : §a" + completed + "§7/§b" + total, "§8§l» §6Cliquez §fpour voir le détail."));
            kItem.addCallback((inv, itemStack, p, ctx) -> new PlayerAchievementsGUI(off, viewer, 0).getkInventory().open(viewer));
            kInventory.setElement(slot++, kItem);
        }

        if (page > 0) {
            KItem prev = new KItem(new ItemStack(Material.ARROW));
            prev.setName("§8┃ §6Page précédente");
            prev.addCallback((inv, item, p, ctx) -> new PlayersAchievementsGUI(viewer, page - 1).getkInventory().open(viewer));
            kInventory.setElement(45, prev);
        }
        if (page < totalPages - 1) {
            KItem next = new KItem(new ItemStack(Material.ARROW));
            next.setName("§8┃ §6Page suivante");
            next.addCallback((inv, item, p, ctx) -> new PlayersAchievementsGUI(viewer, page + 1).getkInventory().open(viewer));
            kInventory.setElement(53, next);
        }

        KItem back = new KItem(new ItemCreator(SkullList.LEFT_AROOW.getItemStack())
                .name("§8┃ §cFermer le menu")
                .lore("", "§8§l» §6Cliquez §fpour fermer.").get());
        back.addCallback((inv, item, p, ctx) -> p.closeInventory());
        kInventory.setElement(49, back);
    }

    public KInventory getkInventory() {
        return kInventory;
    }

    private List<UUID> getAllSavedPlayerUUIDs() {
        File folder = new File("plugins/UHC/achievements/");
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
        List<UUID> list = new ArrayList<>();
        if (files != null) {
            for (File f : files) {
                try {
                    list.add(UUID.fromString(f.getName().replace(".json", "")));
                } catch (Exception ignored) {}
            }
        }
        return list;
    }

    private int getCompletedAchievementsCount(UUID uuid) {
        PlayerAchievementData data = AchievementManager.loadPlayerAchievementData(uuid); // à adapter à ta méthode de chargement
        int count = 0;
        for (Achievement ach : AchievementManager.getAllAchievements()) {
            PlayerAchievementData.AchievementProgress progress = data.getProgress(ach.getId());
            if (progress != null && progress.isCompleted()) count++;
        }
        return count;
    }
}
