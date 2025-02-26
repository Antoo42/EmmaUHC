package fr.anto42.emma.game.modes.deathNote.utils;

public enum MeloType {
    KIND(0, "Gentil", InvestigationResultType.KIND), JEALOUS(1, "Jaloux", InvestigationResultType.SUSPECT), EVIL(2, "Méchant", InvestigationResultType.SUSPECT);

    private final int id;
    private final String typeName;
    private final InvestigationResultType investigationResultType;


    MeloType(int id, String typeName, InvestigationResultType investigationResultType) {
        this.id = id;
        this.typeName = typeName;
        this.investigationResultType = investigationResultType;
    }

    public String getTypeName() {
        return typeName;
    }

    public int getId() {
        return id;
    }

    public InvestigationResultType getInvestigationResultType() {
        return investigationResultType;
    }

    public static MeloType getById(int id) {
        for (MeloType power : values()) {
            if (power.getId() == id) {
                return power;
            }
        }
        return null;
    }
}
