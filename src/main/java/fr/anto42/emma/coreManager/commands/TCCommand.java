package fr.anto42.emma.coreManager.commands;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.game.GameState;
import fr.anto42.emma.game.UHCGame;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TCCommand extends Command {
    public TCCommand() {
        super("tc");
        super.getAliases().add("teamcoord");
    }

    final UHCGame uhcGame = UHC.getInstance().getUhcGame();
    @Override
    public boolean execute(CommandSender sender, String s, String[] strings) {
        UHCPlayer uhcPlayer = UHC.getUHCPlayer(((Player) sender));
        if(uhcGame.getGameState() != GameState.PLAYING){
            uhcPlayer.sendClassicMessage("§cVous ne pouvez pas faire cela maintenant !");
            return true;
        }
        if(uhcPlayer.getUhcTeam() == null){
            uhcPlayer.sendClassicMessage("§cVous n'êtes dans aucune équipe !");
            return true;

        }else{
            int x = (int) uhcPlayer.getBukkitPlayer().getLocation().getX();
            int y = (int) uhcPlayer.getBukkitPlayer().getLocation().getY();
            int z = (int) uhcPlayer.getBukkitPlayer().getLocation().getZ();
            uhcGame.getUhcData().getUhcPlayerList().stream().filter(uhcPlayer1 -> uhcPlayer1.getUhcTeam() != null && uhcPlayer1.getUhcTeam().getUuid().equals(uhcPlayer.getUhcTeam().getUuid())).forEach(uhcPlayer1 -> {
                uhcPlayer1.sendClassicMessage("§7Coordonnés de §a" + uhcPlayer.getBukkitPlayer().getName() + "§7: §7X: §e" + x + "§7,Y:§e" + y + "§7,Z: §e" + z);
            });
        }
        return false;
    }
}
