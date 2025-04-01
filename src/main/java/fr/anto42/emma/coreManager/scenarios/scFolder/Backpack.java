package fr.anto42.emma.coreManager.scenarios.scFolder;

import fr.anto42.emma.coreManager.scenarios.ScenarioManager;
import fr.anto42.emma.coreManager.scenarios.ScenarioType;
import fr.anto42.emma.coreManager.scenarios.UHCScenario;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class Backpack extends UHCScenario {
    public Backpack(ScenarioManager scenarioManager) {
        super("Backpack", new ItemStack(Material.CHEST), scenarioManager);
        setScenarioType(ScenarioType.OTHER);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!isActivated())
            return;
        Player player = event.getEntity();
        Block block = player.getLocation().add(0, -1, 0).getBlock();

        block.setType(Material.CHEST);
        block.getState().update();

        Chest chest = (Chest) block.getState();

        for (ItemStack item : player.getEnderChest().getContents()) {
            if (item == null) {
                continue;
            }

            chest.getInventory().addItem(item);
        }

        player.getEnderChest().clear();
    }

}
