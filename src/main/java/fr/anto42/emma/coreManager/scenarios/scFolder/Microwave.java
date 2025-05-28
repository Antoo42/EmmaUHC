package fr.anto42.emma.coreManager.scenarios.scFolder;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.scenarios.ScenarioManager;
import fr.anto42.emma.coreManager.scenarios.ScenarioType;
import fr.anto42.emma.coreManager.scenarios.UHCScenario;
import fr.anto42.emma.utils.players.PlayersUtils;
import fr.anto42.emma.utils.players.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Microwave extends UHCScenario {
    private BukkitRunnable task;

    public Microwave(ScenarioManager scenarioManager) {
        super("Microwave", new ItemStack(Material.FURNACE), scenarioManager);
        setDesc("§8┃ §fPlus tu es proche du 0,0, plus tu chauffes !");
        setScenarioType(ScenarioType.FUN);
        setConfigurable(false);
    }

    @Override
    public void onStart() {
        super.onEnable();
        startMicrowaveTask();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (task != null)
            task.cancel();
    }

    private void startMicrowaveTask() {
        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!isActivated())
                    return;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!player.isOnline() || player.isDead() || player.getGameMode() != GameMode.SURVIVAL)
                        continue;

                    Location loc = player.getLocation();
                    double dist = loc.distance(new Location(loc.getWorld(), 0, loc.getY(), 0));

                    if (dist <= 50) {
                        player.setFireTicks(40);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 60, 0));
                        Title.sendActionBar(player,ChatColor.RED + "🔥 Vous brûlez au centre !");
                        for (ItemStack item : player.getInventory()) {
                            if (item != null && item.getType() == Material.RAW_BEEF)
                                item.setType(Material.COOKED_BEEF);
                        }
                    } else if (dist <= 150) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 0));
                        Title.sendActionBar(player,ChatColor.GOLD + "🌡️ Il fait chaud ici...");
                    } else {
                        Title.sendActionBar(player,ChatColor.GREEN + "❄️ Zone fraîche");
                    }
                }
            }
        };
        task.runTaskTimer(UHC.getInstance(), 0L, 100L);
    }
}
