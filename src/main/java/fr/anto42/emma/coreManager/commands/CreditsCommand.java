package fr.anto42.emma.coreManager.commands;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreditsCommand extends Command {
    public CreditsCommand() {
        super("credits");
        super.getAliases().add("creds");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        UHCPlayer uhcPlayer = UHC.getUHCPlayer(((Player) commandSender));
        uhcPlayer.sendClassicMessage("§7Crédits:");
        uhcPlayer.sendMessage("");
        uhcPlayer.sendMessage("§8§l» §7Développé par: §bAnto42_");
        uhcPlayer.sendMessage("");
        uhcPlayer.sendMessage("§8§l» §7Contributeur: §bUrToBad");
        uhcPlayer.sendMessage("");
        uhcPlayer.sendMessage("§8§l» §7Librairies open-source utilisés:");
        uhcPlayer.sendMessage("  §8§l» §7KInventory par §b@Blendman974");
        uhcPlayer.sendMessage("  §8§l» §7Cityworld par §b@echurchill");
        uhcPlayer.sendMessage("");
        uhcPlayer.sendMessage("§8§l» §7Optimisation et relecture assistées par intelligence artificielle.");

        return false;
    }
}
