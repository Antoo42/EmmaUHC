package fr.anto42.emma.coreManager.scenarios.scFolder;

import fr.anto42.emma.coreManager.scenarios.ScenarioManager;
import fr.anto42.emma.coreManager.scenarios.ScenarioType;
import fr.anto42.emma.coreManager.scenarios.UHCScenario;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MidasTouch extends UHCScenario {
    private static final Set<Material> BLACKLIST = new HashSet<>(Arrays.asList(
            Material.AIR,
            Material.WATER,
            Material.LAVA,
            Material.BEDROCK,
            Material.BARRIER
    ));

    public MidasTouch(ScenarioManager scenarioManager) {
        super("MidasTouch", new ItemStack(Material.GOLD_BLOCK), scenarioManager);
        setDesc("§8┃ §fChaque bloc sur lequel vous marchez se transforme en or !");
        setScenarioType(ScenarioType.FUN);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!isActivated())
            return;
        Player player = event.getPlayer();
        if (event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockY() == event.getTo().getBlockY()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ())
            return;

        Material under = player.getLocation().subtract(0, 1, 0).getBlock().getType();
        if (!BLACKLIST.contains(under)) {
            player.getLocation().subtract(0, 1, 0).getBlock().setType(Material.GOLD_BLOCK);
        }
    }
}
