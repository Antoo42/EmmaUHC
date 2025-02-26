package fr.anto42.emma.coreManager.tasks;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.teams.UHCTeam;
import fr.anto42.emma.coreManager.teams.UHCTeamManager;
import fr.anto42.emma.game.UHCGame;
import fr.anto42.emma.utils.chat.Title;
import fr.anto42.emma.utils.materials.WorldUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.StringJoiner;

public class ArrowTask extends BukkitRunnable {
    private final UHCGame uhc = UHC.getInstance().getUhcGame();
    private final UHCTeamManager teamManager = UHCTeamManager.getInstance();

    @Override
    public void run() {
        if (!teamManager.isActivated() || !teamManager.isDirectionalArrow()) {
            return;
        }

        uhc.getUhcData().getUhcPlayerList().stream()
                .filter(player -> player.getBukkitPlayer() != null && player.getUhcTeam() != null)
                .forEach(uhcPlayer -> {
                    Player player = uhcPlayer.getBukkitPlayer();
                    UHCTeam team = uhcPlayer.getUhcTeam();
                    StringJoiner joiner = new StringJoiner(" §8┃ ");

                    team.getUhcPlayerList().stream()
                            .filter(teammate -> !uhcPlayer.getName().equals(teammate.getName())
                                    && teammate.getBukkitPlayer() != null
                                    && player.getWorld() == teammate.getBukkitPlayer().getWorld())
                            .forEach(teammate -> {
                                Player matePlayer = teammate.getBukkitPlayer();
                                double distance = WorldUtils.distance(player.getLocation(), matePlayer.getLocation());
                                String arrow = WorldUtils.Fleche(WorldUtils.Angle(player, matePlayer.getLocation()));
                                String info = teammate.getUhcTeam().getColor() + teammate.getName() +
                                        " §c" + (int) distance + "§cm " + arrow;
                                joiner.add(info);
                            });

                    Title.sendActionBar(player, joiner.toString());
                });
    }
}

