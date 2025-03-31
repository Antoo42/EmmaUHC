package fr.anto42.emma.game.modes.bingo;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BingoGenerator {
    public static List<ItemStack> getValidItems() {
        return Arrays.asList(
                /*new ItemStack(Material.DIRT), new ItemStack(Material.GRASS), new ItemStack(Material.COBBLESTONE),
                new ItemStack(Material.STONE), new ItemStack(Material.SAND), new ItemStack(Material.GRAVEL),
                new ItemStack(Material.CLAY), new ItemStack(Material.LOG), new ItemStack(Material.LOG_2), new ItemStack(Material.WOOD),*/

                new ItemStack(Material.WATER_BUCKET), new ItemStack(Material.LAVA_BUCKET),

                new ItemStack(Material.STICK), new ItemStack(Material.TORCH), new ItemStack(Material.COAL),
                new ItemStack(Material.IRON_INGOT), new ItemStack(Material.GOLD_INGOT), new ItemStack(Material.DIAMOND),
                new ItemStack(Material.FLINT), new ItemStack(Material.STRING), new ItemStack(Material.FEATHER),

                /*new ItemStack(Material.APPLE), new ItemStack(Material.BREAD), new ItemStack(Material.CARROT),
                new ItemStack(Material.POTATO), new ItemStack(Material.BAKED_POTATO), new ItemStack(Material.COOKED_BEEF),
                new ItemStack(Material.COOKED_CHICKEN), new ItemStack(Material.COOKED_MUTTON), new ItemStack(Material.COOKED_FISH),
                new ItemStack(Material.COOKED_RABBIT),*/

                new ItemStack(Material.WHEAT), new ItemStack(Material.MELON_SEEDS), new ItemStack(Material.PUMPKIN_SEEDS),
                new ItemStack(Material.MELON), new ItemStack(Material.PUMPKIN), new ItemStack(Material.SUGAR_CANE),
                new ItemStack(Material.CACTUS),

                new ItemStack(Material.LEATHER), new ItemStack(Material.WOOL), new ItemStack(Material.RAW_BEEF),
                new ItemStack(Material.RAW_CHICKEN), new ItemStack(Material.RAW_FISH), new ItemStack(Material.COOKED_MUTTON),
                new ItemStack(Material.GRILLED_PORK), new ItemStack(Material.RABBIT_FOOT), new ItemStack(Material.EGG),

                new ItemStack(Material.BONE), new ItemStack(Material.PAPER), new ItemStack(Material.BOOK),
                new ItemStack(Material.INK_SACK), new ItemStack(Material.BOWL), new ItemStack(Material.FLOWER_POT),

                new ItemStack(Material.LEATHER_HELMET), new ItemStack(Material.LEATHER_CHESTPLATE),
                new ItemStack(Material.LEATHER_LEGGINGS), new ItemStack(Material.LEATHER_BOOTS),

                new ItemStack(Material.IRON_HELMET), new ItemStack(Material.IRON_CHESTPLATE),
                new ItemStack(Material.IRON_LEGGINGS), new ItemStack(Material.IRON_BOOTS),

                new ItemStack(Material.GOLD_HELMET), new ItemStack(Material.GOLD_CHESTPLATE),
                new ItemStack(Material.GOLD_LEGGINGS), new ItemStack(Material.GOLD_BOOTS),

                new ItemStack(Material.DIAMOND_HELMET), new ItemStack(Material.DIAMOND_CHESTPLATE),
                new ItemStack(Material.DIAMOND_LEGGINGS), new ItemStack(Material.DIAMOND_BOOTS),

                /*new ItemStack(Material.WOOD_DOOR), new ItemStack(Material.BIRCH_DOOR), new ItemStack(Material.SPRUCE_DOOR),
                new ItemStack(Material.JUNGLE_DOOR), new ItemStack(Material.ACACIA_DOOR), new ItemStack(Material.DARK_OAK_DOOR),*/

                new ItemStack(Material.GLASS), new ItemStack(Material.STAINED_GLASS),

                new ItemStack(Material.DIAMOND_BLOCK), new ItemStack(Material.GOLD_BLOCK),new ItemStack(Material.LAPIS_BLOCK), new ItemStack(Material.EMERALD_BLOCK), new ItemStack(Material.IRON_BLOCK)
                );
    }

    public static List<ItemStack> generateBingoGrid(int size) {
        List<ItemStack> possibleItems = getValidItems();

        if (possibleItems.size() < size * size) {
            throw new IllegalArgumentException("Pas assez d'items pour une grille de cette taille !");
        }

        Collections.shuffle(possibleItems);
        return possibleItems.subList(0, size * size);
    }


    public static void displayBingoGrid(List<ItemStack> grid, int size) {
        System.out.println("===== Bingo Grid =====");
        for (int i = 0; i < grid.size(); i++) {
            System.out.print(grid.get(i) + "  ");
            if ((i + 1) % size == 0) System.out.println();
        }
    }
}
