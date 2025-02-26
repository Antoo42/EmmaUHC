package fr.anto42.emma.game.modes.deathNote.roles;

import fr.anto42.emma.coreManager.Module;
import fr.anto42.emma.game.modes.deathNote.impl.DNRole;
import fr.anto42.emma.game.modes.deathNote.utils.InvestigationResultType;
import fr.anto42.emma.utils.materials.ItemCreator;
import org.bukkit.Material;

public class Near extends DNRole {
    private boolean tryKiraKiller = false;
    public Near(Module gamemode) {
        super("Near", null, gamemode);
        setInvestigationResultType(InvestigationResultType.KIND);
        setCanInvest(true);
    }

    @Override
    public void setRole() {
        super.setRole();
        getUhcPlayer().safeGive(new ItemCreator(Material.GOLD_HOE).unbreakable(true).lore("", "§8§l» §7Frappez un kira.").name("§e§lKira killer").get());
    }

    @Override
    public void onEpisode() {
        setTryKiraKiller(false);
    }

    public boolean isTryKiraKiller() {
        return tryKiraKiller;
    }

    public void setTryKiraKiller(boolean tryKiraKiller) {
        this.tryKiraKiller = tryKiraKiller;
    }
}
