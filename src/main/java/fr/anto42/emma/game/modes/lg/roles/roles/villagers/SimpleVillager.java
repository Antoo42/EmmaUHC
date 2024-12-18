package fr.anto42.emma.game.modes.lg.roles.roles.villagers;

import fr.anto42.emma.coreManager.Module;
import fr.anto42.emma.coreManager.players.roles.Camp;
import fr.anto42.emma.game.modes.lg.LGModule;
import fr.anto42.emma.game.modes.lg.roles.LGRole;

public class SimpleVillager extends LGRole {
    public SimpleVillager(LGModule gamemode) {
        super("Simple Villageois", gamemode.getLgData().getVillageCamp(), gamemode);
        setRoleDesc("aucun pouvoir spécial");
    }
}
