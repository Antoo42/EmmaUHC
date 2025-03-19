package fr.anto42.emma.coreManager.listeners.customListeners;

import fr.anto42.emma.UHC;
import fr.anto42.emma.utils.gameSaves.EventType;
import org.bukkit.event.Cancellable;


public class TryStartEvent extends UHCEvent implements Cancellable {
    private boolean cancel = false;
    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean b) {
        cancel = b;
    }

    public TryStartEvent() {
        UHC.getInstance().getGameSave().registerEvent(EventType.CORE, "Tentative de démarrage de la partie");
    }
}
