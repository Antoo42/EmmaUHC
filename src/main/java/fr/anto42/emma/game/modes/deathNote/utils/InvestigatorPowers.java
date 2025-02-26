package fr.anto42.emma.game.modes.deathNote.utils;

public enum InvestigatorPowers {
    INVESTIGATION(0, "Enquête", "Tu peux faire une enquête trop bien"),
    REAL_DEADS(1, "Voir les vraies morts", "OMG TU PEUX VOIR LES VRAIES MORTS"),
    DN_INFO(2, "DeathNote info", "tu sais quand le DN est use"),
    KIRA_CHAT(3, "KiraChat", "voir le chat des kiras");


    private final int id;
    private final String name;
    private final String desc;

    InvestigatorPowers(int id, String name, String desc) {
        this.id = id;
        this.name = name;
        this.desc = desc;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }
    public static InvestigatorPowers getById(int id) {
        for (InvestigatorPowers power : values()) {
            if (power.getId() == id) {
                return power;
            }
        }
        return null;
    }
}
