package fr.anto42.emma.coreManager.commands;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.scenarios.ScenarioManager;
import fr.anto42.emma.game.GameState;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BPCommand extends Command {
    public BPCommand() {
        super("bp");
        getAliases().add("backpack");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] strings) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use backpacks.");
            return true;
        }
        if (UHC.getInstance().getUhcGame().getGameState() != GameState.PLAYING)
            return true;

        Player player = (Player) sender;

        if (!UHC.getInstance().getUhcManager().getScenarioManager().getScenario("Backpack").isActivated()) {
            return true;
        }

        player.openInventory(player.getEnderChest());
        return true;
    }
}
