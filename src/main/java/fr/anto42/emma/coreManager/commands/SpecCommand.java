package fr.anto42.emma.coreManager.commands;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.UHCManager;
import fr.anto42.emma.coreManager.listeners.customListeners.DeathEvent;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.coreManager.teams.UHCTeam;
import fr.anto42.emma.coreManager.teams.UHCTeamManager;
import fr.anto42.emma.coreManager.uis.PlayerInvSeeGUI;
import fr.anto42.emma.coreManager.uis.SpecInfoGUI;
import fr.anto42.emma.game.GameState;
import fr.anto42.emma.game.UHCGame;
import fr.anto42.emma.utils.chat.InteractiveMessage;
import fr.anto42.emma.utils.chat.InteractiveMessageBuilder;
import fr.anto42.emma.utils.players.SoundUtils;
import fr.anto42.emma.utils.players.PlayersUtils;
import fr.anto42.emma.utils.saves.SaveSerializationManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class SpecCommand extends Command {
    public SpecCommand() {
        super("spec");
        super.getAliases().add("mod");
    }


    private final UHCGame uhc = UHC.getInstance().getUhcGame();
    private final UHCManager uhcManager = UHC.getInstance().getUhcManager();

    UHCTeam uhcTeam = null;
    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        UHCPlayer uhcPlayer = UHC.getUHCPlayer(((Player) sender));
        if(!uhc.getUhcData().getSpecList().contains(uhcPlayer) && !uhcPlayer.isUHCOp() && !uhc.getUhcData().getCoHostList().contains(uhcPlayer) && uhc.getUhcData().getHostPlayer() != uhcPlayer){
            uhcPlayer.sendModMessage("§cVous ne pouvez pas faire ça !");
            return true;
        }
        if(args.length == 0 || args[0].equalsIgnoreCase("help")){
            uhcPlayer.sendModMessage("§7Liste des commandes disponibles:");
            uhcPlayer.sendMessage("");
            uhcPlayer.sendMessage("§8§l» §c/spec add/remove/list§7: Ajoutez, retirez ou consultez la liste des spectateurs de la partie.");
            uhcPlayer.sendMessage("");
            uhcPlayer.sendMessage("§8§l» §c/spec config§7: Ouvrez, par le biais d'une commande, le menu de configuration.");
            uhcPlayer.sendMessage("");
            uhcPlayer.sendMessage("§8§l» §c/spec chat§7: Activez ou désactivez le chat.");
            uhcPlayer.sendMessage("");
            uhcPlayer.sendMessage("§8§l» §c/spec inv <Player>§7: Incrustez vous dans l'esprit du joueur cible afin de vous dévoiler tout ses plus profonds secrets.");
            uhcPlayer.sendMessage("");
            uhcPlayer.sendMessage("§8§l» §c/spec role <Player>§7: Prenez connaissance du rôle du joueur ciblé.");
            uhcPlayer.sendMessage("");
            uhcPlayer.sendMessage("§8§l» §c/spec spy§7: Espionnez les messages privés entre les différents joueurs.");
            uhcPlayer.sendMessage("");
            uhcPlayer.sendMessage("§8§l» §c/spec teams§7: Accédez au gestionnaire des équipes de la partie.");
            uhcPlayer.sendMessage("");
            uhcPlayer.sendMessage("§8§l» §c/spec tp <Player>§7: Téléportez-vous au joueur souhaiter.");
            uhcPlayer.sendMessage("");
            uhcPlayer.sendMessage("§8§l» §c/spec kill <Player>§7: Tuez le joueur indiquer.");
            uhcPlayer.sendMessage("");
            uhcPlayer.sendMessage("§8§l» §c/spec info <Player>§7: Ouvrez le menu de modération du joueur.");
            uhcPlayer.sendMessage("");
            uhcPlayer.sendMessage("§8§l» §c/spec viewoffline§7: Regardez les joueurs hors-ligne.");
            uhcPlayer.sendMessage("");
            uhcPlayer.sendMessage("§8§l» §c/spec killoffline§7: Tuez un joueur déconnecté.");
            uhcPlayer.sendMessage("");
            uhcPlayer.sendMessage("§8§l» §c/spec events§7: Obtenez un récap de toute la game.");
            uhcPlayer.sendMessage("");
            uhcPlayer.sendMessage("§8§l» §c/god§7: Devenez opérateur de la partie.");
            uhcPlayer.sendMessage("");
        }else if (args[0].equalsIgnoreCase("add")){
            if (uhcPlayer.getBukkitPlayer().hasPermission("emma.manageSpec") || uhcPlayer.isHost()){
                if (args.length == 1){
                    uhcPlayer.sendModMessage("§cMerci de préciser un joueur.");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null){
                    uhcPlayer.sendModMessage("§cMerci de préciser un joueur connecté.");
                    return true;
                }
                UHCPlayer uhctarget = UHC.getUHCPlayer(target);
                if (uhc.getUhcData().getSpecList().contains(uhctarget))
                    return true;
                uhctarget.getBukkitPlayer().setGameMode(GameMode.SPECTATOR);
                uhc.getUhcData().getSpecList().add(uhctarget);
                uhc.getUhcData().getUhcPlayerList().remove(uhctarget);
                uhcPlayer.sendModMessage("§7Vous avez ajouté §a" + uhctarget.getName() + " §7à la liste des spectateurs.");
                SoundUtils.playSoundToPlayer(uhctarget.getBukkitPlayer(), Sound.ORB_PICKUP);
                uhctarget.sendModMessage("§7Vous avez été ajouté comme §aspectateur §7à la partie. Consultez dès à présent les commandes mises à votre disposition en utilisant le §b/spec help§7.");
            }
        }else if(args[0].equalsIgnoreCase("recap") || args[0].equalsIgnoreCase("events")) {
            uhcPlayer.sendModMessage("Voici un récap de toute la partie:");
            UHC.getInstance().getGameSave().getEvents().forEach(s1 -> {
                uhcPlayer.sendMessage("§7-§e" + SaveSerializationManager.fromEventString(s1).getTimer() + " §8§l» §f" + SaveSerializationManager.fromEventString(s1).getString());
            });
        }


        else if (args[0].equalsIgnoreCase("remove")){
            if (uhcPlayer.getBukkitPlayer().hasPermission("emma.manageSpec") || uhc.getUhcData().getHostPlayer() == uhcPlayer || uhc.getUhcData().getCoHostList().contains(uhcPlayer)){
                if (args.length == 1){
                    uhcPlayer.sendModMessage("§cMerci de préciser un joueur.");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null){
                    uhcPlayer.sendModMessage("§cMerci de préciser un joueur connecté.");
                    return true;
                }
                UHCPlayer uhctarget = UHC.getUHCPlayer(target);
                if (!uhc.getUhcData().getSpecList().contains(uhctarget))
                    return true;
                uhc.getUhcData().getSpecList().remove(uhctarget);
                uhc.getUhcData().getUhcPlayerList().add(uhctarget);
                uhcPlayer.sendModMessage("§7Vous avez retiré §a" + uhctarget.getName() + " §7de la liste des spectateurs.");
                SoundUtils.playSoundToPlayer(uhctarget.getBukkitPlayer(), Sound.ORB_PICKUP);
                uhctarget.sendModMessage("§7Vous avez été retiré des §aspectateurs §7de la partie.");
                if (!uhc.getGameState().equals(GameState.PLAYING)) {
                    uhctarget.getBukkitPlayer().setGameMode(GameMode.SURVIVAL);
                    if (uhc.getGameState() == GameState.WAITING || uhc.getGameState() == GameState.STARTING)
                        PlayersUtils.giveWaitingStuff(uhctarget.getBukkitPlayer());
                }
            }
        }else if (args[0].equalsIgnoreCase("list")){
            if (uhc.getUhcData().getSpecList().isEmpty())
                uhcPlayer.sendModMessage("§cAucun spectateur n'est présent.");
            uhcPlayer.sendModMessage("§7Voici la liste des spectateurs de la partie:");
            uhc.getUhcData().getSpecList().forEach(uhcPlayer1 -> {
                uhcPlayer.sendMessage("§8§l» §e" + uhcPlayer1.getName() + " §3(" + uhcPlayer1.getUuid() + ")" + (Bukkit.getPlayer(uhcPlayer1.getUuid()).isOnline() ? "§aConnecté" : "§cHors-ligne"));
            });
        }
        else if (args[0].equalsIgnoreCase("config")){
            uhcManager.getConfigMainGUI().open(uhcPlayer.getBukkitPlayer());
        }else if (args[0].equalsIgnoreCase("chat")){
            if (!uhc.getUhcData().isChat()){
                uhc.getUhcData().setChat(true);
                Bukkit.broadcastMessage(UHC.getInstance().getConfig().getString("modPrefix").replace("&", "§") + " §7Le chat vient d'être §aactivé§7.");
            }
            else {
                uhc.getUhcData().setChat(false);
                Bukkit.broadcastMessage(UHC.getInstance().getConfig().getString("modPrefix").replace("&", "§") + " §7Le chat vient d'être §cdésactivé§7.");
            }
        }else if (args[0].equalsIgnoreCase("role")){
            if (args.length == 1){
                uhcPlayer.sendModMessage("§cMerci de préciser un joueur. §3(/spec role <Player>)");
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null){
                uhcPlayer.sendModMessage("§cMerci de préciser un joueur connecté.");
                return true;
            }
            UHCPlayer uhctarget = UHC.getUHCPlayer(target);
            if (uhctarget.getRole() == null)
                uhcPlayer.sendModMessage("§7Le joueur §a" + uhctarget.getName() + "§7 ne possède §caucun§7 rôle.");
            else
                uhcPlayer.sendModMessage("§7Le joueur §a" + uhctarget.getName() + "§7 est§c" + uhctarget.getRole().getName() +" §7.");
        }else if (args[0].equalsIgnoreCase("inv") || args[0].equalsIgnoreCase("seeinv") || args[0].equalsIgnoreCase("inventory")){
            if (args.length == 1){
                uhcPlayer.sendModMessage("§cMerci de préciser un joueur. §3(/spec inv <Player>)");
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null){
                uhcPlayer.sendModMessage("§cMerci de préciser un joueur connecté.");
                return true;
            }
            new PlayerInvSeeGUI(target).getkInventory().open(uhcPlayer.getBukkitPlayer());
        } else if (args[0].equalsIgnoreCase("spy")){
            if (!uhc.getUhcData().getSpecList().contains(uhcPlayer)){
                uhc.getUhcData().getSpecList().remove(uhcPlayer);
                uhcPlayer.sendModMessage("§7Vous venez de §cdésactivé §7le mode espion.");
                return true;
            } else{
                uhc.getUhcData().getSpecList().add(uhcPlayer);
                uhcPlayer.sendModMessage("§7Vous venez d'§aactivé §7le mode espion.");
                return true;
            }
        } else if (args[0].equalsIgnoreCase("tp")){
            if (args.length == 1){
                uhcPlayer.sendModMessage("§cMerci de préciser un joueur. §3(/spec tp <Player>)");
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null){
                uhcPlayer.sendModMessage("§cMerci de préciser un joueur connecté.");
                return true;
            }
            uhcPlayer.getBukkitPlayer().teleport(target.getLocation());
            uhcPlayer.sendModMessage("§aVous venez de vous téléporter sur §3" + target.getDisplayName() + "§a.");
        } else if (args[0].equalsIgnoreCase("teams")) {
            if (args.length == 1){
                uhcPlayer.sendModMessage("§7Gestionnaire d'équipes:");
                uhcPlayer.sendMessage("");
                uhcPlayer.sendMessage("§8§l» §c/spec teams list§7: Affiche les équipes en vie.");
                uhcPlayer.sendMessage("§8§l» §c/spec teams move <Player, UUID(équipe)> §7: Changez un joueur d'équipe par le biais de l'UUID de l'équipe cible.");
                uhcPlayer.sendMessage("§8§l» §c/spec teams kill <UUID> §7: Eliminez manuellement une équipe. Cela fera quitter tout les joueurs de cette dernière.");
                uhcPlayer.sendMessage("");
                return true;
            }
            StringBuilder stringBuilder = new StringBuilder();
            if (args[1].equalsIgnoreCase("list")){
                uhcPlayer.sendModMessage("§7Liste des équipes en vie:");
                UHCTeamManager.getInstance().getUhcTeams().forEach(uhcTeam -> {
                    stringBuilder.setLength(0);
                    stringBuilder.append("§8§l» §7Joueurs dans l'équipe:").append("\n\n");
                    uhcTeam.getUhcPlayerList().forEach(uhcPlayer1 -> {
                        stringBuilder.append("§8┃ §f").append(uhcPlayer1.getName());
                    });
                    new InteractiveMessage().add(new InteractiveMessageBuilder("§8§l» §a" + uhcTeam.getDisplayName() + " §8┃ §3" + uhcTeam.getUuid()).setHoverMessage(stringBuilder.toString()).build()).sendMessage(uhcPlayer.getBukkitPlayer());
                    //uhcPlayer.sendMessage("§8§l» §a" + uhcTeam.getDisplayName() + " §8┃ §3" + uhcTeam.getUuid());
                });
            }else if (args[1].equalsIgnoreCase("move") || args[1].equalsIgnoreCase("put")){
                uhcTeam = null;
                if (args.length == 2){
                    uhcPlayer.sendModMessage("§cMerci de préciser un joueur ainsi que l'UUID d'une équipe.");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[2]);
                if (target == null){
                    uhcPlayer.sendModMessage("§cMerci de préciser un joueur connecté.");
                    return true;
                }
                UHCPlayer uhctarget = UHC.getUHCPlayer(target);
                String uuid = args[3];
                UHCTeamManager.getInstance().getUhcTeams().forEach(uhcTeam1 -> {
                    if (uhcTeam1.getUuid().equals(uuid))
                        uhcTeam = uhcTeam1;
                });
                if (uhcTeam == null){
                    uhcPlayer.sendModMessage("§cMerci de préciser un UUID valide. Vous pouvez consulter ces derniers à l'aide de la commande §b/spec teams list§c.");
                    return true;
                }
                uhctarget.joinTeam(uhcTeam);
            } else if (args[1].equalsIgnoreCase("kill")){
                if (args.length == 2){
                    uhcPlayer.sendModMessage("§cMerci de préciser un UUID valide. Vous pouvez consulter ces derniers à l'aide de la commande §b/spec teams list§c.");
                    return true;
                }
                String uuid = args[2];
                UHCTeamManager.getInstance().getUhcTeams().forEach(uhcTeam1 -> {
                    if (uhcTeam1.getUuid().equals(uuid))
                        uhcTeam = uhcTeam1;
                });
                if (uhcTeam == null){
                    uhcPlayer.sendModMessage("§cMerci de préciser un UUID valide. Vous pouvez consulter ces derniers à l'aide de la commande §b/spec teams list§c.");
                    return true;
                }
                uhcTeam.destroy();
            }
        }else if (args[0].equalsIgnoreCase("kill")){
            if (args.length == 1){
                uhcPlayer.sendModMessage("§cMerci de préciser un joueur. §3(/spec kill <Player>)");
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null){
                uhcPlayer.sendModMessage("§cMerci de préciser un joueur connecté.");
                return true;
            }
            target.setHealth(0);
            uhcPlayer.sendModMessage("§aVous venez de tuer §3" + target.getDisplayName() + "§a.");
        }else if (args[0].equalsIgnoreCase("viewoffline") || args[0].equalsIgnoreCase("seeoffline")){
            uhcPlayer.sendModMessage("§7Voici la liste des joueurs déconnectés:");
            uhc.getUhcData().getUhcPlayerList().stream().filter(uhcPlayer1 -> uhcPlayer1.getBukkitPlayer() == null).forEach(uhcPlayer1 -> {
                uhcPlayer.sendMessage("§8§l» §a" + uhcPlayer1.getName() + " §8┃ §3" + uhcPlayer1.getUuid());
            });
        }else if (args[0].equalsIgnoreCase("killoffline")){
            if (args.length == 1){
                uhcPlayer.sendModMessage("§cMerci de préciser un joueur. §3(/spec killoffline <Player>)");
                return true;
            }
            String name = args[1];
            AtomicReference<UHCPlayer> target = null;
            uhc.getUhcData().getUhcPlayerList().stream().filter(uhcPlayer1 -> uhcPlayer1.getBukkitPlayer() == null).forEach(uhcPlayer1 -> {
                if (name.equals(uhcPlayer1.getName())){
                    target.set(uhcPlayer1);
                    target.set(uhcPlayer1);
                }
            });
            if (target.get() == null){
                uhcPlayer.sendModMessage("§cMerci de préciser un joueur déconnecté encore dans la partie. §3(/spec killoffline <Player>)");
                return true;
            }
            Bukkit.getServer().getPluginManager().callEvent(new DeathEvent(target.get(), null));
        } else if (args[0].equalsIgnoreCase("alerts") || args[0].equalsIgnoreCase("alertes")) {
            if (args.length == 1) {
                uhcPlayer.sendModMessage(uhc.getUhcData().getAlerts().contains(uhcPlayer) ? "§7Vous avez actuellement les alertes §aactivés§7. Pour les restreindre, entrez §3/spec alerts off§7." : "§7Vos alertes de modération sont §cdésactivés§7. Pour les activer, entrez §3/spec alerts on§7.");
                return true;
            }
            if (args[1].equalsIgnoreCase("on")) {
                if (uhc.getUhcData().getAlerts().contains(uhcPlayer)) return true;
                uhc.getUhcData().getAlerts().add(uhcPlayer);
                uhcPlayer.sendModMessage("§aVous venez d'activer les alertes de modération !");
            } else if (args[1].equalsIgnoreCase("off")) {
                if (!uhc.getUhcData().getAlerts().contains(uhcPlayer)) return true;
                uhc.getUhcData().getAlerts().remove(uhcPlayer);
                uhcPlayer.sendModMessage("§cVous avez désactiver les alertes de modération !");
            }
        } else if (args[0].equalsIgnoreCase("freeze")) {
            if(args.length == 1) {
                uhcPlayer.sendModMessage("§cVeuillez indiquez un joueur ! §3(/spec freeze <Player>)");
                return true;
            }
            Player player = Bukkit.getPlayer(args[1]);
            if (player == null) {
                uhcPlayer.sendModMessage("§cMerci d'indiquer un joueur connecté !");
                return true;
            }
            UHCPlayer uhcTarget = UHC.getUHCPlayer(player);
            if (uhcTarget.getFreeze()) {
                uhcPlayer.sendModMessage("§cCe joueur est déjà freeze.");
                return true;
            }
            uhcTarget.freeze();
            uhcPlayer.sendModMessage("§cVous avez §b§lfreeze §a" + uhcTarget.getName() + "§c. N'oubliez pas de vous occuper de lui !");
        } else if (args[0].equalsIgnoreCase("unfreeze")){
            if(args.length == 1) {
                uhcPlayer.sendModMessage("§cVeuillez indiquez un joueur ! §3(/spec unfreeze <Player>)");
                return true;
            }
            Player player = Bukkit.getPlayer(args[1]);
            if (player == null) {
                uhcPlayer.sendModMessage("§cMerci d'indiquer un joueur connecté !");
                return true;
            }
            UHCPlayer uhcTarget = UHC.getUHCPlayer(player);
            if (!uhcTarget.getFreeze()) {
                uhcPlayer.sendModMessage("§cCe joueur n'est pas freeze.");
                return true;
            }
            uhcTarget.unFreeze();
            uhcTarget.getBukkitPlayer().setWalkSpeed(0.2F);
            uhcPlayer.sendModMessage("§cVous avez §b§lunfreeze §a" + uhcTarget.getName() + "§c.");
        } else if (args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("profile")) {
            //uhcPlayer.sendModMessage("§cCette foncionnalité est en cours de développement !");
            if(args.length == 1) {
                uhcPlayer.sendModMessage("§cVeuillez indiquez un joueur connecté ! §3(/spec info <Player>)");
                return true;
            }
            Player player = Bukkit.getPlayer(args[1]);
            if (player == null) {
                uhcPlayer.sendModMessage("§cMerci d'indiquer un joueur connecté !");
                return true;
            }
            new SpecInfoGUI(player).getkInventory().open(uhcPlayer.getBukkitPlayer());
        }
        return false;
    }


    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> completions = new ArrayList<>();

        if (!(sender instanceof Player)) {
            return completions;
        }
        if (args.length == 1) {
            List<String> subCommands = Arrays.asList(
                    "add", "remove", "list", "config", "chat", "inv", "role", "spy", "teams",
                    "tp", "kill", "viewoffline", "killoffline", "recap", "events", "info", "profil"
            );
            StringUtil.copyPartialMatches(args[0], subCommands, completions);
        } else if (args.length == 2) {
            List<String> playerCommands = Arrays.asList("inv", "role", "tp", "kill", "killoffline");

            if (playerCommands.contains(args[0].toLowerCase())) {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    completions.add(onlinePlayer.getName());
                }
            }
        }

        Collections.sort(completions);
        return completions;
    }
}