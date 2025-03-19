package fr.anto42.emma.coreManager.listeners.customListeners;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.utils.TimeUtils;
import fr.anto42.emma.utils.gameSaves.EventType;

public class DeathEvent extends UHCEvent{
    private final UHCPlayer victim;
    private UHCPlayer killer = null;

    public DeathEvent(UHCPlayer victim, UHCPlayer killer) {
        this.victim = victim;
        if (killer != null) {
            this.killer = killer;
        };
        UHC.getInstance().getGameSave().registerEvent(EventType.DEATHS,"Mort de :" + victim.getName() + ", cause: " +  (killer == null ? "PvE" : "tué par " + killer.getName()) + ", timer: " + TimeUtils.getFormattedTime(UHC.getInstance().getUhcGame().getUhcData().getTimer()));
    }

    public UHCPlayer getVictim() {
        return victim;
    }

    public UHCPlayer getKiller() {
        return killer;
    }
}
