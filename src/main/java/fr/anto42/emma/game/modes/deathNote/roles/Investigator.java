package fr.anto42.emma.game.modes.deathNote.roles;

import fr.anto42.emma.coreManager.Module;
import fr.anto42.emma.game.modes.deathNote.impl.DNRole;
import fr.anto42.emma.game.modes.deathNote.utils.InvestigationResultType;
import fr.anto42.emma.game.modes.deathNote.utils.InvestigatorPowers;

import java.util.Random;

public class Investigator extends DNRole {
    private InvestigatorPowers power;
    public Investigator(Module gamemode) {
        super("Enquêteur", null, gamemode);
        setInvestigationResultType(InvestigationResultType.KIND);
    }

    @Override
    public void setRole() {
        super.setRole();
        int r = new Random().nextInt(3);
        power = InvestigatorPowers.getById(r);
        assert power != null;
        if (power.equals(InvestigatorPowers.INVESTIGATION)) {
            setCanInvest(true);
        }
    }

    @Override
    public void sendDesc() {
        super.sendDesc();

        getUhcPlayer().sendMessage("tu as le pouvoir de " + getPower().getName());
    }

    public InvestigatorPowers getPower() {
        return power;
    }

}
