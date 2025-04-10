package fr.anto42.emma.game.modes.slaveMarket.impl;

import fr.anto42.emma.coreManager.players.UHCPlayer;

import java.util.HashMap;
import java.util.Map;

public class SlaveData {
    private final Map<UHCPlayer, Integer> diamondsLeft = new HashMap<>();


    public int getDiamondsLeft(UHCPlayer uhcPlayer) {
        return diamondsLeft.get(uhcPlayer);
    }

    public Map<UHCPlayer, Integer> getDiamondsLeft() {
        return diamondsLeft;
    }
}
