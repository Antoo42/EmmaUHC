package fr.anto42.emma.utils.gameSaves;

public enum EventType {
    CORE("Core"), DEATHS("PvP"), ROLE("Rôle");


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
