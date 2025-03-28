package fr.anto42.emma.coreManager.uis.config.worlds;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.worldManager.WorldManager;
import fr.anto42.emma.game.GameState;
import fr.anto42.emma.utils.players.SoundUtils;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.skulls.SkullList;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;

public class CreateWorldGUI {
    private final KInventory kInventory;

    public CreateWorldGUI() {
        this.kInventory = new KInventory(54, UHC.getInstance().getPrefix() + " §6§lCréer un nouveau monde");
        for (int i = 0; i < 9; i++) {
            KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 5).get());
            this.kInventory.setElement(i, glass);
            this.kInventory.setElement(45 + i, glass);
        }
        for (int i = 36; i < 45; i++) {
            KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 5).get());
            this.kInventory.setElement(i, glass);
        }
        KItem back = new KItem(new ItemCreator(SkullList.LEFT_AROOW.getItemStack()).name("§8┃ §cRevenir en arrière").lore("", "§8┃ §cVous ne trouvez pas §fce que vous souhaitez ?", "§8┃ §aPas de soucis§f, revenez en arrière !", "", "§8§l» §6Cliquez §fpour ouvrir.").get());
        back.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            UHC.getInstance().getUhcManager().getWorldConfgGUI().open(player);
        });
        this.kInventory.setElement(49, back);


        KItem settings = new KItem(new ItemCreator(Material.REDSTONE_COMPARATOR).name("§8┃ §fMondes spéciaux").lore("", "§8┃ §cA vos risques et périls...", "", "§c§o  Cette option n'est pas encore disponible !", "§c§o  Prévue dans une future mise à jour !").get());
        settings.addCallback((kInventory1, item, player, clickContext) -> {
            new SettingsNewWorldGUI().getkInventory().open(player);
        });
        this.kInventory.setElement(21, settings);

        KItem createWorld = new KItem(new ItemCreator(SkullList.GREEN_BALL.getItemStack()).name("§8┃ §fCréer un nouveau monde").lore("", "§8┃ §fLe monde de jeu actuel §cne vous plaît pas §f?", "§8┃ §aRe-créez en un facilement §fque vous pouvez configuré", "§8┃ §fau préalable avec l'option §eparamètres du monde", "", "§8§l» §6Cliquez §fpour sélectionner").get());
        createWorld.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            if (!UHC.getInstance().getUhcGame().getGameState().equals(GameState.WAITING)){
                UHC.getUHCPlayer(player).sendClassicMessage("§cHop hop hop tu ne peux pas faire ça maintenant !");
                SoundUtils.playSoundToPlayer(player, Sound.VILLAGER_NO);
                return;
            }
            Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §cCréation d'un nouveau monde ! Le serveur peut par conséquant subir des ralentissements.");
            WorldManager.setRoofed(false);
            WorldManager.setClean(false);
            UHC.getInstance().getUhcGame().getUhcData().setNetherPreload(false);
            UHC.getInstance().getUhcGame().getUhcData().setPreloadFinished(false);
            UHC.getInstance().getUhcGame().getUhcData().setEndPreload(false);
            UHC.getInstance().getWorldManager().createGameWorld();
        });
        this.kInventory.setElement(23, createWorld);
    }

    public KInventory getkInventory() {
        return kInventory;
    }
}
