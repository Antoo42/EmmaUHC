package fr.anto42.emma.game.modes.deathNote.impl;

import fr.anto42.emma.coreManager.Module;
import fr.anto42.emma.coreManager.players.roles.Camp;
import fr.anto42.emma.coreManager.players.roles.Role;
import fr.anto42.emma.coreManager.teams.UHCTeam;
import fr.anto42.emma.game.modes.deathNote.utils.InvestigationResultType;
import fr.anto42.emma.utils.players.SoundUtils;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.players.PlayersUtils;
import org.bukkit.Material;
import org.bukkit.Sound;

public abstract class DNRole extends Role {
    private boolean canInvest = false;
    private boolean inInvest = false;
    private int numberOfInvest = 0;
    private int maxNumberOfInvest = 1;
    private boolean reveal = false;
    private boolean seeLife = false;
    private boolean canReveal = false;

    @Override
    public void sendDesc() {
        super.sendDesc();
        getUhcPlayer().sendMessage("Tu es: " + getName());
    }

    private InvestigationResultType investigationResultType;

    public DNRole(String name, Camp camp, Module gamemode) {
        super(name, camp, gamemode);
    }

    public void reveal() {
        if (!canReveal)
            return;
        UHCTeam uhcTeam = getUhcPlayer().getUhcTeam();
        setReveal(true);
        getUhcPlayer().safeGive(new ItemCreator(Material.GOLDEN_APPLE).get());
        SoundUtils.playSoundToAll(Sound.GHAST_SCREAM);
        getUhcPlayer().leaveTeam();
        if (!uhcTeam.isAlive()) {
            uhcTeam.destroy();
        }
        PlayersUtils.broadcastMessage("§c" + getUhcPlayer().getName() + " §7se révèle être §c" + getName() + "§7 !");
    }

    public InvestigationResultType getInvestigationResultType() {
        return investigationResultType;
    }

    public void setInvestigationResultType(InvestigationResultType investigationResultType) {
        this.investigationResultType = investigationResultType;
    }

    public boolean isCanInvest() {
        return canInvest && numberOfInvest < maxNumberOfInvest && !inInvest;
    }

    public void setCanInvest(boolean canInvest) {
        this.canInvest = canInvest;
    }

    public boolean isInInvest() {
        return inInvest;
    }

    public void setInInvest(boolean inInvest) {
        this.inInvest = inInvest;
        numberOfInvest++;
    }

    public int getNumberOfInvest() {
        return numberOfInvest;
    }

    public void setNumberOfInvest(int numberOfInvest) {
        this.numberOfInvest = numberOfInvest;
    }
    public void increaseNumberOfInvest () {
        this.numberOfInvest++;
    }

    public int getMaxNumberOfInvest() {
        return maxNumberOfInvest;
    }

    public void setMaxNumberOfInvest(int maxNumberOfInvest) {
        this.maxNumberOfInvest = maxNumberOfInvest;
    }

    public boolean isReveal() {
        return reveal;
    }

    public void setReveal(boolean reveal) {
        this.reveal = reveal;
    }

    public boolean isSeeLife() {
        return seeLife;
    }

    public void setSeeLife(boolean seeLife) {
        this.seeLife = seeLife;
    }

    public boolean isCanReveal() {
        return canReveal;
    }

    public void setCanReveal(boolean canReveal) {
        this.canReveal = canReveal;
    }
}
