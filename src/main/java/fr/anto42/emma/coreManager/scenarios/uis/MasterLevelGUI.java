package fr.anto42.emma.coreManager.scenarios.uis;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.scenarios.scFolder.MasterLevel;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.skulls.SkullList;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;
import org.bukkit.Material;

public class MasterLevelGUI {
    private final KInventory kInventory;

    String translate(int number){
        if (number <= 0)
            return "§cdésactivé";
        else
            return "§c" + number;
    }

    public MasterLevelGUI(MasterLevel masterLevel) {
        this.kInventory = new KInventory(54, UHC.getInstance().getPrefix() + " §6§lConfiguration de MasterLevel");

        for (int i = 0; i < 9; i++) {
            KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 3).get());
            this.kInventory.setElement(i, glass);
            this.kInventory.setElement(45 + i, glass);
        }
        for (int i = 36; i < 45; i++) {
            KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 3).get());
            this.kInventory.setElement(i, glass);
        }

        KItem back = new KItem(new ItemCreator(SkullList.LEFT_AROOW.getItemStack()).name("§8┃ §cRevenir en arrière").lore("", "§8┃ §cVous ne trouvez pas §fce que vous souhaitez ?", "§8┃ §aPas de soucis§f, revenez en arrière !", "", "§8§l» §6Cliquez §fpour ouvrir.").get());
        back.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            UHC.getInstance().getUhcManager().getScenariosConfigGUI().open(player);
        });
        this.kInventory.setElement(49, back);

        KItem heart = new KItem(new ItemCreator(SkullList.XP.getItemStack()).name("§8┃ §fNiveau d'expérience").lore("", "§8§l» §fStatut: §c" + translate(masterLevel.getLevel()), "", "§8┃ §6Configurez §favec combien de niveau d'expérience", "§8┃ §fles joueurs débuteront la partie", "", "§8§l» §6Clique-gauche §fpour ajouter 1.", "§8§l» §6Clique-droit §fpour enlever 1.").get());
        heart.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            if (kInventoryClickContext.getClickType().isLeftClick()){
                if (masterLevel.getLevel() == 500)
                    return;
                masterLevel.setLevel(masterLevel.getLevel() + 1);

            }else if (kInventoryClickContext.getClickType().isRightClick()) {
                if (masterLevel.getLevel() == 0)
                    return;
                masterLevel.setLevel(masterLevel.getLevel() - 1);
            }
            heart.setItem(new ItemCreator(SkullList.XP.getItemStack()).name("§8┃ §fNiveau d'expérience").lore("", "§8§l» §fStatut: §c" + translate(masterLevel.getLevel()), "", "§8┃ §6Configurez §favec combien de niveau d'expérience", "§8┃ §fles joueurs débuteront la partie", "", "§8§l» §6Clique-gauche §fpour ajouter 1.", "§8§l» §6Clique-droit §fpour enlever 1.").get());
        });
        this.kInventory.setElement(22, heart);
    }

    public KInventory getkInventory() {
        return kInventory;
    }
}
