package fr.anto42.emma.game.modes.deathNote.roles;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.Module;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.coreManager.teams.UHCTeam;
import fr.anto42.emma.coreManager.teams.UHCTeamManager;
import fr.anto42.emma.game.modes.deathNote.impl.DNRole;
import fr.anto42.emma.game.modes.deathNote.utils.InvestigationResultType;
import fr.anto42.emma.game.modes.deathNote.utils.MeloType;
import fr.anto42.emma.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;

import java.util.Random;

public class Mello extends DNRole {
    private MeloType meloType;
    private boolean god = false;
    public Mello(Module gamemode) {
        super("Mello", null, gamemode);
    }

    @Override
    public void reveal() {
        super.reveal();
        if (getMeloType() != null && getMeloType() == MeloType.EVIL) {
            UHCTeam uhcTeam = UHCTeamManager.getInstance().createNewTeam("Melo-" + getUhcPlayer().getName(), "§6§lMelo ", DyeColor.ORANGE, 0, "§6");
            getUhcPlayer().joinTeam(uhcTeam);
            getUhcPlayer().getBukkitPlayer().setMaxHealth(getUhcPlayer().getBukkitPlayer().getMaxHealth() + 10);
        }
    }



    @Override
    public void setRole() {
        super.setRole();
        Bukkit.getScheduler().runTaskLater(UHC.getInstance(), () -> {
            if (getMeloType() != null)
                return;
            getUhcPlayer().sendClassicMessage("§cVous avez mis trop de temps à choisir votre type !");
            int id = new Random().nextInt(2);
            setMeloType(MeloType.getById(id));
            setInvestigationResultType(getMeloType().getInvestigationResultType());
            setupMeloType();
            sendDesc();
        }, TimeUtils.minutes(2));
    }

    UHCPlayer nearPlayer;
    public void setupMeloType () {
        if (getMeloType().equals(MeloType.KIND)) {
            setCanInvest(true);
            setInvestigationResultType(InvestigationResultType.KIND);

        }
        if (getMeloType().equals(MeloType.JEALOUS)) {
            setCanInvest(true);
            //setNumberOfInvest(0);
            setMaxNumberOfInvest(4);
            setInvestigationResultType(InvestigationResultType.SUSPECT);
        }
        if (getMeloType().equals(MeloType.EVIL)) {
            Bukkit.getScheduler().runTaskLater(UHC.getInstance(), () -> {
                getUhcPlayer().getUhcTeam().getAliveUhcPlayers().stream().filter(uhcPlayer -> uhcPlayer.getRole() != null && uhcPlayer.getRole() instanceof Near).forEach(uhcPlayer -> {
                    nearPlayer = uhcPlayer;
                });
            }, 5L);
            setInvestigationResultType(InvestigationResultType.SUSPECT);

        }
        Bukkit.getScheduler().runTaskLater(UHC.getInstance(), this::sendDesc, 10L);
    }

    @Override
    public void sendDesc() {
        super.sendDesc();

        if(getMeloType() != null && getMeloType() == MeloType.EVIL) getUhcPlayer().sendMessage("§7Le near de ta team est: " + (nearPlayer == null ? "§cinconnu" : nearPlayer.getName()));

    }

    public MeloType getMeloType() {
        return meloType;
    }

    public void setMeloType(MeloType meloType) {
        this.meloType = meloType;
    }


    public boolean isGod() {
        return god;
    }

    public void setGod(boolean god) {
        this.god = god;
    }
}
