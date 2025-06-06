package fr.anto42.emma.coreManager.scenarios.uis;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.scenarios.scFolder.XHealths;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.skulls.SkullList;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;
import org.bukkit.Material;

public class XHealthsGUI {
    private KInventory kInventory;

    public XHealthsGUI (XHealths xHealths){
        this.kInventory = new KInventory(54, UHC.getInstance().getPrefix() + " §6§lConfiguration de XHealths");

        for (int i = 0; i < 9; i++) {
            KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 6).get());
            this.kInventory.setElement(i, glass);
            this.kInventory.setElement(45 + i, glass);
        }
        for (int i = 36; i < 45; i++) {
            KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 6).get());
            this.kInventory.setElement(i, glass);
        }

        KItem back = new KItem(new ItemCreator(SkullList.LEFT_AROOW.getItemStack()).name("§8┃ §cRevenir en arrière").lore("", "§8┃ §cVous ne trouvez pas §fce que vous souhaitez ?", "§8┃ §aPas de soucis§f, revenez en arrière !", "", "§8§l» §6Cliquez §fpour ouvrir.").get());
        back.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            UHC.getInstance().getUhcManager().getScenariosConfigGUI().open(player);
        });
        this.kInventory.setElement(49, back);

        KItem chance = new KItem(new ItemCreator(SkullList.HEART.getItemStack()).name("§8┃ §fNombre de demi-coeurs de départ").lore("", "§8§l» §fStatut: §c" + xHealths.getH() + " demi-coeurs", "", "§8┃ §6Configurez §fle nombre de demi-§dcoeurs", "§8┃ §fde départ", "", "§8§l» §6Clique-gauche §fpour augmenter de 1.", "§8§l» §6Clique-droit §fpour retirer 1.").get());
        chance.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            if (kInventoryClickContext.getClickType().isLeftClick())
                xHealths.addH();
            else if (kInventoryClickContext.getClickType().isRightClick())
                xHealths.increaseH();
            chance.setItem(new ItemCreator(SkullList.HEART.getItemStack()).name("§8┃ §fNombre de demi-coeurs de départ").lore("", "§8§l» §fStatut: §c" + xHealths.getH() + " demi-coeurs", "", "§8┃ §6Configurez §fle nombre de demi-§dcoeurs", "§8┃ §fde départ", "", "§8§l» §6Clique-gauche §fpour augmenter de 1.", "§8§l» §6Clique-droit §fpour retirer 1.").get());
        });
        this.kInventory.setElement(22, chance);
    }

    public KInventory getkInventory() {
        return kInventory;
    }
}
