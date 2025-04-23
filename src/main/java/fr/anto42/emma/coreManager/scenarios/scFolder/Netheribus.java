package fr.anto42.emma.coreManager.scenarios.scFolder;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.scenarios.ScenarioManager;
import fr.anto42.emma.coreManager.scenarios.ScenarioType;
import fr.anto42.emma.coreManager.scenarios.UHCScenario;
import fr.anto42.emma.coreManager.scenarios.uis.NetheribusConfigGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Netheribus extends UHCScenario {
    private BukkitRunnable damageTask;
    private int netherDelay = 6000;
    private int damageInterval = 200;
    private double damageAmount = 1.0;

    public Netheribus(ScenarioManager scenarioManager) {
        super("Netheribus", new ItemStack(Material.NETHERRACK), scenarioManager);
        setDesc("§8┃ §fForce les joueurs à aller dans le Nether après délai");
        setScenarioType(ScenarioType.FUN);
        setkInventory(new NetheribusConfigGUI(this).getkInventory());
    }

    @Override
    public void onEnable() {
        super.onEnable();
        startNetherCheck();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (damageTask != null) damageTask.cancel();
    }

    private void startNetherCheck() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.broadcastMessage("§c⛏️ Le Nether est maintenant obligatoire !");
                startDamageTask();
            }
        }.runTaskLater(UHC.getInstance(), netherDelay);
    }

    private void startDamageTask() {
        damageTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getWorld().getEnvironment() != World.Environment.NETHER) {
                        player.damage(damageAmount);
                        player.sendMessage("§c🔥 Retournez dans le Nether !");
                    }
                }
            }
        };
        damageTask.runTaskTimer(UHC.getInstance(), damageInterval, damageInterval);
    }

    public int getNetherDelay() { return netherDelay; }
    public void setNetherDelay(int delay) { this.netherDelay = delay; }

    public int getDamageInterval() { return damageInterval; }
    public void setDamageInterval(int interval) { this.damageInterval = interval; }

    public double getDamageAmount() { return damageAmount; }
    public void setDamageAmount(double amount) { this.damageAmount = amount; }
}
