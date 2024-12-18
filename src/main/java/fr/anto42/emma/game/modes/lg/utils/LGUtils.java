package fr.anto42.emma.game.modes.lg.utils;

import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.game.modes.lg.LGModule;

public class LGUtils {
    public static void sendLGMessage(UHCPlayer player, String message) {
        player.sendMessage("§c§lLG §8§l» §7" + message);
    }


    private static LGModule module;

    public static LGModule getModule(){
        return module;
    }

    public static void setModule(LGModule module) {
        LGUtils.module = module;
    }
}
