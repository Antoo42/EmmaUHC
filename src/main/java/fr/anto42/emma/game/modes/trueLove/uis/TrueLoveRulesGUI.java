package fr.anto42.emma.game.modes.trueLove.uis;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.uis.config.GameModeGUI;
import fr.anto42.emma.game.modes.trueLove.TrueLoveModule;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.skulls.SkullList;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;
import org.bukkit.Material;

public class TrueLoveRulesGUI {
    private final KInventory kInventory;

    public TrueLoveRulesGUI(TrueLoveModule switchModule){
        this.kInventory = new KInventory(54, UHC.getInstance().getPrefix() + " §5§lTrueLove");
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
        back.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> player.closeInventory());
        this.kInventory.setElement(49, back);


        KItem timer = new KItem(new ItemCreator(Material.BOW).name("§8┃ §fRayon de cupidon").lore("", "§8§l» §fStatut: §c" + switchModule.getRadius() + "§f blocks", "","§8┃ §6Configurez §fle champ d'action du §dcupidon", "").get());
        this.kInventory.setElement(22, timer);
    }

    public KInventory getkInventory() {
        return kInventory;
    }
}
