package fr.anto42.emma.coreManager.listeners.customListeners;

import fr.anto42.emma.coreManager.players.UHCPlayer;

public class WinEvent extends UHCEvent{
    private final UHCPlayer winner;
    public WinEvent(UHCPlayer winner) {
        this.winner = winner;
    }

    public UHCPlayer getWinner() {
        return winner;
    }
}
