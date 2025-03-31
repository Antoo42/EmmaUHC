package fr.anto42.emma.game.modes.elisa;

import fr.anto42.emma.coreManager.Module;
import fr.blendman974.kinventory.inventories.KInventory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ElisaUHC extends Module {
    public ElisaUHC() {
        super("§b§lElisaUHC", "ElisaUHC", new ItemStack(Material.DIAMOND_BLOCK));
        setDev("Mushorn et Anto42_");
        getDesc().add("§8┃ §fUhc cubique sur la vie de §3Elisa.");

        setConfigurable(true);
        //setkInventory(new ElisaUHC().getkInventory());
        super.setUhcScoreboard(new ElisaScoreboard());
        super.setAvailable(false);
    }

    @Override
    public KInventory getConfigGUI() {
        return null;
    }
}
