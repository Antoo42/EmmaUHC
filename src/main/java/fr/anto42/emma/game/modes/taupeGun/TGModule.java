package fr.anto42.emma.game.modes.taupeGun;


import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.Module;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.coreManager.teams.UHCTeam;
import fr.anto42.emma.coreManager.teams.UHCTeamManager;
import fr.anto42.emma.game.GameState;
import fr.anto42.emma.game.impl.UHCData;
import fr.anto42.emma.game.modes.taupeGun.commands.*;
import fr.anto42.emma.game.modes.taupeGun.impl.TGConfig;
import fr.anto42.emma.game.modes.taupeGun.impl.TGData;
import fr.anto42.emma.game.modes.taupeGun.listeners.TGListeners;
import fr.anto42.emma.game.modes.taupeGun.listeners.TryStartListeners;
import fr.anto42.emma.game.modes.taupeGun.roles.SuperTaupe;
import fr.anto42.emma.game.modes.taupeGun.roles.Taupe;
import fr.anto42.emma.game.modes.taupeGun.uis.TGConfigGUI;
import fr.anto42.emma.game.modes.taupeGun.utils.GameUtils;
import fr.anto42.emma.utils.TimeUtils;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.players.CommandUtils;
import fr.anto42.emma.utils.players.PlayersUtils;
import fr.blendman974.kinventory.inventories.KInventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class TGModule extends Module {
    private final TGConfig config;
    private final TGData data;

    public TGModule() {
        super("§c§lTaupe§7-§e§lGun", "Taupe Gun", new ItemCreator(Material.GOLD_SPADE).get());
        GameUtils.setWerewolf(this);
        this.config = new TGConfig();
        this.data = new TGData();
        super.setConfigurable(true);
        super.setkInventory(new TGConfigGUI(this).getkInventory());
        super.setDev("Anto42_");
        super.getDesc().add("§8┃ §fLes joueurs commenceront la partie en §aéquipe§f,");
        super.getDesc().add("§8┃ §cnéanmoins§f, au sein de ces dernières, des §ctraîtres§f devront");
        super.getDesc().add("§8┃ §6gagner §favec leur §6nouvelle équipe§f.");
    }

    public TGConfig getConfig() {
        return config;
    }

    public TGData getData() {
        return data;
    }

    @Override
    public void onStart() {
        UHCTeamManager.getInstance().setActivated(true);
        Bukkit.getPluginManager().registerEvents(new TGListeners(this), UHC.getInstance());
        CommandUtils.registerCommand("uhc-taupegun", new RevealCommand());
        CommandUtils.registerCommand("uhc-taupegun", new TCommand());
        CommandUtils.registerCommand("uhc-taupegun", new ClaimCommand());
        CommandUtils.registerCommand("uhc-taupegun", new RoleCommand());
        CommandUtils.registerCommand("uhc-taupegun", new AdminCommand());
    }

    @Override
    public void onLoad() {
        Bukkit.getPluginManager().registerEvents(new TryStartListeners(), UHC.getInstance());
    }

    @Override
    public void winTester() {

        UHCData gameManager = UHC.getInstance().getUhcGame().getUhcData();
        int kira = 0;
        for (UHCPlayer uhcPlayer : gameManager.getUhcPlayerList()) {
            if (uhcPlayer.getRole() instanceof Taupe)
                kira++;
        }

        int st = 0;
        for (UHCPlayer uhcPlayer : gameManager.getUhcPlayerList()) {
            if (uhcPlayer.getRole() instanceof SuperTaupe)
                st++;
        }
        if (st != 0) {
            if (gameManager.getUhcPlayerList().size() == 1) {
                UHC.getInstance().getUhcGame().setGameState(GameState.FINISH);
                Bukkit.broadcastMessage("§7");
                Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §aFélicitations au joueur " + gameManager.getUhcPlayerList().get(0).getName() + "§a pour sa victoire en " + UHC.getInstance().getUhcManager().getGamemode().getName() + "§a avec §b" + gameManager.getUhcPlayerList().get(0).getKills() + "§a kills !");
                Bukkit.broadcastMessage("§7");
                UHC.getInstance().getDiscordManager().sendWin(gameManager.getUhcPlayerList().get(0));
                announceEnd();
            }
        } else if (UHCTeamManager.getInstance().getUhcTeams().size() == 1 && !(data.getTeamList().contains(UHCTeamManager.getInstance().getUhcTeams().get(0))) && kira == 0 && gameManager.getUhcPlayerList().stream().noneMatch(uhcPlayer -> uhcPlayer.getUhcTeam() == null)) {
            UHC.getInstance().getUhcGame().setGameState(GameState.FINISH);
            
            UHCTeam uhcTeam = UHCTeamManager.getInstance().getUhcTeams().get(0);
            UHC.getInstance().getDiscordManager().sendWin(uhcTeam);
            Bukkit.broadcastMessage("§7");
            Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §aFélicitations à l'équipe " + uhcTeam.getDisplayName() + "§a pour sa victoire en " + UHC.getInstance().getUhcManager().getGamemode().getName() + "§a avec §b" + uhcTeam.getKillsTeam() + "§a kills !");
            Bukkit.broadcastMessage("§7");
            announceEnd();
        } else if (kira != 0 && UHCTeamManager.getInstance().getUhcTeams().size() == 1 && gameManager.getUhcPlayerList().stream().noneMatch(uhcPlayer -> uhcPlayer.getUhcTeam() == null)) {
            UHC.getInstance().getUhcGame().setGameState(GameState.FINISH);
            
            UHCTeam uhcTeam = UHCTeamManager.getInstance().getUhcTeams().get(0);
            UHC.getInstance().getDiscordManager().sendWin(uhcTeam);
            Bukkit.broadcastMessage("§7");
            Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §aFélicitations à l'équipe " + uhcTeam.getDisplayName() + "§a pour sa victoire en " + UHC.getInstance().getUhcManager().getGamemode().getName() + "§a avec §b" + uhcTeam.getKillsTeam() + "§a kills !");
            Bukkit.broadcastMessage("§7");
            announceEnd();
        } else if (kira == UHCTeamManager.getInstance().getUhcTeams().size() && data.getTeamList().size() == 1) {
            UHC.getInstance().getUhcGame().setGameState(GameState.FINISH);
            
            UHCTeam uhcTeam = UHCTeamManager.getInstance().getUhcTeams().get(0);
            UHC.getInstance().getDiscordManager().sendWin(uhcTeam);
            Bukkit.broadcastMessage("§7");
            Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §aFélicitations à l'équipe " + uhcTeam.getDisplayName() + "§a pour sa victoire en " + UHC.getInstance().getUhcManager().getGamemode().getName() + "§a avec §b" + uhcTeam.getKillsTeam() + "§a kills !");
            Bukkit.broadcastMessage("§7");
            announceEnd();
        } else if (UHCTeamManager.getInstance().getUhcTeams().isEmpty() && gameManager.getUhcPlayerList().size() == 1) {
            UHC.getInstance().getUhcGame().setGameState(GameState.FINISH);
            UHC.getInstance().getDiscordManager().sendWin(gameManager.getUhcPlayerList().get(0));
            Bukkit.broadcastMessage("§7");
            Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §aFélicitations au joueur " + gameManager.getUhcPlayerList().get(0).getName() + "§a pour sa victoire en " + UHC.getInstance().getUhcManager().getGamemode().getName() + "§a avec §b" + gameManager.getUhcPlayerList().get(0).getKills() + "§a kills !");
            Bukkit.broadcastMessage("§7");
            announceEnd();
        }
    }

    @Override
    public KInventory getConfigGUI() {
        return null;
    }


    void announceEnd() {
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
        Bukkit.getScheduler().runTaskLater(UHC.getInstance(), () -> {

            Bukkit.shutdown();
        }, TimeUtils.minutes(5));
    }
}
