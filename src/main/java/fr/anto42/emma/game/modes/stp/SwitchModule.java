package fr.anto42.emma.game.modes.stp;

import fr.anto42.emma.game.modes.stp.listeners.PreStartListener;
import fr.anto42.emma.game.modes.stp.listeners.SwitchListeners;
import fr.anto42.emma.game.modes.stp.uis.SwitchConfigGUI;
import fr.anto42.emma.game.modes.stp.uis.SwitchGUI;
import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.Module;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.coreManager.teams.UHCTeam;
import fr.anto42.emma.coreManager.teams.UHCTeamManager;
import fr.anto42.emma.game.GameState;
import fr.anto42.emma.game.UHCGame;
import fr.anto42.emma.utils.gameSaves.EventType;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.players.PlayersUtils;
import fr.anto42.emma.utils.TimeUtils;
import fr.anto42.emma.utils.skulls.SkullList;
import fr.blendman974.kinventory.inventories.KInventory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class SwitchModule extends Module {
    public SwitchModule() {
        super("§e§lSWITCH", "SwitchThePatrick", SkullList.TIMER.getItemStack());
        super.setDev("Anto42_");
        super.setConfigurable(true);
        super.getDesc().add("§8┃ §fLes équipes seront §arenouvelées§f dans ce mode de jeu");
        super.getDesc().add("§8┃ §foù toutes les §cx minutes§f, certains équipiers");
        super.getDesc().add("§8┃ §féchangeront d'équipes avec d'autres.");
        super.setkInventory(new SwitchGUI(this).getkInventory());
        setConfigGUI(new SwitchConfigGUI(this).getkInventory());
        super.setUhcScoreboard(new SwitchScoreboard(this));

    }

    private int timer = 20*60;

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public void addTimer(){
        if (timer == 40*60)
            return;
        timer = timer + 60;
    }


    public void removeTimer(){
        if (timer == 60)
            return;
        timer = timer - 60;
    }

    @Override
    public void onLoad() {
        Bukkit.getServer().getPluginManager().registerEvents(new PreStartListener(), UHC.getInstance());
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
        return new SwitchConfigGUI(this).getkInventory();
    }

    private int l = 0;
    @Override
    public void onStart() {
        Bukkit.getPluginManager().registerEvents(new SwitchListeners(this), UHC.getInstance());
        (new BukkitRunnable() {
            @Override
            public void run() {
                if (l == timer && uhcGame.getGameState() == GameState.PLAYING){
                    l = 0;
                    switchTP();
                }
                l++;
            }
        }).runTaskTimer(UHC.getInstance(), 0L, 20L);
    }

    public int getL() {
        return l;
    }

    public void switchTP() {
        List<UHCTeam> uTeams_toswitch = new ArrayList<>();
        uTeams_toswitch.addAll(UHCTeamManager.getInstance().getUhcTeams());
        Collections.shuffle(uTeams_toswitch);

        for (UHCTeam uTeam : new ArrayList<>(uTeams_toswitch)) {
            if (uTeams_toswitch.size() <= 1)
                break;

            if (!uTeams_toswitch.contains(uTeam))
                continue;


            uTeams_toswitch.remove(uTeam);
            UHCTeam uTeam2 = uTeams_toswitch.get(getRandomIntInRange(uTeams_toswitch.size() - 1, 0));
            uTeams_toswitch.remove(uTeam2);

            Random random = new Random();

            Player player1 = Bukkit.getPlayer(uTeam.getAliveUhcPlayers().get(random.nextInt(uTeam.getAliveUhcPlayers().size())).getName());
            Player player2 = Bukkit.getPlayer(uTeam2.getAliveUhcPlayers().get(random.nextInt(uTeam2.getAliveUhcPlayers().size())).getName());

            Location loc1 = player1.getLocation().clone();
            Location loc2 = player2.getLocation().clone();

            if (player1.isOnline())
                player1.teleport(loc2);
            if (player2.getPlayer().isOnline())
                player2.getPlayer().teleport(loc1);

            UHCPlayer uhcPlayer = UHC.getUHCPlayer(player1);
            UHCPlayer uhcPlayer1 = UHC.getUHCPlayer(player2);

            uhcPlayer.joinTeam(uTeam2);
            uhcPlayer1.joinTeam(uTeam);

            uhcPlayer.sendClassicMessage("§7Vous avez été échangé(e) avec §e" + uhcPlayer1.getName() + " §7(" + uhcPlayer.getUhcTeam().getDisplayName() + ")");
            uhcPlayer1.sendClassicMessage("§7Vous avez été échangé(e) avec §e" + uhcPlayer.getName() + " §7(" + uhcPlayer1.getUhcTeam().getDisplayName() + ")");
            UHC.getInstance().getGameSave().registerEvent(EventType.MODULE, "switch de " + uhcPlayer.getName() + " et " + uhcPlayer1.getName());

        }
    }

    public static int getRandomIntInRange(int max, int min) {
        int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
        return randomNum;
    }
}
