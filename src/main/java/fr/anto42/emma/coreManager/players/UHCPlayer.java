package fr.anto42.emma.coreManager.players;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.players.roles.Role;
import fr.anto42.emma.coreManager.teams.KickPlayer;
import fr.anto42.emma.coreManager.teams.UHCTeam;
import fr.anto42.emma.utils.chat.InteractiveMessage;
import fr.anto42.emma.utils.chat.InteractiveMessageBuilder;
import fr.anto42.emma.utils.players.SoundUtils;
import kotlin.reflect.KClassesImplKt;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UHCPlayer {
    private final UUID uuid;
    private final String name;
    private UHCPlayerStates playerState = UHCPlayerStates.NONE;
    private Role role;
    private Player player;
    private Boolean UHCOp = false;
    private Boolean freeze = false;
    private Boolean damageable;
    private UUID offlineZombieUuid;
    private Location quitLoc;
    private List<ItemStack> quitInv = new ArrayList<>();
    private int diamondMined = 0;
    private int ironMined = 0;
    private int goldMined = 0;
    private int kills = 0;
    private int death = 0;
    private boolean editing = false;
    private UHCTeam previousTeam;
    private boolean hasWin = false;
    private double receivedDamages = 0;
    private double makeDamages = 0;

    private final Inventory backupInventory;


    public double getReceivedDamages() {
        return receivedDamages;
    }

    public double getMakeDamages() {
        return makeDamages;
    }

    public void addMakeDamages(Double makeDamages) {
        this.makeDamages = this.makeDamages + makeDamages;
    }

    public void addReceivedDamages(Double receivedDamages) {
        this.receivedDamages = this.receivedDamages + receivedDamages;
    }

    public Inventory getBackupInventory() {
        return backupInventory;
    }

    public UHCPlayer(UUID uuid, String name, Player player) {
        this.uuid = uuid;
        this.name = name;
        this.player = player;
        this.backupInventory = Bukkit.createInventory(null,InventoryType.CHEST,"Inventaire de Backup de " + name);
    }
    private UHCTeam uhcTeam;

    public UHCTeam getUhcTeam() {
        return uhcTeam;
    }

    public void joinTeam(UHCTeam uhcTeam) {
        if (uhcTeam != null)
            leaveTeam();
        assert uhcTeam != null;
        uhcTeam.getUhcPlayerList().forEach(uhcPlayer1 -> {
            uhcPlayer1.sendMessage("§6§lTEAM §8§l» §7Le joueur §a" + this.getName() + "§7 a rejoint l'équipe !");
        });
        this.uhcTeam = uhcTeam;
        uhcTeam.getUhcPlayerList().add(this);
        uhcTeam.getTeam().addEntry(this.getName());
        setKickPlayer(null);
    }

    public void leaveTeam(){
        if (uhcTeam != null){
            uhcTeam.getUhcPlayerList().remove(this);
            uhcTeam.getTeam().removeEntry(this.getName());
            uhcTeam.getUhcPlayerList().forEach(uhcPlayer1 -> {
                uhcPlayer1.sendMessage("§6§lTEAM §8§l» §7Le joueur §c" + this.getName() + "§7 a quitté l'équipe !");
            });
            setPreviousTeam(uhcTeam);
            uhcTeam = null;
        }
        setKickPlayer(null);
    }

    public void sendEffect (PotionEffect potionEffect) {
        if (getBukkitPlayer() == null)
            return;
        getBukkitPlayer().addPotionEffect(potionEffect);
    }

    public boolean isHost () {
        return UHC.getInstance().getUhcGame().getUhcData().getCoHostList().contains(this) || UHC.getInstance().getUhcGame().getUhcData().getHostPlayer().equals(this);
    }

    public boolean isSpec () {
        return UHC.getInstance().getUhcGame().getUhcData().getSpecList().contains(this);
    }

    public Player getBukkitPlayer() {
        return player;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public UHCPlayerStates getPlayerState() {
        return playerState;
    }

    public boolean isAlive() {
        return playerState==UHCPlayerStates.ALIVE;
    }

    public void setPlayerState(UHCPlayerStates playerState) {
        this.playerState = playerState;
    }

    public void sendMessage(String string){
        if (getBukkitPlayer() == null)
            return;
        getBukkitPlayer().sendMessage(string);
    }

    public void sendClassicMessage(String string){
        if (getBukkitPlayer() == null)
            return;
        getBukkitPlayer().sendMessage(UHC.getInstance().getPrefix() + " §7" + string);
    }

    public void sendModMessage(String string) {
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().sendMessage(UHC.getInstance().getConfig().getString("modPrefix").replace("&", "§") + "§7 " + string);
    }


    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
        role.setUhcPlayer(this);
    }

    public Boolean isUHCOp() {
        return UHCOp;
    }

    public void setUHCOp(Boolean UHCOp) {
        this.UHCOp = UHCOp;
    }

    public Boolean isDamageable() {
        return damageable;
    }

    public void setDamageable(Boolean damageable) {
        this.damageable = damageable;
    }

    public void safeGive(ItemStack itemStack){
        if (getBukkitPlayer() == null) {
            return;
        }
        if (getBukkitPlayer().getInventory().firstEmpty() == -1){
            //getBukkitPlayer().getLocation().getWorld().dropItemNaturally(getBukkitPlayer().getLocation(), itemStack);
            //sendMessage(UHC.getInstance().getPrefix() + " §c§nAttention ! §cVotre inventaire est plein ! Par conséquent, les items qui vous ont été donnés ont été posés au sol !");
            //SoundUtils.playSoundToPlayer(getBukkitPlayer(), Sound.VILLAGER_NO);
            sendMessage(UHC.getInstance().getPrefix() + " §c§nAttention ! §cVotre inventaire est plein ! Par conséquent, les items qui vous ont été donnés ont été posés dans l'inventaire de backup (/backup) !");
            this.backupInventory.addItem(itemStack);
        }else{
            getBukkitPlayer().getInventory().addItem(itemStack);
        }

    }
    public void safeGiveOrDrop(ItemStack itemStack){
        if (getBukkitPlayer() == null)
            return;
        if (getBukkitPlayer().getInventory().firstEmpty() == -1) {
            getBukkitPlayer().getWorld().dropItemNaturally(getBukkitPlayer().getLocation(), itemStack);
            sendMessage(UHC.getInstance().getPrefix() + " §c§nAttention ! §cVotre inventaire est plein ! Les items ont été déposés au sol !");
            SoundUtils.playSoundToPlayer(getBukkitPlayer(), Sound.VILLAGER_NO);
        } else {
            getBukkitPlayer().getInventory().addItem(itemStack);
        }
    }

    List<ItemStack> toremove = new ArrayList<>();
    public void safeRemove(ItemStack itemStack) {
        if (getBukkitPlayer() == null) {
            toremove.add(itemStack);
            return;
        }
        toremove.remove(itemStack);
        if(getBukkitPlayer().getInventory().contains(itemStack)){
            getBukkitPlayer().getInventory().remove(itemStack);
        } else if (getBackupInventory().contains(itemStack)) {
            getBackupInventory().remove(itemStack);
        }
    }

    public List<ItemStack> getToremove() {
        return toremove;
    }

    public UUID getOfflineZombieUuid() {
        return offlineZombieUuid;
    }

    public void setOfflineZombieUuid(UUID offlineZombieUuid) {
        this.offlineZombieUuid = offlineZombieUuid;
    }

    public int getDiamondMined() {
        return diamondMined;
    }

    public void setDiamondMined(int diamondMined) {
        this.diamondMined = diamondMined;
    }

    public int getIronMined() {
        return ironMined;
    }

    public void setIronMined(int ironMined) {
        this.ironMined = ironMined;
    }

    public int getGoldMined() {
        return goldMined;
    }

    public void setGoldMined(int goldMined) {
        this.goldMined = goldMined;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeath() {
        return death;
    }

    public void setDeath(int death) {
        this.death = death;
    }

    public boolean isEditing() {
        return editing;
    }

    public void setEditing(boolean editing) {
        this.editing = editing;
    }

    public boolean isInTeamWith(UHCPlayer uhcPlayer){
        return this.getUhcTeam() != null && uhcPlayer.getUhcTeam() != null && this.getUhcTeam().getUhcPlayerList().contains(uhcPlayer);
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void kickPlayer(String reason){
        player.kickPlayer(UHC.getInstance().getPrefix() + " " + reason);
    }

    public Location getQuitLoc() {
        return quitLoc;
    }

    public void setQuitLoc(Location quitLoc) {
        this.quitLoc = quitLoc;
    }

    public List<ItemStack> getQuitInv() {
        return quitInv;
    }

    public void setQuitInv(List<ItemStack> quitInv) {
        this.quitInv = quitInv;
    }

    public void freeze() {
        freeze = true;
        getBukkitPlayer().setWalkSpeed(0F);
        sendModMessage("§cUn modérateur vous a §b§lfreeze§c. Vous ne pouvez par conséquent plus vous déplacer.");
    }

    public void unFreeze() {
        freeze = false;
        sendModMessage("§cVous n'êtes plus §b§lfreeze§c. Bon retour au jeu !");
    }

    public Boolean getFreeze() {
        return freeze;
    }

    public UHCTeam getPreviousTeam() {
        return previousTeam;
    }

    public void setPreviousTeam(UHCTeam previousTeam) {
        this.previousTeam = previousTeam;
    }

    public boolean isHasWin() {
        return hasWin;
    }

    public void setHasWin(boolean hasWin) {
        this.hasWin = hasWin;
    }

    private List<PotionEffect> potionEffects = new ArrayList<>();
    public void safePotionEffect(PotionEffect potionEffect) {
        if (getBukkitPlayer() == null) {
            potionEffects.add(potionEffect);
            return;
        }
        getBukkitPlayer().addPotionEffect(potionEffect);
    }

    public List<PotionEffect> getPotionEffects() {
        return potionEffects;
    }


    private List<PotionEffect> toRemovePotionEffects = new ArrayList<>();

    public void safeRemovePotionEffect (PotionEffect potionEffect) {
        if (getBukkitPlayer() == null){
            toRemovePotionEffects.add(potionEffect);
            return;
        }
        getBukkitPlayer().removePotionEffect(potionEffect.getType());
    }

    public List<PotionEffect> getToRemovePotionEffects() {
        return toRemovePotionEffects;
    }

    public Location getLocation() {
        return (getBukkitPlayer() == null ? getQuitLoc() : getBukkitPlayer().getLocation());
    }
    public void safeSetLevel(int level){
        if(getBukkitPlayer() == null){
            return;
        }
        getBukkitPlayer().setLevel(level);
    }

    private KickPlayer kickPlayer;

    public KickPlayer getKickPlayer() {
        return kickPlayer;
    }

    public void setKickPlayer(KickPlayer kickPlayer) {
        this.kickPlayer = kickPlayer;
    }


    public void startKickPlayer(UHCPlayer starter) {
        if (uhcTeam == null)
            return;
        uhcTeam.getUhcPlayerList().forEach(uhcPlayer -> {
            new InteractiveMessage().add(new InteractiveMessageBuilder("§cUn vote d'exclusion sur §e" + getName() + " §cvient d'être lancé ! Cliquez §eici§c pour voter.").setClickAction(ClickEvent.Action.RUN_COMMAND, "/teamsvotekick " + getName()).build()).sendMessage(uhcPlayer.getBukkitPlayer());
        });
        setKickPlayer(new KickPlayer(this));
        getKickPlayer().addYesVote(starter);
    }
}
