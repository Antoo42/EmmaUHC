package fr.anto42.emma.game.modes.deathNote.roles;

import fr.anto42.emma.coreManager.Module;
import fr.anto42.emma.coreManager.teams.UHCTeam;
import fr.anto42.emma.coreManager.teams.UHCTeamManager;
import fr.anto42.emma.game.modes.deathNote.DeathNoteModule;
import fr.anto42.emma.game.modes.deathNote.impl.DNRole;
import fr.anto42.emma.game.modes.deathNote.utils.InvestigationResultType;
import fr.anto42.emma.utils.materials.ItemCreator;
import org.bukkit.DyeColor;
import org.bukkit.Material;

public class Kira extends DNRole {
    private boolean kiraThisEp = false;
    public Kira(Module gamemode) {
        super("Kira", ((DeathNoteModule) gamemode).getDeathNoteData().getKiraCamp(), gamemode);
        setInvestigationResultType(InvestigationResultType.SUSPECT);
    }

    @Override
    public void setRole() {
        getUhcPlayer().safeGive(new ItemCreator(Material.ENCHANTED_BOOK).name("§4§lDEATH NOTE").get());
    }

    private boolean getKiraKiller = false;
    @Override
    public void reveal() {
        super.reveal();
        UHCTeam uhcTeam = UHCTeamManager.getInstance().createNewTeam("Kira-" + getUhcPlayer().getName(), "§c§lKira ", DyeColor.RED, 0, "§c");
        getUhcPlayer().joinTeam(uhcTeam);
    }

    public boolean isGetKiraKiller() {
        return getKiraKiller;
    }

    public void setGetKiraKiller(boolean getKiraKiller) {
        this.getKiraKiller = getKiraKiller;
    }

    @Override
    public void onEpisode() {
        if (getUhcPlayer().getBukkitPlayer() == null) return;
        if (getKiraKiller && getUhcPlayer().getBukkitPlayer().getMaxHealth() < 20) {
            getUhcPlayer().getBukkitPlayer().setMaxHealth(getUhcPlayer().getBukkitPlayer().getMaxHealth() + 2);
        }
        setKiraThisEp(false);
    }

    public boolean isKiraThisEp() {
        return kiraThisEp;
    }

    public void setKiraThisEp(boolean kiraThisEp) {
        this.kiraThisEp = kiraThisEp;
    }
}
