package fr.anto42.emma.coreManager.spec;

import java.util.UUID;

public class DamageEvent {
    private final UUID attacker;
    private final UUID victim;
    private final double damage;
    private final long timestamp;

    public DamageEvent(UUID attacker, UUID victim, double damage, long timestamp) {
        this.attacker = attacker;
        this.victim = victim;
        this.damage = damage;
        this.timestamp = timestamp;
    }

    // Getters
    public UUID getAttacker() { return attacker; }
    public UUID getVictim() { return victim; }
    public double getDamage() { return damage; }
    public long getTimestamp() { return timestamp; }
}
