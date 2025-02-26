package fr.anto42.emma.game.modes.deathNote.impl;

import fr.anto42.emma.coreManager.teams.UHCTeam;
import fr.anto42.emma.game.modes.deathNote.DeathNoteModule;
import fr.anto42.emma.game.modes.deathNote.roles.Kira;
import fr.anto42.emma.game.modes.deathNote.roles.camps.KiraCamp;

public class DeathNoteData {
    private final DeathNoteModule module;

    public DeathNoteData(DeathNoteModule module) {
        this.module = module;
    }


    private final KiraCamp kiraCamp = new KiraCamp();

    public KiraCamp getKiraCamp() {
        return kiraCamp;
    }
}
