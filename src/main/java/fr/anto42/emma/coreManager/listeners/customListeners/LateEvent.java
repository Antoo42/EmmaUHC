package fr.anto42.emma.coreManager.listeners.customListeners;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.coreManager.players.UHCPlayerStates;
import fr.anto42.emma.utils.TimeUtils;
import fr.anto42.emma.utils.gameSaves.EventType;

public class LateEvent extends UHCEvent {

    private final UHCPlayer uhcPlayer;

    public LateEvent(UHCPlayer uhcPlayer) {
        this.uhcPlayer = uhcPlayer;
        UHC.getInstance().getGameSave().registerEvent(EventType.CORE,uhcPlayer.getName() + "est ajouté à la partie");
        uhcPlayer.setPlayerState(UHCPlayerStates.ALIVE);
        UHC.getInstance().getUhcGame().getUhcData().getPreWhitelist().add(uhcPlayer.getName());
    }

    public UHCPlayer getUhcPlayer() {
        return uhcPlayer;
    }
}
