package fr.anto42.emma.game.modes.oldDN;


import fr.anto42.emma.coreManager.teams.UHCTeam;
import fr.anto42.emma.coreManager.teams.UHCTeamManager;
import fr.anto42.emma.game.GameState;
import fr.anto42.emma.game.impl.UHCData;
import fr.anto42.emma.game.modes.oldDN.gameManager.commands.*;
import fr.anto42.emma.game.modes.oldDN.impl.DNConfig;
import fr.anto42.emma.game.modes.oldDN.impl.DNData;
import fr.anto42.emma.game.modes.oldDN.roles.Kira;
import fr.anto42.emma.game.modes.oldDN.roles.Mello;
import fr.anto42.emma.game.modes.oldDN.roles.Shinigami;
import fr.anto42.emma.game.modes.oldDN.uis.DNConfigGUI;
import fr.anto42.emma.game.modes.oldDN.uis.MelloGUI;
import fr.anto42.emma.game.modes.oldDN.utils.GameUtils;
import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.Module;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.game.modes.oldDN.gameManager.DeathNoteListener;
import fr.anto42.emma.game.modes.oldDN.gameManager.DNGameListeners;
import fr.anto42.emma.game.modes.oldDN.gameManager.TryStartListener;
import fr.anto42.emma.utils.*;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.players.CommandUtils;
import fr.anto42.emma.utils.players.PlayersUtils;
import fr.anto42.emma.utils.players.SoundUtils;
import fr.blendman974.kinventory.inventories.KInventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class DNModule extends Module {
    //CLASS
    private final DNModule dnModule;
    private final DNData dnData;
    private final DNConfig dnConfig;
    private final DeathNoteListener deathNoteListener = new DeathNoteListener(this);
    private final DNGameListeners gameListeners = new DNGameListeners(this);
    private final TryStartListener tryStartListener = new TryStartListener();
    private final MelloGUI melloGUI = new MelloGUI(this);
    //HASHMAPS
    public Map<UUID, Boolean> isReveal = new HashMap<>();
    public Map<UUID, String> investPower = new HashMap<>();
    public Map<UUID, String> powerDesc = new HashMap<>();
    public Map<UUID, Integer> aura = new HashMap<>();
    public Map<UUID, Boolean> canKira = new HashMap<>();
    public Map<UUID, Integer> kiraNum = new HashMap<>();
    public Map<UUID, String> melloType = new HashMap<>();
    public Map<UUID, Integer> melloInvest = new HashMap<>();
    //LISTS
    public List<UHCPlayer> seeWhenDN = new ArrayList<>();
    public List<UHCPlayer> canSeeKiraChat = new ArrayList<>();
    public List<UHCPlayer> canSeeRealDeath = new ArrayList<>();
    public List<UHCPlayer> canInvest = new ArrayList<>();
    public List<String> melloList = new ArrayList<>();
    //CONFIG
    public DNConfig getDnConfig() {
        return dnConfig;
    }
    public DNData getDnData() {
        return dnData;
    }
    public DNModule getDnModule() {
        return dnModule;
    }
    public DeathNoteListener getDeathNoteListener() {
        return deathNoteListener;
    }
    //GAME
    private int distanceDeathNote = 25;
    private double pointsForEnquete = 3000;
    public int getDistanceDeathNote() {
        return distanceDeathNote;
    }
    public double getPointsForEnquete() {
        return pointsForEnquete;
    }
    public void setDistanceDeathNote(int distanceDeathNote) {
        this.distanceDeathNote = distanceDeathNote;
    }
    public void setPointsForEnquete(int pointsForEnquete) {
        this.pointsForEnquete = pointsForEnquete;
    }
    private List<UHCPlayer> kiraHasEyesList = new ArrayList<>();
    public List<UHCPlayer> getKiraHasEyesList() {
        return kiraHasEyesList;
    }
    private boolean deathNoteCanBeUsed = false;
    public boolean isDeathNoteCanBeUsed() { return deathNoteCanBeUsed; }
    public void setDeathNoteCanBeUsed(boolean deathNoteCanBeUsed) {
        this.deathNoteCanBeUsed = deathNoteCanBeUsed;
    }

    public DNModule(){
        super("§7OLD §3§lDeath§6§lNote §e§lV4", "DeathNote UHC V4", new ItemCreator(Material.BOOK_AND_QUILL).get());
        super.setDev("Anto42_");
        getDesc().add("§8┃ §fBasé sur l'§eunivers §fdu manga au même nom, §3Death§6Note");
        getDesc().add("§8┃ §fvous plongera dans une partie avec §cmoultes rebondissements§f.");
        super.setConfigurable(true);
        super.setkInventory(new DNConfigGUI(this).getkInventory());

        GameUtils.init(this);

        this.dnData = new DNData();
        this.dnConfig = new DNConfig();
        this.dnModule = this;
        super.setAvailable(false);
    }
    @Override
    public void onStart(){
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(gameListeners, UHC.getInstance());
        pm.registerEvents(deathNoteListener, UHC.getInstance());

        CommandUtils.registerCommand("uhc-dn", new RevealCommand(this));
        CommandUtils.registerCommand("uhc-dn", new KCommand(this));
        CommandUtils.registerCommand("uhc-dn", new InvestCommand(this));
        CommandUtils.registerCommand("uhc-dn", new MelloCommand(this, melloGUI));
        CommandUtils.registerCommand("uhc-dn", new DNCommand());
        CommandUtils.registerCommand("uhc-dn", new DevCommand(this));
    }
    @Override
    public void onLoad(){
        Bukkit.getPluginManager().registerEvents(tryStartListener, UHC.getInstance());
    }

    //METHODS
    public void reveal(UHCPlayer uhcPlayer){
        if(isReveal.get(uhcPlayer.getBukkitPlayer().getUniqueId()))
            return;
        isReveal.put(uhcPlayer.getBukkitPlayer().getUniqueId(), true);
        SoundUtils.playSoundToAll(Sound.GHAST_SCREAM);
        PlayersUtils.broadcastMessage("§c" + uhcPlayer.getName() + " §7se révèle être §c" + uhcPlayer.getRole().getName() + "§7 !");
        uhcPlayer.leaveTeam();
        if(uhcPlayer.getRole() instanceof Kira) {
            uhcPlayer.joinTeam(gameListeners.getKiraTeam());
        }
        else if(uhcPlayer.getRole() instanceof Shinigami){
            uhcPlayer.safeGive(new ItemCreator(Material.GOLDEN_APPLE).get());
            uhcPlayer.sendEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
            uhcPlayer.joinTeam(gameListeners.getShini());
        }else if(uhcPlayer.getRole() instanceof Mello){
            uhcPlayer.getBukkitPlayer().setMaxHealth(30);
            uhcPlayer.joinTeam(gameListeners.getBadMello());
        }
        uhcPlayer.safeGive(new ItemCreator(Material.GOLDEN_APPLE).get());
        winTester();
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
            if(uhcPlayer.getRole() instanceof Mello && melloType.get(uhcPlayer.getBukkitPlayer().getUniqueId()).equals("méchant")){
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
        }
        if(UHC.getInstance().getUhcGame().getUhcData().getUhcPlayerList().size() == 1 && shinigami == 1 || UHC.getInstance().getUhcGame().getUhcData().getUhcPlayerList().size() == 1 && badMello == 1){
            if (shinigami == 1){
                UHC.getInstance().getUhcGame().setGameState(GameState.FINISH);
                Bukkit.broadcastMessage("§7");
                Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §aFélicitations au joueur " + gameManager.getUhcPlayerList().get(0).getName() + "§a pour sa victoire en " + UHC.getInstance().getUhcManager().getGamemode().getName() + "§a avec §b" + gameManager.getUhcPlayerList().get(0).getKills() + "§a kills !");
                Bukkit.broadcastMessage("§7");
                announceEnd();
            } else {
                UHC.getInstance().getUhcGame().setGameState(GameState.FINISH);
                Bukkit.broadcastMessage("§7");
                Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §aFélicitations au joueur " + gameManager.getUhcPlayerList().get(0).getName() + "§a pour sa victoire en " + UHC.getInstance().getUhcManager().getGamemode().getName() + "§a avec §b" + gameManager.getUhcPlayerList().get(0).getKills() + "§a kills !");
                Bukkit.broadcastMessage("§7");
                announceEnd();
            }
        } else if(UHC.getInstance().getUhcGame().getUhcData().getUhcPlayerList().size() == kira){
            for(UHCPlayer uhcPlayer : UHC.getInstance().getUhcGame().getUhcData().getUhcPlayerList()){
                uhcPlayer.leaveTeam();
                uhcPlayer.joinTeam(gameListeners.getKiraTeam());
            }
            UHC.getInstance().getUhcGame().setGameState(GameState.FINISH);
            
            UHCTeam uhcTeam = UHCTeamManager.getInstance().getUhcTeams().get(0);
            Bukkit.broadcastMessage("§7");
            Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §aFélicitations à l'équipe " + uhcTeam.getDisplayName() + "§a pour sa victoire en " + UHC.getInstance().getUhcManager().getGamemode().getName() + "§a avec §b" + uhcTeam.getKillsTeam() + "§a kills !");
            Bukkit.broadcastMessage("§7");
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