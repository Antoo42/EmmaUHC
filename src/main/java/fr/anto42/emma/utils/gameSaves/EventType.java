package fr.anto42.emma.utils.gameSaves;

public enum EventType {
    CORE("EmmaCore"), DEATHS("Morts"), ROLE("Role"), MODULE("Module"), ACHIVEMENT("Succès");


    private final String type;

    EventType(String type) {
        this.type = type;
    }

    public String getString() {
        return type;
    }

    public static EventType fromString(String name) {
        for (EventType eventType : EventType.values()) {
            if (eventType.name().equalsIgnoreCase(name)) {
                return eventType;
            }
        }
        throw new IllegalArgumentException("Aucun EventType trouvé pour : " + name);
    }
}
