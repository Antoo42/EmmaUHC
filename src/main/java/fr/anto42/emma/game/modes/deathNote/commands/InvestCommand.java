package fr.anto42.emma.game.modes.deathNote.commands;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.game.modes.deathNote.impl.DNRole;
import fr.anto42.emma.game.modes.deathNote.utils.GameUtils;
import fr.anto42.emma.game.modes.deathNote.utils.InvestTask;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InvestCommand extends Command {
    public InvestCommand() {
        super("invest");
        super.getAliases().add("enquete");
        super.getAliases().add("enquête");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        UHCPlayer uhcPlayer = UHC.getUHCPlayer(((Player) commandSender));
        if (uhcPlayer.getRole() == null) {
            uhcPlayer.sendClassicMessage("§cTu ne peux pas faire ça !");
            return false;
        }
        if (!((DNRole) uhcPlayer.getRole()).isCanInvest()) {
            uhcPlayer.sendClassicMessage("§cTu ne peux pas faire ça !");
            return false;
        }
        if (args.length < 1) {
            uhcPlayer.sendClassicMessage("§cErreur: /invest <player>");
            return false;
        }
        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            uhcPlayer.sendClassicMessage("§cJoueur inconnu !");
            return false;
        }
        if(targetPlayer.getName().equals(uhcPlayer.getName())){
            uhcPlayer.sendMessage("§cTu ne peux pas t'enquêter toi même !");
            return true;
        }
        ((DNRole) uhcPlayer.getRole()).setInInvest(true);
        UHCPlayer target = UHC.getUHCPlayer(targetPlayer);
        new InvestTask(uhcPlayer, target, GameUtils.getModule()).runTaskTimer(UHC.getInstance(), 20L, 20L);

        return false;
    }
}
