package fr.anto42.emma.game.modes.deathNote.utils;

public enum InvestigationResultType {
    KIND("Innoncent"), SUSPECT("Suspect");


    private final String name;

    InvestigationResultType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
