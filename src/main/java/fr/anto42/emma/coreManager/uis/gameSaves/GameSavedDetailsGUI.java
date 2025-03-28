package fr.anto42.emma.coreManager.uis.gameSaves;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.players.PlayerStats;
import fr.anto42.emma.coreManager.uis.rules.RulesGUI;
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

public class GameSavedDetailsGUI {
    private final KInventory kInventory;
    public GameSavedDetailsGUI(Player player, GameSave gameSave, KInventory previous) {
        this.kInventory = new KInventory(54, UHC.getInstance().getPrefix() + " §7Game: " + gameSave.getGameID());
        for (int i = 0; i < 9; i++) {
            KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 14).get());
            this.kInventory.setElement(i, glass);
            this.kInventory.setElement(45 + i, glass);
        }
        for (int i = 36; i < 45; i++) {
            KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 14).get());
            this.kInventory.setElement(i, glass);
        }
        KItem back = new KItem(new ItemCreator(SkullList.LEFT_AROOW.getItemStack()).name("§8┃ §cRevenir en arrière").lore("", "§8┃ §cVous ne trouvez pas §fce que vous souhaitez ?", "§8┃ §aPas de soucis§f, revenez en arrière !", "", "§8§l» §6Cliquez §fpour ouvrir.").get());
        back.addCallback((kInventoryRepresentation, itemStack, player1, kInventoryClickContext) -> {
            if (previous != null) {
                previous.open(player1);
            }
            else player1.closeInventory();
        });
        this.kInventory.setElement(49, back);

        KItem stats = new KItem(new ItemCreator(Material.SKULL_ITEM, 1 , (byte) 3).owner(player.getName()).name("§8┃ §fVos statistiques").get());
        stats.setDescription(player1 -> {
            List<String> lore = new ArrayList<>();
            lore.add("");
            String playerDataString = null;
            for (String playerData : gameSave.getUhcPlayerList()) {
                if (playerData.contains("name=" + player.getName())) {
                    playerDataString = playerData;
                    break;
                }
            }
            if (playerDataString != null) {
                PlayerStats playerStats = UHC.getInstance().getSaveSerializationManager().fromString(playerDataString);
                lore.add(" §8§l» §7Kills: §c" + playerStats.getKills());
                lore.add(" §8§l» §7Morts: §c" + playerStats.getDeaths());
                lore.add(" §8§l» §7Diamants minés: §b" + playerStats.getDiamondsMined());
                lore.add(" §8§l» §7Or miné: §e" + playerStats.getGoldMined());
                lore.add(" §8§l» §7Fer miné: §f" + playerStats.getIronMined());
                lore.add(" §8§l» §7Equipe: §c" + (playerStats.getTeam() != null ? playerStats.getTeam() : "Aucune"));
                lore.add(" §8§l» §7Rôle: §c" + (playerStats.getRole() != null ? playerStats.getRole() : "Aucun"));
                lore.add(" §8§l» §7Statut: §a" + (playerStats.isAlive() ? "§aEn vie" : "§cMort"));
                lore.add(" §8§l» §7Degats: §c" + ((int) playerStats.getMakedDamages()) +  "❤ infligés §8┃ §c" + ((int) playerStats.getReceivedDamages()) +  "❤ reçues");
                lore.add("");
                lore.add(" §8§l» §7Evenements");
                gameSave.getEvents().stream().filter(s -> s.contains(player.getName())).forEach(s -> {
                    lore.add("  §8§l» §e" + SaveSerializationManager.fromEventString(s).getTimer() + "§f: " + SaveSerializationManager.fromEventString(s).getString());
                });
                lore.add("");
                lore.add("§8§l» §6Cliquez §fpour voir les messages envoyés par ce joueur.");
                stats.addCallback((kInventory1, item, player2, clickContext) -> {
                    new MessagesSentByPlayerGUI(playerStats.getName(), getkInventory(), gameSave).getkInventory().open(player2);
                });
            } else {
                lore.add("§8§l» §cAucune donnée disponible.");
            }
            return lore;
        });


        this.kInventory.setElement(20, stats);


        KItem playersList = new KItem(new ItemCreator(Material.BOOK).name("§8┃ §fListe des joueurs de la partie").lore("", "§8┃ §fObtenez des informations sur la partie de chacun", "", "§8§l» §6§lCliquez§f pour ouvrir.").get());
        playersList.addCallback((kInventory1, item, player1, clickContext) -> {
            new GameSavedPlayerListGUI(player, gameSave, getkInventory(), 0).getkInventory().open(player1);
        });
        this.kInventory.setElement(22, playersList);

        KItem eventsList = new KItem(new ItemCreator(Material.PAPER).name("§8┃ §fListe des events de la partie").lore("", "§8┃ §fObtenez des informations sur la partie", "", "§8§l» §6§lCliquez§f pour ouvrir.").get());
        eventsList.addCallback((kInventory1, item, player1, clickContext) -> {
            new GameSavedEventsGUI(gameSave, getkInventory(), 0, null).getkInventory().open(player1);
        });
        this.kInventory.setElement(24, eventsList);
    }


    public KInventory getkInventory() {
        return kInventory;
    }
}
