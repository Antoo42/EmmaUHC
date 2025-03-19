package fr.anto42.emma.utils.gameSaves;

public class Event {
    private final EventType eventType;
    private final String string;
    private final String date;
    private final String timer;

    public Event(EventType eventType, String string, String date, String timer) {
        this.eventType = eventType;
        this.string = string;
        this.date = date;
        this.timer = timer;
    }

    public EventType getEventType() {
        return eventType;
    }

    public String getString() {
        return string;
    }

    public String getDate() {
        return date;
    }

    public String getTimer() {
        return timer;
    }
}
