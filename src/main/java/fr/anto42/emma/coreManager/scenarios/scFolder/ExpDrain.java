package fr.anto42.emma.coreManager.scenarios.scFolder;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.scenarios.ScenarioManager;
import fr.anto42.emma.coreManager.scenarios.ScenarioType;
import fr.anto42.emma.coreManager.scenarios.UHCScenario;
import fr.anto42.emma.coreManager.scenarios.uis.ExpDrainConfigGUI;
import fr.anto42.emma.utils.players.PlayersUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class ExpDrain extends UHCScenario {
    private BukkitRunnable task;

    public ExpDrain(ScenarioManager scenarioManager) {
        super("ExpDrain", new ItemStack(Material.EXP_BOTTLE), scenarioManager);
        setDesc("§8┃ §fToutes les x minutes, perte d'1 niveau d’XP ou 1/2 coeur !");
        setScenarioType(ScenarioType.FUN);
        setConfigurable(true);
        setkInventory(new ExpDrainConfigGUI(this).getkInventory());
    }

    public BukkitRunnable getTask() {
        return task;
    }

    public void setTask(BukkitRunnable task) {
        this.task = task;
    }

    private int drainInterval = 6000;
    private double damage = 1.0;

    public int getDrainInterval() { return drainInterval; }
    public void setDrainInterval(int interval) { this.drainInterval = interval; }

    public double getDamage() { return damage; }
    public void setDamage(double damage) { this.damage = damage; }


    @Override
    public void onEnable() {
        super.onEnable();
        startDrainTask();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (task != null)
            task.cancel();
    }

    private void startDrainTask() {
        task = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getLevel() > 0) {
                        player.setLevel(player.getLevel() - 1);
                    } else {
                        player.damage(1.0);
                    }
                }
                PlayersUtils.broadcastMessage("§c⚡ Drain d’XP effectué !");
            }
        };
        task.runTaskTimer(UHC.getInstance(), drainInterval, drainInterval);
    }
}
