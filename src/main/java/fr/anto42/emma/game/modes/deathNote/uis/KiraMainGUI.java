package fr.anto42.emma.game.modes.deathNote.uis;

import fr.anto42.emma.UHC;
import fr.anto42.emma.game.modes.deathNote.roles.Kira;
import fr.anto42.emma.game.modes.deathNote.utils.GameUtils;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

public class KiraMainGUI {
    private final KInventory kInventory;
    private final Kira kiraRole;
    private final KiraDeathNoteGUI kiraDnInventory;
    private final KiraEyesGUI kiraEyesInventory;


    public KiraMainGUI(Kira kiraRole){
        this.kiraRole = kiraRole;
        this.kiraDnInventory = new KiraDeathNoteGUI();
        this.kiraEyesInventory = new KiraEyesGUI(this, GameUtils.getModule());
        this.kInventory = new KInventory(InventoryType.HOPPER, UHC.getInstance().getPrefix() + " §8Menu du DeathNote");
        KItem kItem = new KItem(new ItemCreator(Material.BOOK_AND_QUILL).name("§8┃ §fUtiliser le DeathNote").lore("", "§8┃ §f§7Vous permet de §créduire la vie§7 du joueur visé.", "", "§8§l» §6Clique-gauche §fpour utiliser votre DeathNote.").get());
        kItem.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            /*if(!((DeathNoteModule) kiraRole.getModule()).isDeathNoteCanBeUsed()){
                player.closeInventory();
                UHC.getUHCPlayer(player).sendClassicMessage("§cLe pouvoir d'attaque du DeathNote ne peut être utilisé qu'entre la 5e et la 15e minute.");
            } else */
            if(!kiraRole.isKiraThisEp())
                kiraDnInventory.open(player);
            else
                UHC.getUHCPlayer(player).sendClassicMessage("§cVous avez déjà attaqué un joueur cet épisode-ci !");
        });
        this.kInventory.setElement(1, kItem);

        KItem kItem1 = new KItem(new ItemCreator(Material.EYE_OF_ENDER).name("§cYeux du Dieu de la Mort").lore("§7Vous permet de voir leur vie au dessus des joueurs.", "§7Vous perdrez §c3 coeurs permanant§7.", "", "§8» §cClique-gauche §7pour débloquer ce pouvoir.").get());
        kItem1.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            if(!kiraRole.isSeeLife())
                kiraEyesInventory.open(player);
            else {
                player.closeInventory();
                UHC.getUHCPlayer(player).sendClassicMessage("§cVous avez déjà fait ce pacte.");
            }
        });
        this.kInventory.setElement(3, kItem1);
    }

    public void open(Player player){
        this.kInventory.open(player);
    }

    public Kira getKira() {
        return kiraRole;
    }
}
