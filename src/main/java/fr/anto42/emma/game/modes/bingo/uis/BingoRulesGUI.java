package fr.anto42.emma.game.modes.bingo.uis;

import fr.anto42.emma.UHC;
import fr.anto42.emma.game.modes.bingo.BingoModule;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.skulls.SkullList;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BingoRulesGUI {
    private final KInventory kInventory;
    @org.jetbrains.annotations.NotNull
    private final BingoModule module;

    public BingoRulesGUI(BingoModule module) {
        this.kInventory = new KInventory(54, UHC.getInstance().getPrefix() + module.getName());
        this.module = module;
        update();
    }
    private void update() {
        KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 3).get());
        this.kInventory.setElement(0, glass);
        this.kInventory.setElement(1, glass);
        this.kInventory.setElement(9, glass);
        this.kInventory.setElement(8, glass);
        this.kInventory.setElement(7, glass);
        this.kInventory.setElement(17, glass);
        this.kInventory.setElement(36, glass);
        this.kInventory.setElement(44, glass);
        this.kInventory.setElement(45, glass);
        this.kInventory.setElement(46, glass);
        this.kInventory.setElement(52, glass);
        this.kInventory.setElement(53, glass);
        KItem back = new KItem(new ItemCreator(SkullList.LEFT_AROOW.getItemStack()).name("§8┃ §cRevenir en arrière").lore("", "§8┃ §cVous ne trouvez pas §fce que vous souhaitez ?", "§8┃ §aPas de soucis§f, revenez en arrière !", "", "§8§l» §6Cliquez §fpour ouvrir.").get());
        back.addCallback((kInventoryRepresentation, itemStack, player1, kInventoryClickContext) -> {
            player1.closeInventory();
        });
        this.kInventory.setElement(46, back);


        int[] gridSlots = generateGridSlots(module.getBingoConfig().getCartSize());
        List<ItemStack> bingoItems = module.getBingoList();

        for (int i = 0; i < gridSlots.length && i < bingoItems.size(); i++) {
            ItemStack material = bingoItems.get(i);
            KItem item = new KItem(new ItemCreator(material).name("§6" + material.getType().name()).get());
            this.kInventory.setElement(gridSlots[i], item);
        }
    }
    private int[] generateGridSlots(int size) {
        int[][] grids = {
                {},
                {},
                {21, 22, 30, 31},
                {20, 21, 22, 29, 30, 31, 38, 39, 40},
                {11, 12, 13, 14, 20, 21, 22, 23, 29, 30, 31, 32, 38, 39, 40, 41},
                {2, 3, 4, 5, 6, 11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42},
                {1, 2, 3, 4, 5, 6, 10, 11, 12, 13, 14, 15, 19, 20, 21, 22, 23, 24, 28, 29, 30, 31, 32, 33, 37, 38, 39, 40, 41, 42, 46, 47, 48, 49, 50, 51}
        };

        return size >= 2 && size <= 6 ? grids[size] : grids[5];
    }


    public KInventory getkInventory() {
        update();
        return kInventory;
    }
}
