package fr.anto42.emma.coreManager.commands;

import fr.anto42.emma.coreManager.achievements.Achievement;
import fr.anto42.emma.coreManager.achievements.AchievementManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManageAchievementsCommand extends Command {
    public ManageAchievementsCommand() {
        super("managesucces");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        Player player = Bukkit.getPlayer(commandSender.getName());
        if (!player.isOp())
            return false;
        if (strings.length == 0 )
            return false;
        if (strings[0].equalsIgnoreCase("remove") && strings.length == 3) {
            AchievementManager.removeAchievementFromPlayer(strings[1], strings[2]);
        }
        if (strings[0].equalsIgnoreCase("add") && strings.length == 4) {
            AchievementManager.getPlayerData(Bukkit.getPlayer(strings[1])).updateProgress(strings[2], Integer.parseInt(strings[3]));
        }
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> completions = new ArrayList<>();
        for (Achievement achievement : AchievementManager.getAllAchievements()) {
            completions.add(achievement.getId());
        }
        Collections.sort(completions);
        return completions;
    }
}
