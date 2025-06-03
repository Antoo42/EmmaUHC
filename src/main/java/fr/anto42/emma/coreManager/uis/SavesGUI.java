package fr.anto42.emma.coreManager.uis;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.listeners.customListeners.ConfigSavedEvent;
import fr.anto42.emma.coreManager.scenarios.ScenarioManager;
import fr.anto42.emma.coreManager.scenarios.UHCScenario;
import fr.anto42.emma.game.UHCGame;
import fr.anto42.emma.game.impl.UHCData;
import fr.anto42.emma.game.impl.config.UHCConfig;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.skulls.SkullList;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SavesGUI {
    private final KInventory kInventory;
    private static final String SAVE_FOLDER = "plugins/UHC/saves/";

    public SavesGUI(Player player, boolean priv, String type, int page) {
        UHC uhcInstance = UHC.getInstance();
        UHCGame uhcGame = uhcInstance.getUhcGame();
        UHCData uhcData = uhcGame.getUhcData();
        UHCConfig uhcConfig = uhcGame.getUhcConfig();

        this.kInventory = new KInventory(54, UHC.getInstance().getConfig().getString("generalPrefix").replace("&", "§") + " §6§lSauvegardes");

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

        KItem back = new KItem(new ItemCreator(SkullList.LEFT_AROOW.getItemStack()).name("§8┃ §cRevenir en arrière").lore("", "§8┃ §cVous ne trouvez pas §fce que vous souhaitez ?", "§8┃ §aPas de soucis§f, revenez en arrière !", "", "§8§l» §6Cliquez §fpour ouvrir.").get());
        back.addCallback((kInventoryRepresentation, itemStack, player5, kInventoryClickContext) -> {
            UHC.getInstance().getUhcManager().getConfigMainGUI().open(player5);
        });
        this.kInventory.setElement(49, back);

        KItem filter = new KItem(new ItemCreator(Material.PAPER).name("§8┃ §fFiltre").lore("", "§8┃ §fStatut :§b " + (priv ? "vos configurations" : "toutes"), "", "§8§l» §6Cliquez §fpour modifier.").get());
        filter.addCallback((kInventory1, item, player1, clickContext) -> {
            new SavesGUI(player, !priv, "all", 0).getkInventory().open(player);
        });
        this.kInventory.setElement(7, filter);

        KItem save = new KItem(new ItemCreator(SkullList.LIME_BALL.getItemStack()).name("§8┃ §aCréer une nouvelle sauvegarde").lore("", "§8┃ §6Cliquez §fpour ajouter une nouvelle sauvegarde", "§8┃ §fet ainsi vous §aépargner du temps §fde configuration", "", "§8§l» §6Cliquez §fpour sauvegarder.").get());
        save.addCallback((kInventoryRepresentation, itemStack, player4, kInventoryClickContext) -> {
            String uuid = RandomStringUtils.random(5, true, false) + "-" + RandomStringUtils.random(5, false, true);
            uhcConfig.setOfficial(false);
            uhcConfig.setCreator(uhcData.getHostName());
            uhcConfig.getScenarios().clear();
            uhcInstance.getUhcManager().getScenarioManager().getActivatedScenarios().forEach(uhcScenario -> {
                uhcConfig.getScenarios().add(uhcScenario.getName());
            });

            File saveFolder = new File(SAVE_FOLDER);
            if (!saveFolder.exists() && !saveFolder.mkdirs()) {
                UHC.getUHCPlayer(player4).sendClassicMessage("§cErreur : impossible de créer le dossier de sauvegarde !");
                return;
            }

            File saveFile = new File(saveFolder, uhcData.getHostName() + " - " + uhcConfig.getUHCName() + "§6 - " + uuid + ".json");

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile))) {
                String json = uhcInstance.getSaveSerializationManager().serialize(uhcConfig);
                writer.write(json);
                UHC.getUHCPlayer(player4).sendClassicMessage("§aSauvegarde créée avec succès !");
                Bukkit.getPluginManager().callEvent(new ConfigSavedEvent(uhcData.getHostPlayer()));
            } catch (IOException e) {
                UHC.getUHCPlayer(player4).sendClassicMessage("§cErreur lors de la sauvegarde !");
                e.printStackTrace();
            }
            new SavesGUI(player4, false, type, 0).getkInventory().open(player4);
        });
        this.kInventory.setElement(4, save);

        File folder = new File(SAVE_FOLDER);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));

        if (files != null) {
            Arrays.sort(files, (f1, f2) -> {
                try {
                    String configName1 = extractConfigNameFromFile(f1);
                    String configName2 = extractConfigNameFromFile(f2);
                    return configName1.compareToIgnoreCase(configName2);
                } catch (Exception e) {
                    return f1.getName().compareToIgnoreCase(f2.getName());
                }
            });
        }

        int maxPerPage = 27;
        assert files != null;
        int totalPages = (files.length + maxPerPage - 1) / maxPerPage;

        if (files.length == 0) {
            KItem noSave = new KItem(new ItemCreator(SkullList.COMMANDBLOCK_RED.getItemStack()).name("§8┃ §cAucune sauvegarde trouvée").lore("", "§8┃ §fAucune sauvegarde n'a été trouvée.", "", "§8§l» §6Cliquez §fpour rafraîchir.").get());
            this.kInventory.setElement(22, noSave);
        } else {
            int start = page * maxPerPage;
            int end = Math.min(start + maxPerPage, files.length);
            UHCConfig uhcConfig1 = null;
            int slot = 9;
            for (int i = start; i < end; i++) {
                File file = files[i];
                String[] strings = file.getName().split(" - ");
                String creator = strings[0];
                String tempName = strings[1];
                if (priv && !creator.contains(player.getName())) continue;
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String json = reader.lines().reduce("", (a, b) -> a + b);
                    uhcConfig1 = uhcInstance.getSaveSerializationManager().deserialize(json);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                UHCConfig finalUhcConfig = uhcConfig1;
                KItem saveItem = new KItem(new ItemCreator(finalUhcConfig.isOfficial() ? SkullList.BLOCK_COMMANDBLOCK_GREEN.getItemStack() : SkullList.BLOCK_COMMANDBLOCK_DEFAULT.getItemStack()).get());
                saveItem.setName("§8┃ §6" + tempName + (finalUhcConfig.isOfficial() ? " §7(officielle)" : ""));

                saveItem.setDescription(player1 -> {
                    List<String> lore = new ArrayList<>();

                    lore.add("");
                    lore.add("§8§l» §7Créateur: §b" + creator);
                    lore.add("");
                    lore.add("§8§l» §7Slots: §c" + finalUhcConfig.getSlots());
                    lore.add("§8§l» §7PvP: §c" + finalUhcConfig.getPvp() + "§7 min");
                    lore.add("§8§l» §7Roles: §c" + finalUhcConfig.getRoles() + "§7 min");
                    lore.add("");
                    lore.add("§8§l» §7Bordure de départ: §c" + finalUhcConfig.getStartBorderSize());
                    lore.add("§8§l» §7Bordure de fin: §c" + finalUhcConfig.getFinalBorderSize());
                    lore.add("§8§l» §7Blocks/s: §c" + finalUhcConfig.getBlockPerS());
                    lore.add("");

                    lore.add((finalUhcConfig.getScenarios().isEmpty() ? "§8§l» §cAucun scénario" : "§8§l» §7Scénarios:"));
                    for (String scenario : finalUhcConfig.getScenarios()) {
                        lore.add("  §8§l» §a" + scenario);
                    }
                    lore.add("");
                    lore.add("§8§l» §6Cliquez §fpour charger.");
                    lore.add("§8§l» §6Jetez §fpour supprimer.");

                    return lore;
                });

                saveItem.addCallback((kInventoryRepresentation, itemStack, player3, kInventoryClickContext) -> {
                    if (kInventoryClickContext.getInventoryAction().name().contains("DROP")) {
                        if (finalUhcConfig.isOfficial()) return;
                        if (!Bukkit.getPlayer(player3.getName()).isOp()) return;
                        new DeleteFileGUI(file, getkInventory()).getkInventory().open(player);
                    } else {
                        uhcGame.setUhcConfig(finalUhcConfig);
                        uhcInstance.getUhcManager().getScenarioManager().resetScenarios();
                        Bukkit.getScheduler().runTaskLater(UHC.getInstance(), () -> {
                            finalUhcConfig.getScenarios().forEach(s -> {
                                uhcInstance.getUhcManager().getScenarioManager().activateScenerio(uhcInstance.getUhcManager().getScenarioManager().getScenario(s));
                            });
                            UHC.getUHCPlayer(player3).sendClassicMessage("§aConfiguration chargée !");
                        }, 10L);

                    }
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

    private String extractConfigNameFromFile(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String json = reader.lines().reduce("", (a, b) -> a + b);
            UHCConfig config = UHC.getInstance().getSaveSerializationManager().deserialize(json);
            return config.getUHCName();
        }
    }

    public KInventory getkInventory() {
        return kInventory;
    }
}
