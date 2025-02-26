package fr.anto42.emma.game.modes.deathNote.commands;

import com.google.common.base.Joiner;
import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.game.modes.deathNote.roles.Investigator;
import fr.anto42.emma.game.modes.deathNote.roles.Kira;
import fr.anto42.emma.game.modes.deathNote.roles.Mello;
import fr.anto42.emma.game.modes.deathNote.utils.InvestigatorPowers;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KCommand extends Command {
    public KCommand() {
        super("k");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        UHCPlayer uhcPlayer = UHC.getUHCPlayer(((Player) commandSender));
        if (uhcPlayer.getRole() == null) return false;
        if(!(uhcPlayer.getRole() instanceof Kira)){
            uhcPlayer.sendClassicMessage("§cVous ne pouvez pas faire ça !");
        }
        else {
            if(args.length < 1){
                uhcPlayer.sendClassicMessage("§cL'utilisation correcte de cette commande est: /k <message>");
            }else{
                UHC.getInstance().getUhcGame().getUhcData().getUhcPlayerList().stream().filter(uhcPlayer1 -> uhcPlayer1.getRole() != null && uhcPlayer1.getRole() instanceof Kira).forEach(uhcPlayer1 -> {
                    uhcPlayer1.sendMessage(uhcPlayer.getUhcTeam().getPrefix() + "§cKira §l" + uhcPlayer.getName() + " §8» §6" + Joiner.on(" ").join(args));
                });
                UHC.getInstance().getUhcGame().getUhcData().getUhcPlayerList().stream().filter(uhcPlayer1 -> uhcPlayer1.getRole() != null && uhcPlayer1.getRole() instanceof Investigator && ((Investigator) uhcPlayer1.getRole()).getPower().equals(InvestigatorPowers.KIRA_CHAT)).forEach(uhcPlayer1 -> {
                    uhcPlayer1.sendMessage("§cKira §k???????? §8» §6" + Joiner.on(" ").join(args));
                });
                UHC.getInstance().getUhcGame().getUhcData().getUhcPlayerList().stream().filter(uhcPlayer1 -> uhcPlayer1.getRole() != null && uhcPlayer1.getRole() instanceof Mello && ((Mello) uhcPlayer1.getRole()).isGod()    ).forEach(uhcPlayer1 -> {
                    uhcPlayer1.sendMessage("§cKira §k???????? §8» §6" + Joiner.on(" ").join(args));
                });
            }
        }
        return false;
    }
}
