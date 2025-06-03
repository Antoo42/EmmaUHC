package fr.anto42.emma.coreManager.spec;

import org.bukkit.Location;

import java.util.*;

public class FightSession {
    private final UUID fightId;
    private final Location location;
    private final Set<UUID> participants = new HashSet<>();
    private final List<DamageEvent> damageEvents = new ArrayList<>();
    private final long startTime;
    private long lastActivity;
    private boolean isNewFight = true;

    public FightSession(UUID fightId, Location location) {
        this.fightId = fightId;
        this.location = location.clone();
        this.startTime = System.currentTimeMillis();
        this.lastActivity = startTime;
    }

    public void addParticipant(UUID playerUuid) {
        participants.add(playerUuid);
    }

    public void addDamageEvent(UUID attacker, UUID victim, double damage) {
        damageEvents.add(new DamageEvent(attacker, victim, damage, System.currentTimeMillis()));
    }

    public boolean isIntenseFight() {
        long recentTime = System.currentTimeMillis() - 5000; // 5 dernières secondes
        double recentDamage = damageEvents.stream()
                .filter(event -> event.getTimestamp() > recentTime)
                .mapToDouble(DamageEvent::getDamage)
                .sum();
        return recentDamage > 20.0; // Plus de 20 dégâts en 5 secondes = intense
    }

    // Getters et setters
    public UUID getFightId() { return fightId; }
    public Location getLocation() { return location; }
    public Set<UUID> getParticipants() { return participants; }
    public long getLastActivity() { return lastActivity; }
    public void updateLastActivity(long time) { this.lastActivity = time; }
    public boolean isNewFight() { return isNewFight; }
    public void setNewFight(boolean newFight) { this.isNewFight = newFight; }
    public long getStartTime() { return startTime; }
    public List<DamageEvent> getDamageEvents() { return damageEvents; }
}
