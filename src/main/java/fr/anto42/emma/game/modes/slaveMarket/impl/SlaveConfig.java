package fr.anto42.emma.game.modes.slaveMarket.impl;

import fr.anto42.emma.coreManager.players.UHCPlayer;

import java.util.ArrayList;
import java.util.List;

public class SlaveConfig {
    private int startdiamond = 50;

    public int getStartdiamond() {
        return startdiamond;
    }

    public void setStartdiamond(int startdiamond) {
        this.startdiamond = startdiamond;
    }

    private final List<UHCPlayer> leadersList = new ArrayList<>();

    public List<UHCPlayer> getLeadersList() {
        return leadersList;
    }

    private int auctionDuration = 30;


    public int getAuctionDuration() {
        return auctionDuration;
    }

    public void setAuctionDuration(int auctionDuration) {
        this.auctionDuration = auctionDuration;
    }
}
