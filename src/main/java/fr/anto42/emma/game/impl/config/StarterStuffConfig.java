package fr.anto42.emma.game.impl.config;

import fr.anto42.emma.utils.saves.ItemStackToString;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class StarterStuffConfig {
    ItemStack barrier = new ItemStack(Material.BARRIER);
    private String head = ItemStackToString.itemStackToString(barrier);
    private String body = ItemStackToString.itemStackToString(barrier);
    private String leggins = ItemStackToString.itemStackToString(barrier);
    private String boots = ItemStackToString.itemStackToString(barrier);

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getLeggins() {
        return leggins;
    }

    public void setLeggins(String leggins) {
        this.leggins = leggins;
    }

    public String getBoots() {
        return boots;
    }

    public void setBoots(String boots) {
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
