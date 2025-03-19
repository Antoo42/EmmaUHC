package fr.anto42.emma.game.modes.deathNote.impl;

import fr.anto42.emma.game.modes.deathNote.DeathNoteModule;

public class DeathNoteConfig {



    private int pointsForInvestigation = 3000;
    private int distanceDeathNote = 25;


    public int getDistanceDeathNote() {
        return distanceDeathNote;
    }

    public void setDistanceDeathNote(int distanceDeathNote) {
        this.distanceDeathNote = distanceDeathNote;
    }

    public int getPointsForInvestigation() {
        return pointsForInvestigation;
    }

    public void setPointsForInvestigation(int pointsForInvestigation) {
        this.pointsForInvestigation = pointsForInvestigation;
    }
}