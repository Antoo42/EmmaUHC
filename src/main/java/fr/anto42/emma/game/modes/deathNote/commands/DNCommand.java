package fr.anto42.emma.game.modes.deathNote.commands;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DNCommand extends Command {
    public DNCommand() {
        super("dn");
        super.getAliases().add("deathnote");
        super.getAliases().add("role");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        UHCPlayer uhcPlayer = UHC.getUHCPlayer(((Player) commandSender));
        if (uhcPlayer.getRole() == null) {
            uhcPlayer.sendClassicMessage("§cVous n'avez pas de rôle !");
            return false;
        }
        uhcPlayer.getRole().sendDesc();
        return false;
    }
}
