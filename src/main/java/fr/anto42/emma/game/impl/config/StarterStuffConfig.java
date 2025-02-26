package fr.anto42.emma.game.impl.config;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class StarterStuffConfig {

    private ItemStack head;
    private ItemStack body;
    private ItemStack leggins;
    private ItemStack boots;

    public ItemStack getHead() {
        return head;
    }

    public void setHead(ItemStack head) {
        this.head = head;
    }

    public ItemStack getBody() {
        return body;
    }

    public void setBody(ItemStack body) {
        this.body = body;
    }

    public ItemStack getLeggins() {
        return leggins;
    }

    public void setLeggins(ItemStack leggins) {
        this.leggins = leggins;
    }

    public ItemStack getBoots() {
        return boots;
    }

    public void setBoots(ItemStack boots) {
        this.boots = boots;
    }

    private String[] startInv = new String[0];


    public String[] getStartInv() {
        return startInv;
    }

    public void setStartInv(String[] startInv) {
        this.startInv = startInv;
    }
}
