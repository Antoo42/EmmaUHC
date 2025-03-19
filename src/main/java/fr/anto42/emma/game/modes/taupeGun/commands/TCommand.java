package fr.anto42.emma.game.modes.taupeGun.commands;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.game.modes.taupeGun.TGModule;
import fr.anto42.emma.game.modes.taupeGun.impl.TRole;
import fr.anto42.emma.game.modes.taupeGun.roles.SuperTaupe;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TCommand extends Command {
    public TCommand() {
        super("t");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] strings) {
        UHCPlayer uhcPlayer = UHC.getUHCPlayer(((Player) sender));
        if (strings.length == 0) return true;
        if (uhcPlayer.getRole() == null) return true;
        if(!(uhcPlayer.getRole() instanceof TRole))
            return true;
        if (uhcPlayer.getRole() instanceof SuperTaupe && ((SuperTaupe) uhcPlayer.getRole()).isSuperReveal())
            return true;
        StringBuilder stringBuilder = new StringBuilder();
        for(String l : strings){
            stringBuilder.append(l).append(" ");
        }
        UHC.getInstance().getUhcGame().getUhcData().getUhcPlayerList().stream().filter(uhcPlayer1 -> uhcPlayer1.getRole() instanceof TRole && ((TRole) uhcPlayer1.getRole()).getTaupeTeam() == ((TRole) uhcPlayer.getRole()).getTaupeTeam()).forEach(uhcPlayer1 -> {
            if (uhcPlayer1.getRole() instanceof SuperTaupe && ((SuperTaupe) uhcPlayer1.getRole()).isSuperReveal())
                return;
            uhcPlayer1.sendMessage("§c§lTAUPE " + uhcPlayer.getName() + " §8§l» §7" + stringBuilder.toString());
        });
        return false;
    }
}
