package fr.anto42.emma.coreManager.scenarios.scFolder;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.scenarios.ScenarioManager;
import fr.anto42.emma.coreManager.scenarios.ScenarioType;
import fr.anto42.emma.coreManager.scenarios.UHCScenario;
import fr.anto42.emma.utils.players.PlayersUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InventoryRoulette extends UHCScenario {
    private BukkitRunnable task;
    private final int SHUFFLE_INTERVAL = 2400;

    public InventoryRoulette(ScenarioManager scenarioManager) {
        super("InventoryRoulette", new ItemStack(Material.CHEST), scenarioManager);
        setDesc("§8┃ §fLes inventaires sont mélangés aléatoirement toutes les 2 minutes !");
        setScenarioType(ScenarioType.FUN);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        startShuffleTask();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (task != null)
            task.cancel();
    }

    private void startShuffleTask() {
        task = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    shuffleInventory(player.getInventory());
                }
                PlayersUtils.broadcastMessage("§cInventaires mélangés !");
            }
        };
        task.runTaskTimer(UHC.getInstance(), SHUFFLE_INTERVAL, SHUFFLE_INTERVAL);
    }

    private void shuffleInventory(Inventory inv) {
        List<ItemStack> items = new ArrayList<>();

        for (int i = 9; i < 36; i++) {
            items.add(inv.getItem(i));
        }

        Collections.shuffle(items);
        int index = 0;
        for (int i = 9; i < 36; i++) {
            inv.setItem(i, items.get(index));
            index++;
        }
    }
}
