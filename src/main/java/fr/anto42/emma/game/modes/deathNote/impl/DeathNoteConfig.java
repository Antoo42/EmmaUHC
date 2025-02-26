package fr.anto42.emma.game.modes.deathNote.impl;

import fr.anto42.emma.game.modes.deathNote.DeathNoteModule;

public class DeathNoteConfig {
    private final DeathNoteModule module;

    public DeathNoteConfig(DeathNoteModule module) {
        this.module = module;
    }


    private int episodeTime = 20;

    public int getEpisodeTime() {
        return episodeTime;
    }

    public void setEpisodeTime(int episodeTime) {
        this.episodeTime = episodeTime;
    }

    private int pointsForInvestigation = 300;
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