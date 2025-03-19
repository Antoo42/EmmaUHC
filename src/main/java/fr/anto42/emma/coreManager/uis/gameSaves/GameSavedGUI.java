package fr.anto42.emma.coreManager.uis.gameSaves;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.players.PlayerStats;
import fr.anto42.emma.coreManager.uis.SavesGUI;
import fr.anto42.emma.utils.TimeUtils;
import fr.anto42.emma.utils.gameSaves.GameSave;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.saves.SaveSerializationManager;
import fr.anto42.emma.utils.skulls.SkullList;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GameSavedGUI {
    private final KInventory kInventory;
    private static final String SAVE_FOLDER = "plugins/UHC/games/";

    public GameSavedGUI(Player player, boolean priv, String type, int page) {
        UHC uhcInstance = UHC.getInstance();

        this.kInventory = new KInventory(54, UHC.getInstance().getConfig().getString("generalPrefix").replace("&", "§") + " §6§lGames jouées");

        for (int i = 0; i < 9; i++) {
            KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 14).get());
            this.kInventory.setElement(i, glass);
            this.kInventory.setElement(45 + i, glass);
        }
        for (int i = 36; i < 45; i++) {
            KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 14).get());
            this.kInventory.setElement(i, glass);
        }

        Path savePath = Paths.get(SAVE_FOLDER);
        if (!Files.exists(savePath)) {
            try {
                Files.createDirectories(savePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        KItem back = new KItem(new ItemCreator(SkullList.LEFT_AROOW.getItemStack()).name("§8┃ §cRevenir en arrière").lore("", "§8┃ §cVous ne trouvez pas §fce que vous souhaitez ?", "§8┃ §aPas de soucis§f, revenez en arrière !", "", "§8§l» §6Cliquez §fpour fermer.").get());
        back.addCallback((kInventoryRepresentation, itemStack, player5, kInventoryClickContext) -> {
            player.closeInventory();
        });
        this.kInventory.setElement(49, back);

        KItem filter = new KItem(new ItemCreator(Material.PAPER).name("§8┃ §fFiltre").lore("", "§8┃ §fStatut :§b " + (priv ? "vos parties" : "toutes"), "", "§8§l» §6Cliquez §fpour modifier.").get());
        filter.addCallback((kInventory1, item, player1, clickContext) -> {
            new GameSavedGUI(player, !priv, "all", 0).getkInventory().open(player);
        });
        this.kInventory.setElement(7, filter);


        File folder = new File(SAVE_FOLDER);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));

        int maxPerPage = 27;
        assert files != null;
        int totalPages = (files.length + maxPerPage - 1) / maxPerPage;

        if (files.length == 0) {
            KItem noSave = new KItem(new ItemCreator(SkullList.COMMANDBLOCK_RED.getItemStack()).name("§8┃ §cAucune game trouvée").lore("", "§8┃ §fAucune game n'a été trouvée.", "").get());
            this.kInventory.setElement(22, noSave);
        } else {
            int start = page * maxPerPage;
            int end = Math.min(start + maxPerPage, files.length);
            GameSave gameSave = null;
            int slot = 9;
            for (int i = start; i < end; i++) {
                File file = files[i];
                String[] strings = file.getName().split(" - ");
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String json = reader.lines().reduce("", (a, b) -> a + b);
                    gameSave = uhcInstance.getSaveSerializationManager().deserializeGame(json);
                    if (gameSave.getUhcPlayerList().contains(player.getName()) && priv)
                        return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                KItem saveItem = new KItem(new ItemCreator(Material.PAPER).get());
                GameSave finalGameSave = gameSave;
                saveItem.setName("§8┃ §6" + finalGameSave.getGameName());

                saveItem.setDescription(player1 -> {
                    List<String> lore = new ArrayList<>();

                    lore.add("§8" + finalGameSave.getGameID());
                    lore.add("");
                    lore.add("§8§l» §7Date: §b" + finalGameSave.getDate());
                    lore.add("");
                    lore.add("§8§l» §7Host: §b" + finalGameSave.getHost());
                    lore.add("§8§l» §7Mode de jeu: §b" + finalGameSave.getModule());
                    lore.add("§8§l» §7Type de monde: §b" + finalGameSave.getWorldType());
                    lore.add("");
                    lore.add("§8§l» §7Vainqueur: §b" + finalGameSave.getWinner());
                    lore.add("§8§l» §7Temps de jeu: §b" + TimeUtils.getFormattedTime(finalGameSave.getTimer()));
                    lore.add("");
                    lore.add("§8§l» §7Statistiques:");
                    String playerDataString = null;
                    for (String playerData : finalGameSave.getUhcPlayerList()) {
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

                        lore.add(" §8§l» §7Evenements");
                        finalGameSave.getEvents().stream().filter(s -> s.contains(player.getName())).forEach(s -> {
                            lore.add("  §8§l» §c" + SaveSerializationManager.fromEventString(s).getTimer() + "§f" + SaveSerializationManager.fromEventString(s).getString());
                        });
                    } else {
                        lore.add("§8§l» §7Aucune donnée disponible.");
                    }
                    lore.add("");
                    lore.add("§8§l» §6§lCliquez§f pour ouvrir.");


                    return lore;
                });
                saveItem.addCallback((kInventory1, item, player1, clickContext) -> {
                    new GameSavedDetailsGUI(player1, finalGameSave, getkInventory()).getkInventory().open(player1);
                });
                this.kInventory.setElement(slot++, saveItem);

            }
        }

        if (page > 0) {
            KItem prevPage = new KItem(new ItemCreator(Material.ARROW).name("§8┃ §6Page précédente").get());
            prevPage.addCallback((kInventoryRepresentation, itemStack, player2, kInventoryClickContext) -> new SavesGUI(player2, priv, type, page - 1).getkInventory().open(player2));
            this.kInventory.setElement(3, prevPage);
        }

        if (page < totalPages - 1) {
            KItem nextPage = new KItem(new ItemCreator(Material.ARROW).name("§8┃ §6Page suivante").get());
            nextPage.addCallback((kInventoryRepresentation, itemStack, player2, kInventoryClickContext) -> new SavesGUI(player2, priv, type, page + 1).getkInventory().open(player2));
            this.kInventory.setElement(5, nextPage);
        }
    }

    public KInventory getkInventory() {
        return kInventory;
    }
}
