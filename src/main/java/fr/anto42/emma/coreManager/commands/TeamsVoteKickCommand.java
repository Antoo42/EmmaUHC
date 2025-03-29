package fr.anto42.emma.coreManager.commands;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.coreManager.uis.teams.KickPlayerGUI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamsVoteKickCommand extends Command {
    public TeamsVoteKickCommand() {
        super("teamsvotekick");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!(sender instanceof Player)) return false;
        UHCPlayer uhcPlayer = UHC.getUHCPlayer(((Player) sender));
        if (args.length != 1) {
            uhcPlayer.sendClassicMessage("§7Usage incorrect.");
            return false;
        }
        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) return false;
        UHCPlayer target = UHC.getUHCPlayer(player);
        if (target == null) {
            uhcPlayer.sendClassicMessage("§7Usage incorrect.");
            return false;
        }

        if (uhcPlayer.getUhcTeam() == null || target.getUhcTeam() == null || uhcPlayer.getUhcTeam().getUuid() != target.getUhcTeam().getUuid())
            return false;
        if (target.getKickPlayer() == null) {
            uhcPlayer.sendClassicMessage("§7Usage incorrect.");
            return false;
        }
        new KickPlayerGUI(target.getKickPlayer(), null).getkInventory().open(uhcPlayer.getBukkitPlayer());
        return false;
    }
}
