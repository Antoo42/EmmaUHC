package fr.anto42.emma.game.modes.deathNote;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.Module;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.coreManager.teams.UHCTeam;
import fr.anto42.emma.coreManager.teams.UHCTeamManager;
import fr.anto42.emma.game.GameState;
import fr.anto42.emma.game.impl.UHCData;
import fr.anto42.emma.game.modes.deathNote.commands.*;
import fr.anto42.emma.game.modes.deathNote.impl.DeathNoteConfig;
import fr.anto42.emma.game.modes.deathNote.impl.DeathNoteData;
import fr.anto42.emma.game.modes.deathNote.listeners.DeathNoteListeners;
import fr.anto42.emma.game.modes.deathNote.listeners.TryStartDeathNoteListeners;
import fr.anto42.emma.game.modes.deathNote.roles.Kira;
import fr.anto42.emma.game.modes.deathNote.roles.Mello;
import fr.anto42.emma.game.modes.deathNote.roles.Shinigami;
import fr.anto42.emma.game.modes.deathNote.uis.DeathNoteConfigGUI;
import fr.anto42.emma.game.modes.deathNote.utils.GameUtils;
import fr.anto42.emma.game.modes.deathNote.utils.MeloType;
import fr.anto42.emma.game.modes.deathNote.utils.NearHealthView;
import fr.anto42.emma.utils.CommandUtils;
import fr.anto42.emma.utils.TimeUtils;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.players.PlayersUtils;
import fr.blendman974.kinventory.inventories.KInventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class DeathNoteModule extends Module {

    private final DeathNoteConfig deathNoteConfig;
    private final DeathNoteData deathNoteData;


    public DeathNoteModule() {
        super("§3§lDeath§6§lNote §e§lV4", "DeathNote UHC V4", new ItemCreator(Material.BOOK_AND_QUILL).get());
        super.setDev("Anto42_");
        getDesc().add("§8┃ §fBasé sur l'§eunivers §fdu manga au même nom, §3Death§6Note");
        getDesc().add("§8┃ §fvous plongera dans une partie avec §cmoultes rebondissements§f.");
        super.setConfigurable(true);
        GameUtils.init(this);
        setVersion("V4.0");


        deathNoteConfig = new DeathNoteConfig();
        deathNoteData = new DeathNoteData(this);
        setkInventory(new DeathNoteConfigGUI(this).getkInventory());
    }

    @Override
    public void onLoad() {
        super.onLoad();
        Bukkit.getPluginManager().registerEvents(new TryStartDeathNoteListeners(this), UHC.getInstance());
    }


    @Override
    public void onStart() {
        Bukkit.getPluginManager().registerEvents(new DeathNoteListeners(this), UHC.getInstance());
        CommandUtils.registerCommand("uhc", new DNCommand());
        CommandUtils.registerCommand("uhc", new InvestCommand());
        CommandUtils.registerCommand("uhc", new RevealCommand());
        CommandUtils.registerCommand("uhc", new KCommand());
        CommandUtils.registerCommand("uhc", new MelloCommand());
        new NearHealthView().startHealthUpdater();
    }

    public DeathNoteConfig getDeathNoteConfig() {
        return deathNoteConfig;
    }

    public DeathNoteData getDeathNoteData() {
        return deathNoteData;
    }


    @Override
    public void winTester(){
        int kira = 0;
        int shinigami = 0;
        int badMello = 0;

        UHCData gameManager = UHC.getInstance().getUhcGame().getUhcData();

        for(UHCPlayer uhcPlayer : UHC.getInstance().getUhcGame().getUhcData().getUhcPlayerList()){
            if(uhcPlayer.getRole() instanceof Kira)
                kira++;
        }
        for(UHCPlayer uhcPlayer : UHC.getInstance().getUhcGame().getUhcData().getUhcPlayerList()){
            if(uhcPlayer.getRole() instanceof Shinigami){
                shinigami++;
            }
        }
        for(UHCPlayer uhcPlayer : UHC.getInstance().getUhcGame().getUhcData().getUhcPlayerList()){
            if(uhcPlayer.getRole() instanceof Mello && ((Mello) uhcPlayer.getRole()).getMeloType().equals(MeloType.EVIL)){
                badMello++;
            }
        }
        if(UHCTeamManager.getInstance().getUhcTeams().size() == 1 && kira == 0 && badMello == 0 && shinigami == 0){
            UHC.getInstance().getUhcGame().setGameState(GameState.FINISH);

            UHCTeam uhcTeam = UHCTeamManager.getInstance().getUhcTeams().get(0);
            Bukkit.broadcastMessage("§7");
            Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §aFélicitations à l'équipe " + uhcTeam.getDisplayName() + "§a pour sa victoire en " + UHC.getInstance().getUhcManager().getGamemode().getName() + "§a avec §b" + uhcTeam.getKillsTeam() + "§a kills !");
            Bukkit.broadcastMessage("§7");
            announceEnd();
            UHC.getInstance().getDiscordManager().sendWin(uhcTeam);

        }
        if(UHC.getInstance().getUhcGame().getUhcData().getUhcPlayerList().size() == 1 && shinigami == 1 || UHC.getInstance().getUhcGame().getUhcData().getUhcPlayerList().size() == 1 && badMello == 1){
            UHC.getInstance().getUhcGame().setGameState(GameState.FINISH);
            Bukkit.broadcastMessage("§7");
            Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §aFélicitations au joueur " + gameManager.getUhcPlayerList().get(0).getName() + "§a pour sa victoire en " + UHC.getInstance().getUhcManager().getGamemode().getName() + "§a avec §b" + gameManager.getUhcPlayerList().get(0).getKills() + "§a kills !");
            Bukkit.broadcastMessage("§7");
            UHC.getInstance().getDiscordManager().sendWin(gameManager.getUhcPlayerList().get(0));
            announceEnd();
        } else if(UHC.getInstance().getUhcGame().getUhcData().getUhcPlayerList().size() == kira){
            for(UHCPlayer uhcPlayer : UHC.getInstance().getUhcGame().getUhcData().getUhcPlayerList()){
                uhcPlayer.leaveTeam();
            }
            UHC.getInstance().getUhcGame().setGameState(GameState.FINISH);

            Bukkit.broadcastMessage("§7");
            Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §aFélicitations à l'équipe des §c§l Kira§a pour sa victoire en " + UHC.getInstance().getUhcManager().getGamemode().getName() + "§a !");
            Bukkit.broadcastMessage("§7");
            UHC.getInstance().getDiscordManager().sendWin(null);
            announceEnd();
        }
    }

    @Override
    public KInventory getConfigGUI() {
        return null;
    }


    void announceEnd() {
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
