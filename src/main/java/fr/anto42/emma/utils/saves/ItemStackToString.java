package fr.anto42.emma.utils.saves;


import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Map;

public class ItemStackToString {

    public static String itemStackToString(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return "null";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(item.getType().toString()).append(":").append(item.getAmount());

        if (item.getDurability() != 0) {
            sb.append("|data=").append(item.getDurability());
        }

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (meta.hasDisplayName()) {
                sb.append("|name=").append(meta.getDisplayName());
            }
            if (meta.hasLore()) {
                sb.append("|lore=").append(String.join(";", meta.getLore()));
            }
            if (meta.hasEnchants()) {
                sb.append("|enchants=");
                for (Map.Entry<Enchantment, Integer> enchant : meta.getEnchants().entrySet()) {
                    sb.append(enchant.getKey().getName()).append("-").append(enchant.getValue()).append(",");
                }
                sb.setLength(sb.length() - 1);
            }
            if (meta.spigot().isUnbreakable()) {
                sb.append("|unbreakable=true");
            }
        }

        return sb.toString();
    }




    public static ItemStack ItemStackFromString(String itemString) {
        if (itemString == null || itemString.equals("null")) {
            return null;
        }

        try {
            String[] parts = itemString.split("\\|");
            String[] baseInfo = parts[0].split(":");

            Material material = Material.valueOf(baseInfo[0]);
            int amount = Integer.parseInt(baseInfo[1]);
            short data = 0;

            ItemStack itemStack = new ItemStack(material, amount);

            ItemMeta meta = itemStack.getItemMeta();

            for (int i = 1; i < parts.length; i++) {
                if (parts[i].startsWith("data=")) {
                    data = Short.parseShort(parts[i].substring(5));
                } else if (parts[i].startsWith("name=")) {
                    meta.setDisplayName(parts[i].substring(5));
                } else if (parts[i].startsWith("lore=")) {
                    meta.setLore(Arrays.asList(parts[i].substring(5).split(";")));
                } else if (parts[i].startsWith("enchants=")) {
                    String[] enchants = parts[i].substring(9).split(",");
                    for (String enchant : enchants) {
                        String[] enchantData = enchant.split("-");
                        Enchantment enchantment = Enchantment.getByName(enchantData[0]);
                        int level = Integer.parseInt(enchantData[1]);
                        if (enchantment != null) {
                            meta.addEnchant(enchantment, level, true);
                        }
                    }
                } else if (parts[i].equals("unbreakable=true")) {
                    meta.spigot().setUnbreakable(true);
                }
            }

            itemStack.setItemMeta(meta);
            itemStack.setDurability(data);

            return itemStack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}