package fr.anto42.emma.game.modes.deathNote.utils;

import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.coreManager.players.UHCPlayerStates;
import fr.anto42.emma.game.modes.deathNote.DeathNoteModule;
import fr.anto42.emma.game.modes.deathNote.impl.DNRole;
import fr.anto42.emma.game.modes.deathNote.roles.Investigator;
import fr.anto42.emma.game.modes.deathNote.roles.Kira;
import fr.anto42.emma.game.modes.deathNote.roles.Mello;
import fr.anto42.emma.game.modes.deathNote.roles.Near;
import fr.anto42.emma.utils.chat.Title;
import org.bukkit.scheduler.BukkitRunnable;

public class InvestTask extends BukkitRunnable {
    private final UHCPlayer investigator;
    private final UHCPlayer target;
    private final DeathNoteModule module;

    private double multi = 1;

    public InvestTask(UHCPlayer investigator, UHCPlayer target, DeathNoteModule module) {
        this.investigator = investigator;
        this.target = target;
        this.module = module;
        ((DNRole) investigator.getRole()).increaseNumberOfInvest();
        investigator.getUhcTeam().getAliveUhcPlayers().stream().filter(uhcPlayer -> uhcPlayer.getRole() != null && uhcPlayer.getRole() instanceof Mello).forEach(uhcPlayer -> {
            if (((Mello) uhcPlayer.getRole()).getMeloType().equals(MeloType.KIND)) multi = 1.5;
        });
        if (investigator.getRole() instanceof Mello && ((Mello) investigator.getRole()).getMeloType().equals(MeloType.JEALOUS)) {
            multi = 2;
        }
        if (investigator.getRole() instanceof Near && target.getRole() instanceof Mello && ((Mello) target.getRole()).getMeloType().equals(MeloType.EVIL)) multi = 0.5;
        System.out.println(((DNRole) investigator.getRole()).getNumberOfInvest());
    }
    private double remainPoints = GameUtils.getModule().getDeathNoteConfig().getPointsForInvestigation();
    @Override
    public void run() {
        if (!investigator.getPlayerState().equals(UHCPlayerStates.ALIVE)) {
            this.cancel();
        }
        Title.sendActionBar(investigator.getBukkitPlayer(), "§8§l» §7Points restants pour l'enquête : §b" + remainPoints);
        System.out.println(remainPoints);
        if(investigator.getBukkitPlayer().getLocation().distance(target.getBukkitPlayer().getLocation()) > 6){
            remainPoints = remainPoints - multi;
        }else if(investigator.getBukkitPlayer().getLocation().distance(target.getBukkitPlayer().getLocation()) <= 6 && investigator.getBukkitPlayer().getLocation().distance(target.getBukkitPlayer().getLocation()) >= 1){
            remainPoints = remainPoints - 5*multi;
        }else if (investigator.getBukkitPlayer().getLocation().distance(target.getBukkitPlayer().getLocation()) < 1){
            remainPoints = remainPoints - 10*multi;
        }
        if (remainPoints <= 0) {
            System.out.println(((DNRole) investigator.getRole()).getNumberOfInvest());
            this.cancel();
            if (investigator.getRole() instanceof Investigator || investigator.getRole() instanceof Mello && ((Mello) investigator.getRole()).getMeloType().equals(MeloType.KIND) || investigator.getRole() instanceof Near) {
                investigator.sendClassicMessage("§aL'enquête sur §3" + target.getName() + " §arévèle qu'il est: §6" + ((DNRole) target.getRole()).getInvestigationResultType().getName());
            }
            else {
                investigator.sendClassicMessage("§aL'enquête sur §3" + target.getName() + " §arévèle qu'il est: §6" + target.getRole().getName() + " §7(" + ((DNRole) target.getRole()).getInvestigationResultType().getName() + ")");
                ((DNRole) investigator.getRole()).setCanInvest(true);
                if (((Mello) investigator.getRole()).getNumberOfInvest() == 4 && (!(target.getRole() instanceof Kira))) {
                    investigator.getBukkitPlayer().setHealth(0);
                } else if ((target.getRole() instanceof Kira)) {
                    ((Mello) investigator.getRole()).setGod(true);
                    ((DNRole) investigator.getRole()).setCanInvest(false);
                    investigator.sendClassicMessage("§aVous obtenez tout les pouvoirs des enquêteurs !");
                }
            }
            System.out.println(((DNRole) investigator.getRole()).getNumberOfInvest());

        }
    }
}
