package fr.anto42.emma.game.modes.coldWar;

import fr.anto42.emma.coreManager.Module;
import fr.anto42.emma.utils.materials.ItemCreator;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

public class ColdWarUHC extends Module {
    public ColdWarUHC() {
        super("§b§lCold War UHC", "Cold War UHC", new ItemStack(Material.ICE));
        super.setDev("Anto42_ & Mushorn <3");
        super.getDesc().add("§8┃ §fUn mode de jeu de fou malade");
    }
}
