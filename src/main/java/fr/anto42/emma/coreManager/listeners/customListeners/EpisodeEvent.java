package fr.anto42.emma.coreManager.listeners.customListeners;

import fr.anto42.emma.UHC;
import fr.anto42.emma.utils.TimeUtils;
import fr.anto42.emma.utils.gameSaves.EventType;

public class EpisodeEvent extends UHCEvent {
    public EpisodeEvent() {
        UHC.getInstance().getGameSave().registerEvent(EventType.CORE, "Nouvel épisode");

    }
}
