package fr.anto42.emma.coreManager.commands;

import fr.anto42.emma.UHC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ObservationCommand extends Command {
    public ObservationCommand() {
        super("observation");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        UHC.getInstance().getSpecManager().getFightDetectionSystem().getAutoSpectatorSystem().enableForPlayer(((Player) commandSender));
        return false;
    }
}
