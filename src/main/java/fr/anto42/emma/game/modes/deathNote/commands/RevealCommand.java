package fr.anto42.emma.game.modes.deathNote.commands;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.players.UHCPlayer;

import fr.anto42.emma.coreManager.players.UHCPlayerStates;
import fr.anto42.emma.game.modes.deathNote.impl.DNRole;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RevealCommand extends Command {
    public RevealCommand() {
        super("reveal");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        UHCPlayer uhcPlayer = UHC.getUHCPlayer(((Player) commandSender));
        if (!uhcPlayer.getPlayerState().equals(UHCPlayerStates.ALIVE))
            return false;
        if (uhcPlayer.getRole() == null) {
            uhcPlayer.sendClassicMessage("§cVous n'avez pas de rôle !");
            return false;
        }
        ((DNRole) uhcPlayer.getRole()).reveal();
        return false;
    }
}
