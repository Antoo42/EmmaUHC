package fr.anto42.emma.coreManager.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ColorCommand extends Command {
    public ColorCommand() {
        super("color");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Seul un joueur peut exécuter cette commande.");
            return false;
        }

        Player player = (Player) sender;

        if (args.length != 2) {
            player.sendMessage("Usage: /colorname <joueur> <couleur>");
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);
        String color = args[1];

        if (target == null) {
            player.sendMessage("Le joueur " + args[0] + " n'est pas en ligne.");
            return false;
        }

        // Vérification de la couleur
        ChatColor chatColor;
        try {
            chatColor = ChatColor.valueOf(color.toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Couleur invalide. Utilise une couleur valide.");
            return false;
        }

        // Créer un scoreboard et un team temporaire pour modifier le nametag
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = scoreboard.getTeam(target.getName());

        if (team == null) {
            team = scoreboard.registerNewTeam(target.getName());
        }

        // Changer la couleur du nametag pour ce joueur dans ce team
        team.setPrefix(chatColor.toString()); // Ajouter la couleur avant le nom
        team.addEntry(target.getName()); // Ajouter le joueur au team

        // Maintenant, on change la couleur du nom dans le tab list pour le joueur qui exécute la commande
        player.setPlayerListName(chatColor + target.getName());

        player.sendMessage("Tu vois maintenant " + target.getName() + " avec un nom de couleur " + color);
        return true;
    }
}
