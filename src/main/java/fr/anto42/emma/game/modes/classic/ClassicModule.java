package fr.anto42.emma.game.modes.classic;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.Module;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.coreManager.teams.UHCTeam;
import fr.anto42.emma.coreManager.teams.UHCTeamManager;
import fr.anto42.emma.game.GameState;
import fr.anto42.emma.game.UHCGame;
import fr.anto42.emma.utils.TimeUtils;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.players.PlayersUtils;
import fr.blendman974.kinventory.inventories.KInventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ClassicModule extends Module {
    public ClassicModule() {
        super("§6§lClassique", "Classique", new ItemCreator(Material.GOLDEN_APPLE).get());
        super.setConfigurable(false);
        super.setDev("Anto42_");
        super.getDesc().add("§8┃ §fLe mode de jeu par défaut mais qui n'est pas moins extravagant !");
    }

    @Override
    public void onStart() {
        Bukkit.getPluginManager().registerEvents(new ClassicListeners(this), UHC.getInstance());
    }

    private final UHCGame uhcGame = UHC.getInstance().getUhcGame();
    public void winTester(){
        if (!UHCTeamManager.getInstance().isActivated()){
            if (uhcGame.getUhcData().getUhcPlayerList().size() <= 1){
                uhcGame.setGameState(GameState.FINISH);
            if (uhcGame.getUhcData().getUhcPlayerList().size() == 1){
                    Bukkit.broadcastMessage("§7");
                    Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §aFélicitations au joueur " + uhcGame.getUhcData().getUhcPlayerList().get(0).getName() + "§a pour sa victoire en " + UHC.getInstance().getUhcManager().getGamemode().getName() + "§a avec §b" + uhcGame.getUhcData().getUhcPlayerList().get(0).getKills() + "§a kills !");
                    Bukkit.broadcastMessage("§7");
                    UHC.getInstance().getDiscordManager().sendWin(uhcGame.getUhcData().getUhcPlayerList().get(0));
            }else {
                Bukkit.broadcastMessage("§7");
                Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §cOups... Je crois que je n'ai pas suivit la partie... Qui a gagné ? :p");
                Bukkit.broadcastMessage("§7");
                UHC.getInstance().getDiscordManager().sendWin(null);
                }

                for(Player player : Bukkit.getOnlinePlayers()){
                    UHCPlayer uhcPlayer  = UHC.getUHCPlayer(player);
                    player.sendMessage("§8┃ §fRécapitulatif de votre partie:");
                    player.sendMessage("§7");
                    player.sendMessage("§8§l» §3Kills: §e" + uhcPlayer.getKills());
                    player.sendMessage("§8§l» §3Morts: §e" + uhcPlayer.getDeath());
                    if (uhcPlayer.getRole() != null)
                        player.sendMessage("§8§l» §3Rôle: §e" + uhcPlayer.getRole().getName());
                }

                Bukkit.broadcastMessage("§7");
                Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §cArrêt automatique du serveur dans 5 minutes !");
                Bukkit.broadcastMessage("§7");
                PlayersUtils.finishToSpawn();
                Bukkit.getScheduler().runTaskLater(UHC.getInstance(), Bukkit::shutdown, TimeUtils.minutes(5));            }
        }else if (UHCTeamManager.getInstance().getUhcTeams().size() == 1){
            uhcGame.setGameState(GameState.FINISH);

            UHCTeam uhcTeam = UHCTeamManager.getInstance().getUhcTeams().get(0);
            UHC.getInstance().getDiscordManager().sendWin(uhcTeam);

            Bukkit.broadcastMessage("§7");
            Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §aFélicitations à l'équipe " + uhcTeam.getDisplayName() + "§a pour sa victoire en " + UHC.getInstance().getUhcManager().getGamemode().getName() + "§a avec §b" + uhcTeam.getKillsTeam() + "§a kills !");
            Bukkit.broadcastMessage("§7");
            for(Player player : Bukkit.getOnlinePlayers()){
                UHCPlayer uhcPlayer  = UHC.getUHCPlayer(player);
                player.sendMessage("§8┃ §fRécapitulatif de votre partie:");
                player.sendMessage("§7");
                player.sendMessage("§8§l» §3Kills: §e" + uhcPlayer.getKills());
                player.sendMessage("§8§l» §3Morts: §e" + uhcPlayer.getDeath());
                if (uhcPlayer.getRole() != null)
                    player.sendMessage("§8§l» §3Rôle: §e" + uhcPlayer.getRole().getName());
            }

            Bukkit.broadcastMessage("§7");
            Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §cArrêt automatique du serveur dans 5 minutes !");
            Bukkit.broadcastMessage("§7");
            PlayersUtils.finishToSpawn();
            Bukkit.getScheduler().runTaskLater(UHC.getInstance(), Bukkit::shutdown, TimeUtils.minutes(5));
        } else if (uhcGame.getUhcData().getUhcPlayerList().isEmpty()){
            uhcGame.setGameState(GameState.FINISH);
            UHC.getInstance().getDiscordManager().sendWin(null);
            Bukkit.broadcastMessage("§7");
            Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §7Oh mince, je n'ai pas regarder la partie... §3Qui a gagner ?");
            Bukkit.broadcastMessage("§7");
            for(Player player : Bukkit.getOnlinePlayers()){
                UHCPlayer uhcPlayer  = UHC.getUHCPlayer(player);
                player.sendMessage("§8┃ §fRécapitulatif de votre partie:");
                player.sendMessage("§7");
                player.sendMessage("§8§l» §3Kills: §e" + uhcPlayer.getKills());
                player.sendMessage("§8§l» §3Morts: §e" + uhcPlayer.getDeath());
                if (uhcPlayer.getRole() != null)
                    player.sendMessage("§8§l» §3Rôle: §e" + uhcPlayer.getRole().getName());
            }

            Bukkit.broadcastMessage("§7");
            Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §cArrêt automatique du serveur dans 5 minutes !");
            Bukkit.broadcastMessage("§7");
            PlayersUtils.finishToSpawn();
            Bukkit.getScheduler().runTaskLater(UHC.getInstance(), Bukkit::shutdown, TimeUtils.minutes(5));
        }
    }

    @Override
    public KInventory getConfigGUI() {
        return null;
    }
}
