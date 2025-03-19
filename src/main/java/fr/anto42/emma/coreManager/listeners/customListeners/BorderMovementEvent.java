package fr.anto42.emma.coreManager.listeners.customListeners;

import fr.anto42.emma.UHC;
import fr.anto42.emma.utils.TimeUtils;
import fr.anto42.emma.utils.gameSaves.EventType;

public class BorderMovementEvent extends UHCEvent{
    public BorderMovementEvent() {
        UHC.getInstance().getGameSave().registerEvent(EventType.CORE, "La bordure se met en mouvement à " + TimeUtils.getFormattedTime(UHC.getInstance().getUhcGame().getUhcData().getTimer()));
    }
}
