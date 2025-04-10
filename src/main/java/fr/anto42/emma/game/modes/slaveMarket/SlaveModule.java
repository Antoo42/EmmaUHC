package fr.anto42.emma.game.modes.slaveMarket;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.Module;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.coreManager.teams.UHCTeam;
import fr.anto42.emma.coreManager.teams.UHCTeamManager;
import fr.anto42.emma.game.GameState;
import fr.anto42.emma.game.UHCGame;
import fr.anto42.emma.game.modes.slaveMarket.commands.BidCommand;
import fr.anto42.emma.game.modes.slaveMarket.impl.Auction;
import fr.anto42.emma.game.modes.slaveMarket.impl.AuctionSTATE;
import fr.anto42.emma.game.modes.slaveMarket.impl.SlaveConfig;
import fr.anto42.emma.game.modes.slaveMarket.impl.SlaveData;
import fr.anto42.emma.game.modes.slaveMarket.listeners.SlaveListeners;
import fr.anto42.emma.game.modes.slaveMarket.listeners.TryStartSlaveListener;
import fr.anto42.emma.game.modes.slaveMarket.uis.SlaveConfigGUI;
import fr.anto42.emma.utils.CommandUtils;
import fr.anto42.emma.utils.TimeUtils;
import fr.anto42.emma.utils.gameSaves.EventType;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.players.PlayersUtils;
import fr.blendman974.kinventory.inventories.KInventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SlaveModule extends Module {
    private final SlaveConfig slaveConfig = new SlaveConfig();
    private final SlaveData slaveData = new SlaveData();
    private AuctionSTATE auctionSTATE = AuctionSTATE.WAITING;
    private Auction auction = null;

    public SlaveModule() {
        super("§6§lSlaveMarket", "SlaveMarket", new ItemStack(Material.LEASH));
        setDev("Anto42_");
        getDesc().add("§8┃ §fDes acheteurs enrichissent sur les joueurs");
        getDesc().add("§8┃ §f en échange de diamants");
        setkInventory(new SlaveConfigGUI(this).getkInventory());
        setConfigurable(true);
        CommandUtils.registerCommand("uhc", new BidCommand(this));
        Bukkit.getPluginManager().registerEvents(new TryStartSlaveListener(this), UHC.getInstance());
    }


    public void startAuctions() {
        if (auctionSTATE == AuctionSTATE.FINISHED)
            return;
        setAuctionSTATE(AuctionSTATE.PENDING);
        UHC.getInstance().getUhcGame().getUhcData().getUhcPlayerList().forEach(UHCPlayer::leaveTeam);
        PlayersUtils.broadcastMessage("§aDébut des enchères ! Les capitaines disposent de la commande /bid pour enchérir !");
        getSlaveConfig().getLeadersList().forEach(uhcPlayer -> {
            getSlaveData().getDiamondsLeft().put(uhcPlayer, getSlaveConfig().getStartdiamond());
            UHCTeam team = UHCTeamManager.getInstance().getRandomEmptyTeam();
            uhcPlayer.joinTeam(team);
        });
        newBid();
    }


    List<UHCPlayer> done = new ArrayList<>();
    public void newBid() {
        List<UHCPlayer> toDo = new ArrayList<>();
        Collections.shuffle(toDo);
        UHC.getInstance().getUhcGame().getUhcData().getUhcPlayerList().stream().filter(uhcPlayer -> uhcPlayer.getUhcTeam() == null && !done.contains(uhcPlayer)).forEach(toDo::add);
        if (toDo.isEmpty()) {
            setAuctionSTATE(AuctionSTATE.FINISHED);
            PlayersUtils.broadcastMessage("§aTout les joueurs ont été séléctionnés, la partie peut démarrer !");
            UHC.getInstance().getUhcGame().startGame();
        } else {
            setAuction(new Auction(toDo.get(0), this));
            done.add(toDo.get(0));
            UHC.getInstance().getGameSave().registerEvent(EventType.MODULE, "Début de l'enchère sur : " + toDo.get(0).getName());
            new AuctionsTasks(this).runTaskTimer(UHC.getInstance(), 0L, 20L);
        }
    }

    @Override
    public void onStart() {
        Bukkit.getPluginManager().registerEvents(new SlaveListeners(this), UHC.getInstance());
        getSlaveConfig().getLeadersList().forEach(uhcPlayer -> {
            uhcPlayer.safeGive(new ItemCreator(Material.DIAMOND, getSlaveData().getDiamondsLeft(uhcPlayer)).get());
        });
        PlayersUtils.broadcastMessage("§aLes capitaines viennent de recevoir leurs diamants !");
    }

    @Override
    public void onLoad() {
        UHCTeamManager.getInstance().setActivated(true);
        UHCTeamManager.getInstance().setSlots(500);
        UHCTeamManager.getInstance().setRandomTeam(true);
    }

    @Override
    public void onUnLoad() {
        UHCTeamManager.getInstance().setActivated(false);
        UHCTeamManager.getInstance().setSlots(2);
        UHCTeamManager.getInstance().setRandomTeam(false);
    }

    private final UHCGame uhcGame = UHC.getInstance().getUhcGame();
    @Override
    public void winTester() {
        if (UHCTeamManager.getInstance().getUhcTeams().size() == 1){
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
        } else if (uhcGame.getUhcData().getUhcPlayerList().isEmpty()) {
            uhcGame.setGameState(GameState.FINISH);
            UHC.getInstance().getDiscordManager().sendWin(null);
            Bukkit.broadcastMessage("§7");
            Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §7Oh mince, je n'ai pas regarder la partie... §3Qui a gagner ?");
            Bukkit.broadcastMessage("§7");
            for (Player player : Bukkit.getOnlinePlayers()) {
                UHCPlayer uhcPlayer = UHC.getUHCPlayer(player);
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

    public SlaveConfig getSlaveConfig() {
        return slaveConfig;
    }

    public SlaveData getSlaveData() {
        return slaveData;
    }

    public AuctionSTATE getAuctionSTATE() {
        return auctionSTATE;
    }

    public void setAuctionSTATE(AuctionSTATE auctionSTATE) {
        this.auctionSTATE = auctionSTATE;
    }

    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }
}
