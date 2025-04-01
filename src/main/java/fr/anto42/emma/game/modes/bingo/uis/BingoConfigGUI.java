package fr.anto42.emma.game.modes.bingo.uis;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.uis.config.GameModeGUI;
import fr.anto42.emma.game.modes.bingo.BingoModule;
import fr.anto42.emma.game.modes.stp.SwitchModule;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.skulls.SkullList;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BingoConfigGUI {
    private final KInventory kInventory;

    public BingoConfigGUI(BingoModule module){
        this.kInventory = new KInventory(54, UHC.getInstance().getPrefix() + module.getName());
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
            new GameModeGUI().getkInventory().open(player);
        });
        this.kInventory.setElement(49, back);

        KItem radius = new KItem(new ItemCreator(Material.COMPASS).name("§8┃ §fTaille de la carte").lore("", "§8§l» §fStatut: §c" + module.getBingoConfig().getCartSize(), "", "§8┃ §fConfigurer la taille de la carte", "§8┃ §fdu bingo", "", "§8§l» §6Clique-gauche §fpour ajouter 1.", "§8§l» §6Clique-droit §fpour retirer 1.").get());
        radius.addCallback((kInventory1, item, player, clickContext) -> {
            if (clickContext.getClickType().isLeftClick()) {
                if (module.getBingoConfig().getCartSize() >= 6)
                    return;
                module.getBingoConfig().setCartSize(module.getBingoConfig().getCartSize() + 1);
                module.resetBingo();
            }
            if (clickContext.getClickType().isRightClick()) {
                if (module.getBingoConfig().getCartSize() <= 2)
                    return;
                module.getBingoConfig().setCartSize(module.getBingoConfig().getCartSize() - 1);
                module.resetBingo();
            }
            radius.setItem(new ItemCreator(Material.COMPASS).name("§8┃ §fTaille de la carte").lore("", "§8§l» §fStatut: §c" + module.getBingoConfig().getCartSize(), "", "§8┃ §fConfigurer la taille de la carte", "§8┃ §fdu bingo", "", "§8§l» §6Clique-gauche §fpour ajouter 1.", "§8§l» §6Clique-droit §fpour retirer 1.").get());
        });

        this.kInventory.setElement(20, radius);

        KItem bingo = new KItem(new ItemCreator(Material.NETHER_STAR).name("§8┃ §fGrille de bingo").lore("", "§8┃ §fConsultez la grille du bingo", "", "§8§l» §6Clique-gauche §fpour ouvrir.", "§8§l» §6Clique-droit §fpour §crecharger une grille.").get());
        bingo.addCallback((kInventory1, item, player, clickContext) -> {
            if (clickContext.getClickType().isLeftClick()) {
                new BingoGUI(module, null, getkInventory()).getkInventory().open(player);
            } else if (clickContext.getClickType().isRightClick()) {
                module.resetBingo();
            }
        });
        this.kInventory.setElement(22, bingo);


        KItem firstWin = new KItem(new ItemCreator(Material.REDSTONE_COMPARATOR).name("§8┃ §fUniquement un vainqueur").lore("", "§8§l» §fStatut: " + (module.getBingoConfig().isFirstWin() ? "§a✔" : "§cdésactivé"), "", "§8┃ §fChoisissez de finir la partie", "§8┃ §fdès qu'un joueur a finit son bingo", "§8┃ §fou laisser les joueurs finir le leur", "", "§8§l» §6Cliquez §fpour configurer.").get());
        firstWin.addCallback((kInventory1, item, player, clickContext) -> {
            module.getBingoConfig().setFirstWin(!module.getBingoConfig().isFirstWin());
            firstWin.setItem(new ItemCreator(Material.REDSTONE_COMPARATOR).name("§8┃ §fUniquement un vainqueur").lore("", "§8§l» §fStatut: " + (module.getBingoConfig().isFirstWin() ? "§a✔" : "§cdésactivé"), "", "§8┃ §fChoisissez de finir la partie", "§8┃ §fdès qu'un joueur a finit son bingo", "§8┃ §fou laisser les joueurs finir le leur", "", "§8§l» §6Cliquez §fpour configurer.").get());
        });
        this.kInventory.setElement(24, firstWin);
    }

    public KInventory getkInventory() {
        return kInventory;
    }
}
