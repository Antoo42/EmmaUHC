package fr.anto42.emma.coreManager.scenarios.uis;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.scenarios.scFolder.TimeBomb;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.skulls.SkullList;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;
import org.bukkit.Material;

public class TimeBombGUI {
    private final KInventory kInventory;

    String translate(int number){
        if (number <= 0)
            return "§cdésactivé";
        else
            return "§c" + number;
    }

    public TimeBombGUI(TimeBomb noCleanUP) {
        this.kInventory = new KInventory(54, UHC.getInstance().getConfig().getString("generalPrefix").replace("&", "§") + " §6§lConfiguration de TimeBomb");

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

        KItem heart = new KItem(new ItemCreator(SkullList.TIMER.getItemStack()).name("§8┃ §fDélai de l'exlposion").lore("", "§8§l» §fStatut: §c" + translate((int) noCleanUP.getDelay()), "", "§8┃ §6Configurez §fle temps de cooldown de la détonation", "", "§8§l» §6Clique-gauche §fpour ajouter 1.", "§8§l» §6Clique-droit §fpour enlever 1.").get());
        heart.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            if (kInventoryClickContext.getClickType().isLeftClick())
                noCleanUP.addDelay();
            else if (kInventoryClickContext.getClickType().isRightClick())
                noCleanUP.removeDelay();
            heart.setItem(new ItemCreator(SkullList.TIMER.getItemStack()).name("§8┃ §fDélai de l'exlposion").lore("", "§8§l» §fStatut: §c" + translate((int) noCleanUP.getDelay()), "", "§8┃ §6Configurez §fle temps de cooldown de la détonation", "", "§8§l» §6Clique-gauche §fpour ajouter 1.", "§8§l» §6Clique-droit §fpour enlever 1.").get());
        });
        this.kInventory.setElement(21, heart);

        KItem radius = new KItem(new ItemCreator(SkullList.CHEST.getItemStack()).name("§8┃ §fRayon de l'explosion").lore("", "§8§l» §fStatut: §c" + translate((int) noCleanUP.getRadius()), "", "§8┃ §6Configurez §fle rayon d'action de la détonation", "", "§8§l» §6Clique-gauche §fpour ajouter 1.", "§8§l» §6Clique-droit §fpour enlever 1.").get());
        radius.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            if (kInventoryClickContext.getClickType().isLeftClick())
                noCleanUP.addRadius();
            else if (kInventoryClickContext.getClickType().isRightClick())
                noCleanUP.removeRadius();
            radius.setItem(new ItemCreator(SkullList.CHEST.getItemStack()).name("§8┃ §fRayon de l'explosion").lore("", "§8§l» §fStatut: §c" + translate((int) noCleanUP.getRadius()), "", "§8┃ §6Configurez §fle rayon d'action de la détonation", "", "§8§l» §6Clique-gauche §fpour ajouter 1.", "§8§l» §6Clique-droit §fpour enlever 1.").get());
        });
        this.kInventory.setElement(23, radius);
    }

    public KInventory getkInventory() {
        return kInventory;
    }
}
