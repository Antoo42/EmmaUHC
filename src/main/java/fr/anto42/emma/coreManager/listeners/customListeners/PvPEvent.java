package fr.anto42.emma.coreManager.listeners.customListeners;

import fr.anto42.emma.UHC;
import fr.anto42.emma.utils.TimeUtils;
import fr.anto42.emma.utils.gameSaves.EventType;

public class PvPEvent extends UHCEvent {
    public PvPEvent() {
        UHC.getInstance().getGameSave().registerEvent(EventType.CORE, "PvP actif à " + TimeUtils.getFormattedTime(UHC.getInstance().getUhcGame().getUhcData().getTimer()));

    }
}
