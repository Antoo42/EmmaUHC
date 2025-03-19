package fr.anto42.emma.coreManager.listeners.customListeners;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.utils.TimeUtils;
import fr.anto42.emma.utils.gameSaves.EventType;

public class LateEvent extends UHCEvent {

    private final UHCPlayer uhcPlayer;

    public LateEvent(UHCPlayer uhcPlayer) {
        this.uhcPlayer = uhcPlayer;
        UHC.getInstance().getGameSave().registerEvent(EventType.CORE,uhcPlayer.getName() + "est ajouté à la partie à " + TimeUtils.getFormattedTime(UHC.getInstance().getUhcGame().getUhcData().getTimer()));
    }

    public UHCPlayer getUhcPlayer() {
        return uhcPlayer;
    }
}
