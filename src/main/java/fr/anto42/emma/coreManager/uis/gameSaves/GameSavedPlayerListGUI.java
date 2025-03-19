package fr.anto42.emma.coreManager.uis.gameSaves;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.players.PlayerStats;
import fr.anto42.emma.utils.gameSaves.GameSave;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.saves.SaveSerializationManager;
import fr.anto42.emma.utils.skulls.SkullList;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameSavedPlayerListGUI {
    private final KInventory kInventory;
    private static final int maxPerPage = 27;

    public GameSavedPlayerListGUI(Player player, GameSave gameSave, KInventory previous, int page) {
        this.kInventory = new KInventory(54, UHC.getInstance().getPrefix() + " §7Game: " + gameSave.getGameID());

        KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 14).get());
        for (int i = 0; i < 9; i++) {
            this.kInventory.setElement(i, glass);
            this.kInventory.setElement(45 + i, glass);
        }
        for (int i = 36; i < 45; i++) {
            this.kInventory.setElement(i, glass);
        }

        KItem back = new KItem(new ItemCreator(SkullList.LEFT_AROOW.getItemStack())
                .name("§8┃ §cRevenir en arrière")
                .lore("", "§8┃ §cRetour au menu précédent", "", "§8§l» §6Cliquez §fpour revenir.")
                .get());
        back.addCallback((kInventoryRepresentation, itemStack, player1, kInventoryClickContext) -> {
            if (previous != null) {
                previous.open(player1);
            } else {
                player1.closeInventory();
            }
        });
        this.kInventory.setElement(49, back);

        List<String> playerDataList = gameSave.getUhcPlayerList();
        int totalPages = (playerDataList.size() + maxPerPage - 1) / maxPerPage;
        int start = page * maxPerPage;
        int end = Math.min(start + maxPerPage, playerDataList.size());

        int slot = 9;
        for (int i = start; i < end; i++) {
            PlayerStats playerStats = UHC.getInstance().getSaveSerializationManager().fromString(playerDataList.get(i));
            KItem kItem = new KItem(new ItemCreator(Material.PAPER)
                    .name("§8┃ §c" + playerStats.getName()).get());

            kItem.setDescription(player1 -> {
                List<String> lore = new ArrayList<>();
                lore.add("");

                lore.add(" §8§l» §7Kills: §c" + playerStats.getKills());
                lore.add(" §8§l» §7Morts: §c" + playerStats.getDeaths());
                lore.add(" §8§l» §7Diamants minés: §b" + playerStats.getDiamondsMined());
                lore.add(" §8§l» §7Or miné: §e" + playerStats.getGoldMined());
                lore.add(" §8§l» §7Fer miné: §f" + playerStats.getIronMined());
                lore.add(" §8§l» §7Equipe: §c" + (playerStats.getTeam() != null ? playerStats.getTeam() : "Aucune"));
                lore.add(" §8§l» §7Rôle: §c" + (playerStats.getRole() != null ? playerStats.getRole() : "Aucun"));
                lore.add(" §8§l» §7Statut: " + (playerStats.isAlive() ? "§aEn vie" : "§cMort"));
                lore.add(" §8§l» §7Dégâts: §c" + ((int) playerStats.getMakedDamages()) + "❤ infligés §8┃ §c" + ((int) playerStats.getReceivedDamages()) + "❤ reçus");
                lore.add("");
                lore.add(" §8§l» §7Événements");
                gameSave.getEvents().stream()
                        .filter(s -> s.contains(playerStats.getName()))
                        .forEach(s -> lore.add("  §8§l» §c" + SaveSerializationManager.fromEventString(s).getTimer() + "§f" + SaveSerializationManager.fromEventString(s).getString()));
                lore.add("");
                return lore;
            });

            this.kInventory.setElement(slot++, kItem);
        }

        if (page > 0) {
            KItem prevPage = new KItem(new ItemCreator(Material.ARROW)
                    .name("§8┃ §6Page précédente").get());
            prevPage.addCallback((kInventoryRepresentation, itemStack, player1, kInventoryClickContext) ->
                    new GameSavedPlayerListGUI(player1, gameSave, previous, page - 1).getkInventory().open(player1)
            );
            this.kInventory.setElement(3, prevPage);
        }

        if (page < totalPages - 1) {
            KItem nextPage = new KItem(new ItemCreator(Material.ARROW)
                    .name("§8┃ §6Page suivante").get());
            nextPage.addCallback((kInventoryRepresentation, itemStack, player1, kInventoryClickContext) ->
                    new GameSavedPlayerListGUI(player1, gameSave, previous, page + 1).getkInventory().open(player1)
            );
            this.kInventory.setElement(5, nextPage);
        }
    }

    public KInventory getkInventory() {
        return kInventory;
    }
}
