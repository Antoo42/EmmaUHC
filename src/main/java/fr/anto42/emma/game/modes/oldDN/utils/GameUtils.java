package fr.anto42.emma.game.modes.oldDN.utils;

import fr.anto42.emma.game.modes.oldDN.DNModule;

public class GameUtils {

    private static DNModule module;
    public static void init(DNModule dnModule){
        module = dnModule;
    }

    public static DNModule getModule(){
        return module;
    }
}
