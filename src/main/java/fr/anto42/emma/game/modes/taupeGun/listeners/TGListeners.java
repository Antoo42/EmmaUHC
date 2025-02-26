package fr.anto42.emma.game.modes.taupeGun.listeners;


import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.listeners.customListeners.DeathEvent;
import fr.anto42.emma.coreManager.listeners.customListeners.RolesEvent;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.coreManager.teams.UHCTeam;
import fr.anto42.emma.coreManager.teams.UHCTeamManager;
import fr.anto42.emma.coreManager.worldManager.WorldManager;
import fr.anto42.emma.game.impl.UHCData;
import fr.anto42.emma.game.modes.taupeGun.TGModule;
import fr.anto42.emma.game.modes.taupeGun.impl.TRole;
import fr.anto42.emma.game.modes.taupeGun.roles.Kits;
import fr.anto42.emma.game.modes.taupeGun.roles.SuperTaupe;
import fr.anto42.emma.game.modes.taupeGun.roles.Taupe;
import fr.anto42.emma.utils.players.InventoryUtils;
import fr.anto42.emma.utils.players.PlayersUtils;
import fr.anto42.emma.utils.SoundUtils;
import fr.anto42.emma.utils.chat.Title;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static fr.anto42.emma.coreManager.players.UHCPlayerStates.DEAD;

public class TGListeners implements Listener {
    private final TGModule module;

    public TGListeners(TGModule module) {
        this.module = module;
    }


    List<UHCTeam> teamList = new ArrayList<>();
    @EventHandler
    public void onRoles(RolesEvent event) {
        PlayersUtils.broadcastMessage("§7Sélection des taupes en cours...");
        Bukkit.getScheduler().runTaskLater(UHC.getInstance(), () -> {
            List<UHCTeam> taupesTeams = module.getData().getTeamList();
            List<UHCTeam> aliveTeams = new ArrayList<>(UHCTeamManager.getInstance().getUhcTeams());

            taupesTeams.forEach(uhcTeam ->
                    System.out.println(module.getData().getUhcTeamIntegerHashMap().get(uhcTeam))
            );

            Collections.shuffle(aliveTeams);
            int teamCounter = 1;
            UHCTeam currentTeam = createTaupeTeam(teamCounter);

            for (UHCTeam uhcTeam : aliveTeams) {
                List<UHCPlayer> players = new ArrayList<>(uhcTeam.getUhcPlayerList());
                List<UHCPlayer> playersToRemove = new ArrayList<>();

                for (int i = 0; i < module.getConfig().getTaupePerTeams() && !players.isEmpty(); i++) {
                    if (module.getData().getUhcTeamIntegerHashMap().get(currentTeam) >= module.getConfig().getTaupeSlots()) {
                        teamCounter++;
                        currentTeam = createTaupeTeam(teamCounter);
                    }

                    UHCPlayer taupe = players.remove(new Random().nextInt(players.size()));
                    playersToRemove.add(taupe);
                    taupe.setRole(new Taupe(module));

                    module.getData().getUhcTeamIntegerHashMap().put(currentTeam, teamCounter);
                    ((TRole) taupe.getRole()).setTaupeTeam(currentTeam);

                    Title.sendTitle(taupe.getBukkitPlayer(), 0, 20 * 5, 15, "§cVous êtes la Taupe !", "§6§oNe le dîtes à personne !");
                    SoundUtils.playSoundToPlayer(taupe.getBukkitPlayer(), Sound.ANVIL_LAND);
                    taupe.getRole().sendDesc();
                    taupe.getRole().setRole();
                }
                uhcTeam.getUhcPlayerList().removeAll(playersToRemove);
            }
        }, 10L);

        if (module.getConfig().isSuperTaupe()) {
            Bukkit.getScheduler().runTaskLater(UHC.getInstance(), () -> {
                PlayersUtils.broadcastMessage("§7Sélection des super taupes en cours...");
                Bukkit.getScheduler().runTaskLater(UHC.getInstance(), () -> {
                    module.getData().getTeamList().forEach(uhcTeam -> {
                        List<UHCPlayer> taupes = new ArrayList<>();

                        UHC.getInstance().getUhcGame().getUhcData().getUhcPlayerList()
                                .stream()
                                .filter(player -> player.getRole() instanceof TRole && ((TRole) player.getRole()).getTaupeTeam() == uhcTeam)
                                .forEach(taupes::add);

                        if (!taupes.isEmpty()) {
                            UHCPlayer superTaupe = taupes.get(new Random().nextInt(taupes.size()));
                            Kits kit = ((TRole) superTaupe.getRole()).getKit();
                            boolean hasClaim = ((TRole) superTaupe.getRole()).isHasClaim();

                            superTaupe.setRole(new SuperTaupe(module));
                            ((SuperTaupe) superTaupe.getRole()).setHasClaim(hasClaim);
                            ((SuperTaupe) superTaupe.getRole()).setKit(kit);
                            ((SuperTaupe) superTaupe.getRole()).sendDesc();

                            module.getData().getRevealPlayers().remove(superTaupe);
                            SoundUtils.playSoundToPlayer(superTaupe.getBukkitPlayer(), Sound.ANVIL_LAND);
                            Title.sendTitle(superTaupe.getBukkitPlayer(), 0, 20 * 5, 15, "§cVous êtes une Super Taupe !", "§6§oNe le dîtes à personne !");
                        }
                    });
                }, 15L);
            }, 20L * 60 * module.getConfig().getTimerSuperTaupe());
        }
    }

    private UHCTeam createTaupeTeam(int index) {
        UHCTeam newTeam = UHCTeamManager.getInstance().createNewTeam(
                "taupe-" + index, "§c§lTaupe " + index + " ", DyeColor.RED, 14, "§c"
        );
        module.getData().getUhcTeamIntegerHashMap().put(newTeam, 0);
        module.getData().getTeamList().add(newTeam);
        this.teamList.add(newTeam);
        return newTeam;
    }

    private final Map<UUID, Location> deathLocationMap = new HashMap<>();

    @EventHandler
    public void onDeathP(PlayerDeathEvent playerDeathEvent){
        deathLocationMap.put(playerDeathEvent.getEntity().getUniqueId(), playerDeathEvent.getEntity().getLocation());
        InventoryUtils.registerInventory(playerDeathEvent.getEntity().getUniqueId(), playerDeathEvent.getEntity());
    }

    AtomicInteger a = new AtomicInteger();
    @EventHandler
    public void onDeath(DeathEvent deathEvent){
        UHCData gameManager = UHC.getInstance().getUhcGame().getUhcData();
        UHCPlayer victim = deathEvent.getVictim();
        UHCTeam uhcTeam = victim.getUhcTeam();
        Location loc = deathEvent.getVictim().getBukkitPlayer().getLocation();
        PlayersUtils.broadcastMessage("§c" + uhcTeam.getPrefix() + victim.getName() + "§7 est mort !");
        if (victim.getRole() != null && ((TRole) victim.getRole()).getTaupeTeam() != null){
            System.out.println("oui");
            ((TRole) victim.getRole()).setTaupeTeam(null);
        }
        SoundUtils.playSoundToAll(Sound.WITHER_SPAWN);

        deathEvent.getVictim().leaveTeam();
        gameManager.getUhcPlayerList().remove(victim);
        victim.setPlayerState(DEAD);

        Bukkit.getScheduler().runTaskLater(UHC.getInstance(), ()->{
            if(!uhcTeam.isAlive()){
                Bukkit.getScheduler().runTaskLater(UHC.getInstance(), uhcTeam::destroy, 2);
            }
            UHCTeamManager.getInstance().getUhcTeams().stream().filter(uhcTeam1 -> !uhcTeam1.isAlive()).forEach(uhcTeam1 -> {
                a.set(0);
                UHC.getInstance().getUhcGame().getUhcData().getUhcPlayerList().stream().filter(uhcPlayer -> uhcPlayer.getRole() != null && uhcPlayer.getRole() instanceof TRole).forEach(uhcPlayer -> {
                    if (((TRole) uhcPlayer.getRole()).getTaupeTeam() != null && ((TRole) uhcPlayer.getRole()).getTaupeTeam() == uhcTeam1)
                        a.getAndIncrement();
                });
                System.out.println(a.get());
                if (a.equals(0)) uhcTeam1.destroy();
            });
        }, 15);


        if(victim.getRole() instanceof Taupe){
            int l = 0;
            for (UHCPlayer aliveUhcPlayer : gameManager.getUhcPlayerList()) {
                if(aliveUhcPlayer.getRole() instanceof Taupe && ((Taupe) aliveUhcPlayer.getRole()).getTaupeTeam() == ((Taupe) victim.getRole()).getTaupeTeam() && ((Taupe) victim.getRole()).getTaupeTeam() != null) {
                    l++;

                }
            }
            if(l == 0){
                module.getData().getTeamList().remove(((Taupe) victim.getRole()).getTaupeTeam());
                ((Taupe) victim.getRole()).getTaupeTeam().destroy();

            }
        }

        Bukkit.getScheduler().runTaskLater(UHC.getInstance(), module::winTester, 17);


        Player player = victim.getBukkitPlayer();
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
}
