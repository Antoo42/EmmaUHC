package fr.anto42.emma.coreManager.listeners.customListeners;

import fr.anto42.emma.coreManager.players.UHCPlayer;

public class ConfigSavedEvent extends UHCEvent{
    private final UHCPlayer host;

    public ConfigSavedEvent(UHCPlayer host) {
        this.host = host;
    }

    public UHCPlayer getHost() {
        return host;
    }
}
