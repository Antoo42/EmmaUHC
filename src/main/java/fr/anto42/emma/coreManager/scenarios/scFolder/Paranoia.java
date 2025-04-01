package fr.anto42.emma.coreManager.scenarios.scFolder;

import fr.anto42.emma.coreManager.scenarios.ScenarioManager;
import fr.anto42.emma.coreManager.scenarios.ScenarioType;
import fr.anto42.emma.coreManager.scenarios.UHCScenario;
import fr.anto42.emma.utils.players.PlayersUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class Paranoia extends UHCScenario {
    public Paranoia(ScenarioManager scenarioManager) {
        super("Paranoia", new ItemStack(Material.NETHERRACK), scenarioManager);
        setScenarioType(ScenarioType.PVP);
        setDesc("§8┃ §fLes coordonnées d'un joueur sont divulgués quand il: minent (diamant/or) ou craft un GAPPLE");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!isActivated())
            return;
        Player player = event.getPlayer();
        Location loc = player.getLocation();
        Block block = event.getBlock();

        if (block.getType() == Material.DIAMOND_ORE) {
            PlayersUtils.broadcastMessage("§c" + player.getName() + "§7 a miné un §bdiamond ore §7en " + location(loc));
        }

        if (block.getType() == Material.GOLD_ORE) {
            PlayersUtils.broadcastMessage("§c" + player.getName() + "§7 a miné un §6gold ore §7en " + location(loc));
        }
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (!isActivated())
            return;
        Player player = event.getPlayer();
        Location loc = player.getLocation();

        if (event.getItem().getType() == Material.GOLDEN_APPLE) {
            PlayersUtils.broadcastMessage("§c" + player.getName() + "§7 a mangé une §eGolden Apple §7en " + location(loc));
        }
    }

    @EventHandler
    public void onPrepareItemCraftEvent(CraftItemEvent event) {
        if (!isActivated())
            return;
        HumanEntity player = event.getWhoClicked();
        Location loc = player.getLocation();

        if (event.getRecipe().getResult().getType() == Material.GOLDEN_APPLE) {
            PlayersUtils.broadcastMessage("§c" + player.getName() + "§7 a craft une §eGolden Apple §7en " + location(loc));
        }

        if (event.getRecipe().getResult().getType() == Material.ANVIL) {
            PlayersUtils.broadcastMessage("§c" + player.getName() + "§7 a carft une §denclume §7en " + location(loc));
        }

        if (event.getRecipe().getResult().getType() == Material.ENCHANTMENT_TABLE) {
            PlayersUtils.broadcastMessage("§c" + player.getName() + "§7 a craft une §5table d'enchantement §7en " + location(loc));
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!isActivated())
            return;
        Player player = event.getEntity();
        Location loc = player.getLocation();

        PlayersUtils.broadcastMessage("§c" + player.getName() + "§7 est mort en " + location(loc));
    }

    /**
     * Get the given location in string form.
     *
     * @param loc the given location.
     * @return Location in String form.
     */
    private String location(Location loc) {
        return "§7x:§e" + loc.getBlockX() + " §7y:§e" + loc.getBlockY() + " §7z:§e" + loc.getBlockZ();
    }
}