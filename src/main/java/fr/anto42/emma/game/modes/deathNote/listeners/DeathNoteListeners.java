package fr.anto42.emma.game.modes.deathNote.listeners;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.Module;
import fr.anto42.emma.coreManager.listeners.customListeners.DeathEvent;
import fr.anto42.emma.coreManager.listeners.customListeners.RolesEvent;
import fr.anto42.emma.coreManager.listeners.customListeners.StartEvent;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.coreManager.players.roles.Role;
import fr.anto42.emma.coreManager.teams.UHCTeam;
import fr.anto42.emma.coreManager.teams.UHCTeamManager;
import fr.anto42.emma.coreManager.worldManager.WorldManager;
import fr.anto42.emma.game.UHCGame;
import fr.anto42.emma.game.impl.UHCData;
import fr.anto42.emma.game.modes.deathNote.DeathNoteModule;
import fr.anto42.emma.game.modes.deathNote.impl.DNRole;
import fr.anto42.emma.game.modes.deathNote.roles.*;
import fr.anto42.emma.game.modes.deathNote.uis.KiraMainGUI;
import fr.anto42.emma.game.modes.deathNote.utils.InvestigatorPowers;
import fr.anto42.emma.utils.SoundUtils;
import fr.anto42.emma.utils.TimeUtils;
import fr.anto42.emma.utils.players.PlayersUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static fr.anto42.emma.coreManager.players.UHCPlayerStates.DEAD;

public class DeathNoteListeners implements Listener {

    private final DeathNoteModule module;

    public DeathNoteListeners(DeathNoteModule module) {
        this.module = module;
    }


    @EventHandler
    public void onStart (StartEvent event) {
        for(UHCTeam uhcTeam : UHCTeamManager.getInstance().getUhcTeams()){
            if(uhcTeam.getUhcPlayerList().isEmpty()){
                uhcTeam.destroy();
            }
        }
    }

    @EventHandler
    public void onRoles(RolesEvent event) {
        UHC uhc = UHC.getInstance();
        UHCGame uhcGame = uhc.getUhcGame();
        UHCData uhcData = uhcGame.getUhcData();

        uhcData.setChat(false);

        UHCTeamManager.getInstance().getUhcTeams().forEach(uhcTeam -> {
            if (uhcTeam == null) {
                System.out.println("Erreur : équipe invalide.");
                return;
            }

            List<UHCPlayer> uhcPlayerList = new ArrayList<>(uhcTeam.getAliveUhcPlayers());
            int playerCount = uhcPlayerList.size();

            if (playerCount == 0) {
                System.out.println("Aucun joueur vivant dans l'équipe.");
                return;
            }

            System.out.println("Assignation des rôles pour une équipe de " + playerCount + " joueurs.");

            List<Class<? extends Role>> roles = new ArrayList<>();
            roles.add(Kira.class);
            roles.add(Mello.class);
            roles.add(Shinigami.class);
            roles.add(Near.class);
            for (int i = 4; i < playerCount; i++) {
                roles.add(Investigator.class);
            }

            Collections.shuffle(roles);
            Collections.shuffle(uhcPlayerList);

            System.out.println("Joueurs mélangés : " + uhcPlayerList);
            System.out.println("Rôles mélangés : " + roles);

            for (int i = 0; i < playerCount; i++) {
                try {
                    UHCPlayer player = uhcPlayerList.get(i);
                    Class<? extends Role> roleClass = roles.get(i);

                    System.out.println("Assignation du rôle " + roleClass.getSimpleName() + " au joueur " + player.getName());
                    player.setRole(roleClass.getConstructor(Module.class).newInstance(module));
                } catch (Exception e) {
                    System.out.println("Erreur lors de l'assignation du rôle : " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        uhcData.getUhcPlayerList().forEach(uhcPlayer -> {
            if (uhcPlayer.getRole() != null) {
                uhcPlayer.getRole().setRole();
                if (uhcPlayer.getRole() instanceof DNRole) {
                    ((DNRole) uhcPlayer.getRole()).setNumberOfInvest(0);
                }
            }
        });

        Bukkit.getScheduler().runTaskLater(uhc, () ->
                uhcData.getUhcPlayerList().forEach(uhcPlayer -> {
                    if (uhcPlayer.getRole() != null) {
                        uhcPlayer.getRole().sendDesc();
                    }
                }), 20L);
    }

    List<UHCPlayer> canSeeRealDeaths = new ArrayList<>();
    @EventHandler
    public void onDeath(DeathEvent deathEvent){
        PlayersUtils.broadcastMessage("§c" + deathEvent.getVictim().getName() + "§7 est mort.");
        canSeeRealDeaths.clear();

        UHC.getInstance().getUhcGame().getUhcData().getUhcPlayerList().stream().filter(uhcPlayer -> uhcPlayer.getRole() != null).forEach(uhcPlayer -> {
            if (( uhcPlayer.getRole()) instanceof Investigator && ((Investigator) uhcPlayer.getRole()).getPower().equals(InvestigatorPowers.REAL_DEADS))
                canSeeRealDeaths.add(uhcPlayer);
            if (uhcPlayer.getRole() instanceof Mello && ((Mello) uhcPlayer.getRole()).isGod())
                canSeeRealDeaths.add(uhcPlayer);
        });

        canSeeRealDeaths.forEach(uhcPlayer -> {
            if (deathEvent.getKiller() == null) {
                uhcPlayer.sendMessage("§c" + deathEvent.getVictim().getName() + " §7est mort seul.");
            } else {
                uhcPlayer.sendClassicMessage("§c" + deathEvent.getVictim().getName() + " §7s'est fait tué(e) par §c" + deathEvent.getKiller().getName());
            }
        });


        SoundUtils.playSoundToAll(Sound.WITHER_SPAWN);
        deathEvent.getVictim().getBukkitPlayer().spigot().respawn();
        deathEvent.getVictim().setPlayerState(DEAD);
        UHC.getInstance().getUhcGame().getUhcData().getUhcPlayerList().remove(deathEvent.getVictim());
        UHCTeam uhcTeam = deathEvent.getVictim().getUhcTeam();
        deathEvent.getVictim().leaveTeam();
        if (deathEvent.getVictim().getRole() != null) {
            DNRole role = (DNRole) deathEvent.getVictim().getRole();
            Bukkit.getScheduler().runTaskLater(UHC.getInstance(), () -> {
                if (!(role instanceof Shinigami))
                    return;
                AtomicReference<UHCPlayer> kiraPlayer = new AtomicReference<>();
                uhcTeam.getUhcPlayerList().stream().filter(uhcPlayer -> uhcPlayer.getRole() != null && uhcPlayer.getRole() instanceof Kira).forEach(kiraPlayer::set);
                if (kiraPlayer.get() != null && !(((DNRole) kiraPlayer.get().getRole()).isReveal())) {
                    ((DNRole) kiraPlayer.get().getRole()).reveal();
                }
            }, TimeUtils.minutes(1));
        }



        if(!uhcTeam.isAlive()){
            uhcTeam.destroy();
        }

        module.winTester();

        Player player = deathEvent.getVictim().getBukkitPlayer();
        Bukkit.getScheduler().runTaskLater(UHC.getInstance(), () -> {
            if (!UHC.getInstance().getUhcGame().getUhcConfig().getAllowSpec().equals("nobody")){
                player.spigot().respawn();
                player.setGameMode(GameMode.SPECTATOR);
                player.teleport(WorldManager.getCenterLoc());
            } else {
                player.kickPlayer(UHC.getInstance().getPrefix() + " §7Je suis navré de devoir vous expulser car les spectateurs sont désactivés dans cette partie, néanmoins je vous attend pour revenir dès la prochaine partie !");
            }
        }, 5);
    }


    @EventHandler
    public void onHit(EntityDamageByEntityEvent event){
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player))
            return;
        UHCPlayer hitter = UHC.getUHCPlayer(((Player) event.getDamager()));
        if (!(hitter.getBukkitPlayer().getItemInHand().getType() == Material.GOLD_HOE)) return;
        UHCPlayer target = UHC.getUHCPlayer(((Player) event.getEntity()));
        if (hitter.getRole() == null || target.getRole() == null) return;
        if (!(hitter.getRole() instanceof Near)) return;
        if (((Near) hitter.getRole()).isTryKiraKiller()) {
            hitter.sendClassicMessage("§cVous ne pouvez essayer de KiraKiller qu'une seule fois par épisode !");
            return;
        }
        if (target.getRole() instanceof Kira && !((Kira) target.getRole()).isReveal()) {
            target.getBukkitPlayer().setMaxHealth(target.getBukkitPlayer().getMaxHealth() - (((Kira) target.getRole()).isGetKiraKiller() ? 5 : 10));
            ((Kira) target.getRole()).setGetKiraKiller(true);
            ((Near) hitter.getRole()).setTryKiraKiller(true);
        }
        else {
            hitter.getBukkitPlayer().setMaxHealth(hitter.getBukkitPlayer().getMaxHealth() - 10);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent interactEvent){
        if(interactEvent.getItem() != null && interactEvent.getItem().getItemMeta().hasDisplayName()){
            if(interactEvent.getItem().getType() == Material.ENCHANTED_BOOK && interactEvent.getItem().getItemMeta().getDisplayName().contains("§4§lDEATH NOTE")){
                UHCPlayer uhcPlayer = UHC.getUHCPlayer(interactEvent.getPlayer());
                if(uhcPlayer.getRole() instanceof Kira){
                    new KiraMainGUI(((Kira) uhcPlayer.getRole())).open(uhcPlayer.getBukkitPlayer());
                }
            }
        }
    }
}
