package fr.anto42.emma.coreManager.uis;

import fr.anto42.emma.UHC;
import fr.anto42.emma.utils.SoundUtils;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.skulls.SkullList;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SavesGUI {
    private final KInventory kInventory;
    public SavesGUI(Player player, boolean priv) {
        priv = true;
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

        KItem back = new KItem(new ItemCreator(SkullList.LEFT_AROOW.getItemStack()).name("§8┃ §cRevenir en arrière").lore("", "§8┃ §cVous ne trouvez pas §fce que vous souhaitez ?", "§8┃ §aPas de soucis§f, revenez en arrière !", "", "§8§l» §6Cliquez §fpour ouvrir.").get());
        back.addCallback((kInventoryRepresentation, itemStack, player5, kInventoryClickContext) -> {
            UHC.getInstance().getUhcManager().getConfigMainGUI().open(player5);
        });
        this.kInventory.setElement(49, back);

        KItem save = new KItem(new ItemCreator(SkullList.LIME_BALL.getItemStack())
                .name("§8┃ §aCréer une nouvelle sauvegarde")
                .lore("", "§8┃ §6Cliquez §fpour ajouter une nouvelle sauvegarde", "§8┃ §fet ainsi vous §aépargner du temps §fde configuration", "", "§8§l» §6Cliquez §fpour sauvegarder.")
                .get());
        save.addCallback((kInventoryRepresentation, itemStack, player4, kInventoryClickContext) -> {
            if (!UHC.getInstance().getUhcGame().getUhcData().getHostPlayer().equals(UHC.getUHCPlayer(player4))) {
                UHC.getUHCPlayer(player4).sendClassicMessage("§cSeul l'Host de la partie peut créer une nouvelle sauvegarde !");
                SoundUtils.playSoundToPlayer(player4, Sound.VILLAGER_NO);
                return;
            }

            /*Firestore db = UHC.getInstance().getDb();
            UHCConfig config = UHC.getInstance().getUhcGame().getUhcConfig();
            String newSave = UHC.getInstance().getSaveSerializationManager().serialize(config);
            ApiFuture<DocumentReference> future = db.collection("saves").add(newSave);

            try {
                DocumentReference documentReference = future.get();
                UHC.getUHCPlayer(player4).sendClassicMessage("§aNouvelle sauvegarde créée avec succès : " + documentReference.getId());
            } catch (Exception e) {
                e.printStackTrace();
                UHC.getUHCPlayer(player4).sendClassicMessage("§cErreur lors de la création de la sauvegarde !");
            }*/
        });
        this.kInventory.setElement(4, save);

        /*Firestore db = UHC.getInstance().getDb();
        int slot = 9;
        try {
            ApiFuture<QuerySnapshot> future = db.collection("saves")
                    .whereEqualTo("owner", player.getUniqueId().toString())
                    .get();
            for (DocumentSnapshot document : future.get().getDocuments()) {
                String json = document.getString("configJson");
                String saveName = document.getString("name");
                UUID owner = UUID.fromString(Objects.requireNonNull(document.getString("owner")));

                KItem saveItem = new KItem(new ItemCreator(SkullList.BLOCK_COMMANDBLOCK_DEFAULT.getItemStack())
                        .name("§8┃ §6" + saveName)
                        .lore("", "§8§l» §6Cliquez §fpour charger.", "§8§l» §6Jetez §fpour supprimer.")
                        .get());

                saveItem.addCallback((kInventoryRepresentation, itemStack, player3, kInventoryClickContext) -> {
                    if (kInventoryClickContext.getInventoryAction() == InventoryAction.DROP_ONE_SLOT || kInventoryClickContext.getInventoryAction() == InventoryAction.DROP_ALL_SLOT) {
                        if (!owner.equals(player3.getUniqueId())) {
                            UHC.getUHCPlayer(player3).sendClassicMessage("§cSeul le créateur de la configuration peut supprimer cette dernière !");
                            SoundUtils.playSoundToPlayer(player3, Sound.VILLAGER_NO);
                            return;
                        }
                        db.collection("saves").document(document.getId()).delete();
                        UHC.getUHCPlayer(player3).sendClassicMessage("§aSauvegarde supprimée avec succès !");
                    } else {
                        try {
                            UHCConfig loadedConfig = UHC.getInstance().getSaveSerializationManager().deserialize(json);
                            UHC.getInstance().getUhcGame().setUhcConfig(loadedConfig);
                            UHC.getInstance().getUhcManager().setScenarioManager(new ScenarioManager());
                            PlayersUtils.broadcastMessage("§7La nouvelle configuration chargée est " + loadedConfig.getUHCName() + "§7.");
                        } catch (Exception e) {
                            UHC.getUHCPlayer(player3).sendClassicMessage("§cErreur lors du chargement de la configuration !");
                            e.printStackTrace();
                        }
                    }
                });

                this.kInventory.setElement(slot, saveItem);
                slot++;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }*/
    }

    public KInventory getkInventory() {
        return kInventory;
    }
}




