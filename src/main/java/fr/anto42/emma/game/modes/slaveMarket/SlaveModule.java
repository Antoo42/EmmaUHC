package fr.anto42.emma.game.modes.slaveMarket;

import fr.anto42.emma.coreManager.Module;
import fr.anto42.emma.utils.players.PlayersUtils;
import fr.blendman974.kinventory.inventories.KInventory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SlaveModule extends Module {
    public SlaveModule() {
        super("§6§lSlaveMarket", "SlaveMarket", new ItemStack(Material.LEASH));
        setDev("Anto42_");
        getDesc().add("§8┃ §fDes acheteurs enrichissent sur les joueurs");
        getDesc().add("§8┃ §f en échange de diamants");
    }


    @Override
    public void onLoad() {
        PlayersUtils.broadcastMessage("§7Le SlaveMarket étant actif, veuillez faire §a/slavemarket§7 afin de prendre connaissance des commandes.");
    }


    @Override
    public void winTester() {
        
    }

    @Override
    public KInventory getConfigGUI() {
        return null;
    }
}
