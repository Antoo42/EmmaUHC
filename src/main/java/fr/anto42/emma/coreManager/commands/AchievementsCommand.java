package fr.anto42.emma.coreManager.commands;

import fr.anto42.emma.coreManager.uis.achievements.AchievementsGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AchievementsCommand extends Command {
    public AchievementsCommand() {
        super("achivements");
        super.getAliases().add("succes");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        new AchievementsGUI(((Player) commandSender), 0).getkInventory().open(((Player) commandSender));
        return false;
    }
}
