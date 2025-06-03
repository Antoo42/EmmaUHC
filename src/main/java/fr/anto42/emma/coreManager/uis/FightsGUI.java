package fr.anto42.emma.coreManager.uis;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.spec.DamageEvent;
import fr.anto42.emma.coreManager.spec.FightDetectionSystem;
import fr.anto42.emma.coreManager.spec.FightSession;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.skulls.SkullList;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class FightsGUI {
    private final KInventory kInventory;
    private final Player viewer;
    private final FightDetectionSystem fightSystem;

    public FightsGUI(Player viewer) {
        this.viewer = viewer;
        this.fightSystem = UHC.getInstance().getSpecManager().getFightDetectionSystem();
        this.kInventory = new KInventory(54, UHC.getInstance().getConfig().getString("modPrefix").replace("&", "§") + " §6§lCombats en cours");

        setupInventory();
        startAutoRefresh();
    }

    private void setupInventory() {
        setupBorders();

        setupRefreshButton();

        setupBackButton();

        displayActiveFights();

        setupInfoButton();
    }

    private void setupBorders() {
        for (int i = 0; i < 9; i++) {
            KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 14).get());
            this.kInventory.setElement(i, glass);
            this.kInventory.setElement(45 + i, glass);
        }

        for (int i = 9; i < 45; i += 9) {
            KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 14).get());
            this.kInventory.setElement(i, glass);
            this.kInventory.setElement(i + 8, glass);
        }
    }

    private void setupRefreshButton() {
        KItem refresh = new KItem(new ItemCreator(SkullList.COMMANDBLOCK_RED.getItemStack())
                .name("§8┃ §aActualiser")
                .lore("",
                        "§8┃ §7Actualisez la liste des combats",
                        "§8┃ §7en cours en temps réel.",
                        "",
                        "§8§l» §6Cliquez §fpour actualiser.")
                .get());

        refresh.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            displayActiveFights();
            player.sendMessage("§8§l» §aListe des combats actualisée !");
        });

        this.kInventory.setElement(49, refresh);
    }

    private void setupBackButton() {
        KItem back = new KItem(new ItemCreator(SkullList.LEFT_AROOW.getItemStack())
                .name("§8┃ §cRetour")
                .lore("",
                        "§8┃ §7Retournez au menu principal",
                        "§8┃ §7de modération.",
                        "",
                        "§8§l» §6Cliquez §fpour revenir.")
                .get());

        back.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            player.closeInventory();
        });

        this.kInventory.setElement(48, back);
    }

    private void setupInfoButton() {
        Collection<FightSession> fights = fightSystem.getActiveFights();

        KItem info = new KItem(new ItemCreator(Material.BOOK)
                .name("§8┃ §eInformations")
                .lore("",
                        "§8┃ §7Combats actifs: §c" + fights.size(),
                        "§8┃ §7Spectateurs connectés: §a" + UHC.getInstance().getSpecManager().getSpectators().size(),
                        "",
                        "§8┃ §7Cliquez sur un combat pour:",
                        "§8┃ §f• Vous téléporter sur place",
                        "§8┃ §f• Suivre un participant",
                        "§8┃ §f• Voir les détails",
                        "",
                        "§8§l» §6Mise à jour automatique")
                .get());

        this.kInventory.setElement(50, info);
    }

    private void displayActiveFights() {
        for (int i = 10; i < 45; i++) {
            if (i % 9 != 0 && i % 9 != 8) {
                this.kInventory.setElement(i, null);
            }
        }

        Collection<FightSession> activeFights = fightSystem.getActiveFights();

        if (activeFights.isEmpty()) {
            KItem noFights = new KItem(new ItemCreator(Material.BARRIER)
                    .name("§8┃ §cAucun combat en cours")
                    .lore("",
                            "§8┃ §7Il n'y a actuellement aucun",
                            "§8┃ §7combat actif sur le serveur.",
                            "",
                            "§8┃ §7La liste se mettra à jour",
                            "§8┃ §7automatiquement dès qu'un",
                            "§8┃ §7combat commencera.",
                            "",
                            "§8§l» §6Actualisation automatique")
                    .get());

            this.kInventory.setElement(22, noFights);
            return;
        }

        int slot = 10;
        for (FightSession fight : activeFights) {
            if (slot >= 44 || slot % 9 == 0 || slot % 9 == 8) {
                slot++;
                if (slot % 9 == 0 || slot % 9 == 8) slot += 1;
                if (slot >= 44) break;
            }

            createFightItem(fight, slot);
            slot++;
        }
    }

    private void createFightItem(FightSession fight, int slot) {
        List<String> participants = new ArrayList<>();
        List<Player> playerList = new ArrayList<>();

        for (UUID uuid : fight.getParticipants()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                participants.add(player.getName());
                playerList.add(player);
            }
        }

        if (participants.isEmpty()) return;

        long duration = (System.currentTimeMillis() - fight.getStartTime()) / 1000;
        String durationStr = formatDuration(duration);
        double totalDamage = fight.getDamageEvents().stream()
                .mapToDouble(DamageEvent::getDamage)
                .sum();

        boolean isIntense = fight.isIntenseFight();
        Material material = isIntense ? Material.DIAMOND_SWORD : Material.IRON_SWORD;
        String intensityColor = isIntense ? "§c§l" : "§e";
        String intensityText = isIntense ? "INTENSE" : "Normal";

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§8┃ §7Participants:");
        for (String participant : participants) {
            Player p = Bukkit.getPlayer(participant);
            String health = p != null ? String.format("%.1f", p.getHealth()) : "?";
            lore.add("§8┃ §c• " + participant + " §7(" + health + "❤)");
        }
        lore.add("");
        lore.add("§8┃ §7Durée: §f" + durationStr);
        lore.add("§8┃ §7Intensité: " + intensityColor + intensityText);
        lore.add("§8┃ §7Dégâts totaux: §c" + String.format("%.1f", totalDamage) + "❤");
        lore.add("§8┃ §7Localisation:");
        lore.add("§8┃ §f  X: " + (int) fight.getLocation().getX());
        lore.add("§8┃ §f  Y: " + (int) fight.getLocation().getY());
        lore.add("§8┃ §f  Z: " + (int) fight.getLocation().getZ());
        lore.add("");
        lore.add("§8§l» §6Clic gauche §fpour vous téléporter");
        if (!playerList.isEmpty()) {
            lore.add("§8§l» §6Clic droit §fpour suivre " + playerList.get(0).getName());
        }

        KItem fightItem = new KItem(new ItemCreator(material)
                .name("§8┃ " + intensityColor + "Combat #" + fight.getFightId().toString().substring(0, 8))
                .lore(lore.toArray(new String[0]))
                .get());

        fightItem.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            if (kInventoryClickContext.getClickType().isLeftClick()) {
                player.teleport(fight.getLocation());
                player.sendMessage("§8§l» §aTéléportation au combat !");
                player.closeInventory();
            } else if (kInventoryClickContext.getClickType().isRightClick() && !playerList.isEmpty()) {
                player.closeInventory();
                Bukkit.dispatchCommand(player, "spec follow " + playerList.get(0).getName());
            }
        });

        this.kInventory.setElement(slot, fightItem);
    }

    private String formatDuration(long seconds) {
        if (seconds < 60) {
            return seconds + "s";
        } else {
            long minutes = seconds / 60;
            long remainingSeconds = seconds % 60;
            return minutes + "m " + remainingSeconds + "s";
        }
    }

    private void startAutoRefresh() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!viewer.isOnline()) {
                    this.cancel();
                    return;
                }

                if (!viewer.getOpenInventory().getTitle().contains("Combats en cours")) {
                    this.cancel();
                    return;
                }

                displayActiveFights();
            }
        }.runTaskTimer(UHC.getInstance(), 60L, 20L);
    }

    public KInventory getkInventory() {
        return kInventory;
    }
}
