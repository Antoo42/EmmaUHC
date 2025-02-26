package fr.anto42.emma.coreManager.uis;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.scenarios.UHCScenario;
import fr.anto42.emma.game.UHCGame;
import fr.anto42.emma.game.impl.UHCData;
import fr.anto42.emma.game.impl.config.UHCConfig;
import fr.anto42.emma.utils.SoundUtils;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.skulls.SkullList;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class SavesGUI {
    private final KInventory kInventory;
    private static final String SAVE_FOLDER = "plugins/UHC/saves/";

    public SavesGUI(Player player, boolean priv, String type) {
        priv = true;
        UHC uhcInstance = UHC.getInstance();
        UHCGame uhcGame = uhcInstance.getUhcGame();
        UHCData uhcData = uhcGame.getUhcData();
        UHCConfig uhcConfig = uhcGame.getUhcConfig();

        this.kInventory = new KInventory(54, UHC.getInstance().getConfig().getString("generalPrefix").replace("&", "§") + " §6§lSauvegardes");

        // Bordures en verre rouge
        for (int i = 0; i < 9; i++) {
            KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 14).get());
            this.kInventory.setElement(i, glass);
            this.kInventory.setElement(45 + i, glass);
        }
        for (int i = 36; i < 45; i++) {
            KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 14).get());
            this.kInventory.setElement(i, glass);
        }

        // Vérifier et créer le dossier de sauvegarde
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

        KItem save = new KItem(new ItemCreator(SkullList.LIME_BALL.getItemStack()).name("§8┃ §aCréer une nouvelle sauvegarde").lore("", "§8┃ §6Cliquez §fpour ajouter une nouvelle sauvegarde", "§8┃ §fet ainsi vous §aépargner du temps §fde configuration", "", "§8§l» §6Cliquez §fpour sauvegarder.").get());
        save.addCallback((kInventoryRepresentation, itemStack, player4, kInventoryClickContext) -> {
            String uuid = RandomStringUtils.random(5, true, false) + "-" + RandomStringUtils.random(5, false, true);

            uhcConfig.setCreator(uhcData.getHostName());

            File saveFolder = new File(SAVE_FOLDER);
            if (!saveFolder.exists() && !saveFolder.mkdirs()) {
                UHC.getUHCPlayer(player4).sendClassicMessage("§cErreur : impossible de créer le dossier de sauvegarde !");
                return;
            }

            File saveFile = new File(saveFolder, uhcData.getHostName() + " - " + uhcConfig.getUHCName() + " - " + uuid + ".json");

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile))) {
                String json = uhcInstance.getSaveSerializationManager().serialize(uhcConfig);
                writer.write(json);
                UHC.getUHCPlayer(player4).sendClassicMessage("§aSauvegarde créée avec succès !");
            } catch (IOException e) {
                UHC.getUHCPlayer(player4).sendClassicMessage("§cErreur lors de la sauvegarde !");
                e.printStackTrace();
            }
            new SavesGUI(player4, false, type).getkInventory().open(player4);
        });
        this.kInventory.setElement(4, save);

        File folder = new File(SAVE_FOLDER);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null || files.length == 0) {
            KItem noSave = new KItem(new ItemCreator(SkullList.COMMANDBLOCK_RED.getItemStack()).name("§8┃ §cAucune sauvegarde trouvée").lore("", "§8┃ §fAucune sauvegarde n'a été trouvée.", "", "§8§l» §6Cliquez §fpour rafraîchir.").get());
            this.kInventory.setElement(22, noSave);
        } else {
            int slot = 9;
            for (File file : files) {
                KItem saveItem = new KItem(new ItemCreator(SkullList.BLOCK_COMMANDBLOCK_DEFAULT.getItemStack()).name("§8┃ §6" + file.getName().replace(".json", "")).lore("", "§8§l» §6Cliquez §fpour charger.", "§8§l» §6Jetez §fpour supprimer.").get());
                saveItem.addCallback((kInventoryRepresentation, itemStack, player3, kInventoryClickContext) -> {
                    if (kInventoryClickContext.getInventoryAction().name().contains("DROP")) {
                        if (!Bukkit.getPlayer(player3.getName()).isOp())
                            return;
                        if (!file.delete()) {
                            UHC.getUHCPlayer(player3).sendClassicMessage("§cImpossible de supprimer cette sauvegarde !");
                        } else {
                            new DeleteFileGUI(file, getkInventory()).getkInventory().open(player);
                        }
                        new SavesGUI(player3, false, type).getkInventory().open(player3);
                    } else {
                        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                            StringBuilder jsonBuilder = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                jsonBuilder.append(line);
                            }

                            String json = jsonBuilder.toString();
                            uhcInstance.getUhcGame().setUhcConfig(uhcInstance.getSaveSerializationManager().deserialize(json));
                            UHC.getUHCPlayer(player3).sendClassicMessage("§aConfiguration §3" + file.getName().replace(".json", "") + "  §achargée avec succès !");

                            for (UHCScenario uhcScenario : uhcInstance.getUhcManager().getScenarioManager().getActivatedScenarios()) {
                                uhcScenario.onEnable();
                            }
                        } catch (IOException e) {
                            UHC.getUHCPlayer(player3).sendClassicMessage("§cErreur lors du chargement !");
                            e.printStackTrace();
                        }
                    }
                });
                this.kInventory.setElement(slot++, saveItem);
            }
        }
    }

    public KInventory getkInventory() {
        return kInventory;
    }
}
