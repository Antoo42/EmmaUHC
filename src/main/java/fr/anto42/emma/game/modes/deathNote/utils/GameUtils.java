package fr.anto42.emma.game.modes.deathNote.utils;

import fr.anto42.emma.game.modes.deathNote.DeathNoteModule;

public class GameUtils {
    private static DeathNoteModule module;
    public static void init(DeathNoteModule dnModule){
        module = dnModule;
    }

    public static DeathNoteModule getModule(){
        return module;
    }
}
