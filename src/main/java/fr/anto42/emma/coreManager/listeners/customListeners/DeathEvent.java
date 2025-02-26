package fr.anto42.emma.coreManager.listeners.customListeners;

import fr.anto42.emma.coreManager.players.UHCPlayer;

public class DeathEvent extends UHCEvent{
    private final UHCPlayer victim;
    private UHCPlayer killer = null;

    public DeathEvent(UHCPlayer victim, UHCPlayer killer) {
        this.victim = victim;
        if (killer != null) {
            this.killer = killer;
        };
    }

    public UHCPlayer getVictim() {
        return victim;
    }

    public UHCPlayer getKiller() {
        return killer;
    }
}
