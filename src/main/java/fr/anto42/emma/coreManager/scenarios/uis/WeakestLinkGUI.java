package fr.anto42.emma.coreManager.scenarios.uis;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.scenarios.scFolder.WeakestLink;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.skulls.SkullList;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;
import org.bukkit.Material;

public class WeakestLinkGUI {

    private final KInventory kInventory;

    String translate(int number){
        if (number <= 0) return "§cdésactivé";
        else if (number == 1) return "§c" + number + "§f minute";
        else return "§c" + number + "§f minutes";
    }

    public WeakestLinkGUI(WeakestLink weakestLink) {
        this.kInventory = new KInventory(54, UHC.getInstance().getPrefix() + " §6§lConfiguration de WeakestLink");

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

        KItem timer = new KItem(new ItemCreator(SkullList.TIMER.getItemStack()).name("§8┃ §fTimer exterminateur").lore("", "§8§l» §fStatut: §c" + translate(weakestLink.getTimer()), "", "§8┃ §6Configurez §fl'intervalle à laquelle", "§8┃ §fle joueur avec le moins de vie sucombera", "", "§8§l» §6Clique-gauche §fpour ajouter 1.", "§8§l» §6Clique-droit §fpour enlever 1.").get());
        timer.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            if (kInventoryClickContext.getClickType().isLeftClick()){
                if (weakestLink.getTimer() == 100)
                    return;
                weakestLink.setTimer(weakestLink.getTimer() + 1);

            }else if (kInventoryClickContext.getClickType().isRightClick()) {
                if (weakestLink.getTimer() == 0)
                    return;
                weakestLink.setTimer(weakestLink.getTimer() - 1);
            }
            timer.setItem(new ItemCreator(SkullList.HEART.getItemStack()).name("§8┃ §fTimer exterminateur").lore("", "§8§l» §fStatut: §c" + translate(weakestLink.getTimer()), "", "§8┃ §6Configurez §fl'intervalle à laquelle", "§8┃ §fle joueur avec le moins de vie sucombera", "", "§8§l» §6Clique-gauche §fpour ajouter 1.", "§8§l» §6Clique-droit §fpour enlever 1.").get());
        });
        this.kInventory.setElement(22, timer);

    }

    public KInventory getkInventory() {
        return kInventory;
    }
}
