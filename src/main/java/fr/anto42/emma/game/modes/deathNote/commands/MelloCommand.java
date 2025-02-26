package fr.anto42.emma.game.modes.deathNote.commands;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.game.modes.deathNote.roles.Mello;
import fr.anto42.emma.game.modes.deathNote.uis.MelloGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MelloCommand extends Command {
    public MelloCommand() {
        super("mello");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        UHCPlayer uhcPlayer = UHC.getUHCPlayer(((Player) commandSender));
        if (uhcPlayer.getRole() == null || !(uhcPlayer.getRole() instanceof Mello)) return false;
        if (((Mello) uhcPlayer.getRole()).getMeloType() != null) return false;
        new MelloGUI().open(uhcPlayer.getBukkitPlayer());
        return false;
    }
}
