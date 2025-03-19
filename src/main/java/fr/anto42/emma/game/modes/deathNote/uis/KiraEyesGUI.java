package fr.anto42.emma.game.modes.deathNote.uis;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.game.modes.deathNote.DeathNoteModule;
import fr.anto42.emma.game.modes.deathNote.impl.DNRole;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KiraEyesGUI {
    private KInventory kInventory;
    private final DeathNoteModule dn;

    public KiraEyesGUI(KiraMainGUI kiraMainInventory, DeathNoteModule dn){
        this.dn = dn;
        this.kInventory = new KInventory(KItem.DEFAULT, 54, UHC.getInstance().getPrefix() + " §8§lYeux de la mort");

        KItem kItem = new KItem(new ItemCreator(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 13)).name("§8┃ §aAccepter").lore("", "§8┃ §fVous permet de §cvoir la vie §7au dessus des joueurs.", "§8┃ §fCoût: §b coeurs permanents§7.", "", "§8§l» §6Cliquez §fpour sélectionner.").get());
        kItem.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            player.closeInventory();
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
            UHCPlayer uhcPlayer = UHC.getUHCPlayer(player);
            player.setMaxHealth(player.getMaxHealth() - 6);
            uhcPlayer.sendClassicMessage("§aVous avez débloqué les Yeux du dieu de la mort.");
            ((DNRole) uhcPlayer.getRole()).setSeeLife(true);
        });

        KItem kItem2 = new KItem(new ItemCreator(new ItemStack(Material.ARROW)).name("§cAnnuler").lore("§7Vous permet de fermer ce menu.", "").get());
        kItem2.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            player.closeInventory();
            UHC.getUHCPlayer(player).sendClassicMessage("§cVous avez annulé l'échange.");
        });
        KItem kItem1 = new KItem(new ItemCreator(Material.EYE_OF_ENDER).name("§8┃ §cPouvoir des Yeux").lore("§8┃ §fLe pouvoir des §cyeux des yeux de la mort", "§8┃ §fVous permet de voir la vie des autres joueurs de", "§8┃ §fla partie en échange de §c3 coeurs permanents§f.").get());

        this.kInventory.setElement(20, kItem);
        this.kInventory.setElement(22, kItem1);
        this.kInventory.setElement(24, kItem2);

    }

    public void open(Player player){
        this.kInventory.open(player);
    }
}
