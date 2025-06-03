package fr.anto42.emma.coreManager.spec;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FightEvent {
    private final FightEventType type;
    private final Player player1;
    private final Player player2;
    private final Location location;
    private final long timestamp;

    public FightEvent(FightEventType type, Player player1, Player player2, Location location, long timestamp) {
        this.type = type;
        this.player1 = player1;
        this.player2 = player2;
        this.location = location;
        this.timestamp = timestamp;
    }

    public FightEventType getType() { return type; }
    public Player getPlayer1() { return player1; }
    public Player getPlayer2() { return player2; }
    public Location getLocation() { return location; }
    public long getTimestamp() { return timestamp; }
}
