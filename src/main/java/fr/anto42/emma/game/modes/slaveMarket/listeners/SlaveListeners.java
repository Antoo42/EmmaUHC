package fr.anto42.emma.game.modes.slaveMarket.listeners;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.listeners.customListeners.DeathEvent;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.coreManager.teams.UHCTeam;
import fr.anto42.emma.coreManager.teams.UHCTeamManager;
import fr.anto42.emma.coreManager.worldManager.WorldManager;
import fr.anto42.emma.game.UHCGame;
import fr.anto42.emma.game.modes.slaveMarket.SlaveModule;
import fr.anto42.emma.utils.players.SoundUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SlaveListeners implements Listener {
    private final SlaveModule module;

    public SlaveListeners(SlaveModule module) {
        this.module = module;
    }
    private final UHCGame uhcGame = UHC.getInstance().getUhcGame();


    @EventHandler
    public void onDeath(DeathEvent deathEvent) {
        UHCPlayer victim = deathEvent.getVictim();

        if (!UHCTeamManager.getInstance().isActivated()) {
            if (deathEvent.getKiller() != null)
                Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §c" + victim.getName() + "§7 est mort des mains de §a" + deathEvent.getKiller().getName() + "§7 !");
            else
                Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §c" + victim.getName() + "§7 est mort seul !");
        } else {
            if (victim.getUhcTeam() != null) {
                UHCTeam uhcTeam = victim.getUhcTeam();
                if (deathEvent.getKiller() != null)
                    Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " " + uhcTeam.getPrefix() + victim.getName() + "§7 est mort des mains de §a" + deathEvent.getKiller().getUhcTeam().getPrefix() + deathEvent.getKiller().getName() + "§7 !");
                else
                    Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " " + uhcTeam.getPrefix() + victim.getName() + "§7 est mort !");
                UHCTeam uhcTeam1 = victim.getUhcTeam();
                victim.leaveTeam();
                Bukkit.getScheduler().runTaskLater(UHC.getInstance(), () -> {
                    if (uhcTeam1.getAliveUhcPlayers().isEmpty()) {
                        Bukkit.getScheduler().runTaskLater(UHC.getInstance(), uhcTeam1::destroy, 2);
                    }
                }, 15);
            }else{
                if (deathEvent.getKiller() != null)
                    Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " " + victim.getName() + "§7 est mort des mains de §a" + deathEvent.getKiller().getUhcTeam().getPrefix() + deathEvent.getKiller().getName() + "§7 !");
                else
                    Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " " + victim.getName() + "§7 est mort !");
            }

        }

        SoundUtils.playSoundToAll(Sound.WITHER_SPAWN);
        uhcGame.getUhcData().getUhcPlayerList().remove(victim);
        Bukkit.getScheduler().runTaskLater(UHC.getInstance(), module::winTester, 3L);
        Player player = victim.getBukkitPlayer();
        Bukkit.getScheduler().runTaskLater(UHC.getInstance(), () -> {
            if (!uhcGame.getUhcConfig().getAllowSpec().equals("nobody")){
                player.spigot().respawn();
                player.setGameMode(GameMode.SPECTATOR);
                player.teleport(WorldManager.getCenterLoc());
            } else {
                player.kickPlayer(UHC.getInstance().getPrefix() + " §7Je suis navré de devoir vous expulser car les spectateurs sont désactivés dans cette partie, néanmoins je vous attend pour revenir dès la prochaine partie !");
            }
        }, 5);
    }
}
