package fr.anto42.emma.utils.lang;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum Langages {
    FRENCH("Français", "ouais tkt", new ItemStack(Material.DIAMOND)),
    ENGLISH("English", "...", new ItemStack(Material.GOLD_INGOT));


    private final String name;
    private final String completed;
    private final ItemStack itemStack;

    Langages(String name, String completed, ItemStack itemStack) {
        this.name = name;
        this.completed = completed;
        this.itemStack = itemStack;
    }

    public String getName() {
        return name;
    }

    public String getCompleted() {
        return completed;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}
