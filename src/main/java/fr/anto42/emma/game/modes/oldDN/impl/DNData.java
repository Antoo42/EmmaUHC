package fr.anto42.emma.game.modes.oldDN.impl;

import fr.anto42.emma.coreManager.players.roles.Camp;
import fr.anto42.emma.game.modes.oldDN.roles.camps.KiraCamp;

public class DNData {
    private final Camp kiraCamp = new KiraCamp();
    public Camp getKiraCamp() {
        return kiraCamp;
    }
}
