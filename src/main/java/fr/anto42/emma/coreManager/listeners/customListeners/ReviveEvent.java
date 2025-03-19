package fr.anto42.emma.coreManager.listeners.customListeners;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.utils.TimeUtils;
import fr.anto42.emma.utils.gameSaves.EventType;

public class ReviveEvent extends UHCEvent{
    private final UHCPlayer revived;

    public ReviveEvent(UHCPlayer revived) {
        this.revived = revived;
        UHC.getInstance().getGameSave().registerEvent(EventType.CORE, "Revive de" + revived.getName() + " à: " + TimeUtils.getFormattedTime(UHC.getInstance().getUhcGame().getUhcData().getTimer()));

    }

    public UHCPlayer getRevived() {
        return revived;
    }
}
