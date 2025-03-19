package fr.anto42.emma.coreManager.commands;

import fr.anto42.emma.coreManager.uis.gameSaves.GameSavedGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsCommand extends Command {
    public StatsCommand() {
        super("stats");
        super.getAliases().add("history");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (!(commandSender instanceof Player))
            return false;
        new GameSavedGUI(((Player) commandSender), false, "all", 0).getkInventory().open(((Player) commandSender).getPlayer());
        return false;
    }
}
