package fr.anto42.emma.coreManager.spec;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.observation.AutoSpectatorSystem;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.game.GameState;
import fr.anto42.emma.utils.chat.InteractiveMessage;
import fr.anto42.emma.utils.chat.InteractiveMessageBuilder;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class FightDetectionSystem implements Listener {

    private final Map<UUID, FightSession> activeFights = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lastDamageTime = new ConcurrentHashMap<>();
    private final Map<UUID, Set<UUID>> fightParticipants = new ConcurrentHashMap<>();
    private final Map<UUID, Location> fightLocations = new ConcurrentHashMap<>();

    private final long FIGHT_TIMEOUT = 30000;
    private final long FIGHT_START_THRESHOLD = 3000;
    private final double FIGHT_RADIUS = 50.0;

    private final Set<FightEventListener> listeners = new HashSet<>();

    private final AutoSpectatorSystem autoSpectatorSystem;
    public FightDetectionSystem() {
        startFightMonitoring();
        this.autoSpectatorSystem = new AutoSpectatorSystem();
        this.addFightEventListener(autoSpectatorSystem);
    }

    public AutoSpectatorSystem getAutoSpectatorSystem() {
        return autoSpectatorSystem;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!UHC.getInstance().getUhcGame().getGameState().equals(GameState.PLAYING))
            return;
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }

        Player victim = (Player) event.getEntity();
        Player attacker = (Player) event.getDamager();

        /*if (UHC.getInstance().getSpecManager().isSpectator(victim) ||
                UHC.getInstance().getSpecManager().isSpectator(attacker)) {
            return;
        }*/

        processFightEvent(attacker, victim, event.getFinalDamage());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID playerUuid = player.getUniqueId();

        endPlayerFights(playerUuid, FightEndReason.DEATH);

        Player killer = player.getKiller();
        if (killer != null) {
            notifyFightEvent(FightEventType.KILL, killer, player, player.getLocation());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerUuid = event.getPlayer().getUniqueId();
        endPlayerFights(playerUuid, FightEndReason.DISCONNECT);
    }


    private void processFightEvent(Player attacker, Player victim, double damage) {
        UUID attackerUuid = attacker.getUniqueId();
        UUID victimUuid = victim.getUniqueId();
        long currentTime = System.currentTimeMillis();

        lastDamageTime.put(attackerUuid, currentTime);
        lastDamageTime.put(victimUuid, currentTime);

        FightSession fight = findOrCreateFight(attacker, victim);

        fight.addParticipant(attackerUuid);
        fight.addParticipant(victimUuid);
        fight.updateLastActivity(currentTime);
        fight.addDamageEvent(attackerUuid, victimUuid, damage);

        checkNearbyPlayers(fight, attacker.getLocation());

        if (fight.isNewFight()) {
            notifyFightEvent(FightEventType.FIGHT_START, attacker, victim, attacker.getLocation());
            fight.setNewFight(false);
        } else {
            notifyFightEvent(FightEventType.DAMAGE_DEALT, attacker, victim, attacker.getLocation());
        }

        if (fight.isIntenseFight()) {
            notifyFightEvent(FightEventType.INTENSE_FIGHT, attacker, victim, attacker.getLocation());
        }
    }

    private FightSession findOrCreateFight(Player attacker, Player victim) {
        UUID attackerUuid = attacker.getUniqueId();
        UUID victimUuid = victim.getUniqueId();

        FightSession existingFight = activeFights.values().stream()
                .filter(fight -> fight.getParticipants().contains(attackerUuid) ||
                        fight.getParticipants().contains(victimUuid))
                .findFirst()
                .orElse(null);

        if (existingFight != null) {
            return existingFight;
        }

        UUID fightId = UUID.randomUUID();
        FightSession newFight = new FightSession(fightId, attacker.getLocation());
        activeFights.put(fightId, newFight);

        return newFight;
    }

    private void checkNearbyPlayers(FightSession fight, Location center) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (UHC.getInstance().getSpecManager().isSpectator(player)) continue;

            if (player.getLocation().distance(center) <= FIGHT_RADIUS) {
                UUID playerUuid = player.getUniqueId();
                if (!fight.getParticipants().contains(playerUuid)) {
                    notifyFightEvent(FightEventType.PLAYER_NEAR_FIGHT, player, null, center);
                }
            }
        }
    }


    private void startFightMonitoring() {
        new BukkitRunnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                Iterator<Map.Entry<UUID, FightSession>> iterator = activeFights.entrySet().iterator();

                while (iterator.hasNext()) {
                    Map.Entry<UUID, FightSession> entry = iterator.next();
                    FightSession fight = entry.getValue();

                    if (currentTime - fight.getLastActivity() > FIGHT_TIMEOUT) {
                        notifyFightEvent(FightEventType.FIGHT_END, null, null, fight.getLocation());
                        iterator.remove();
                        continue;
                    }

                    fight.getParticipants().removeIf(uuid -> {
                        Player player = Bukkit.getPlayer(uuid);
                        return player == null || !player.isOnline();
                    });

                    if (fight.getParticipants().size() < 2) {
                        notifyFightEvent(FightEventType.FIGHT_END, null, null, fight.getLocation());
                        iterator.remove();
                    }
                }

                cleanupOldData(currentTime);
            }
        }.runTaskTimer(UHC.getInstance(), 0L, 20L);
    }

    private void cleanupOldData(long currentTime) {
        lastDamageTime.entrySet().removeIf(entry ->
                currentTime - entry.getValue() > FIGHT_TIMEOUT * 2);
    }


    private void endPlayerFights(UUID playerUuid, FightEndReason reason) {
        activeFights.values().forEach(fight -> {
            if (fight.getParticipants().contains(playerUuid)) {
                fight.getParticipants().remove(playerUuid);

                Player player = Bukkit.getPlayer(playerUuid);
                if (player != null) {
                    if (reason == FightEndReason.DEATH) {
                        notifyFightEvent(FightEventType.PLAYER_DIED, player, null, fight.getLocation());
                    } else if (reason == FightEndReason.DISCONNECT) {
                        notifyFightEvent(FightEventType.PLAYER_DISCONNECTED, player, null, fight.getLocation());
                    }
                }
            }
        });
    }

    // === NOTIFICATIONS ===

    private void notifyFightEvent(FightEventType type, Player player1, Player player2, Location location) {
        FightEvent event = new FightEvent(type, player1, player2, location, System.currentTimeMillis());

        for (FightEventListener listener : listeners) {
            listener.onFightEvent(event);
        }

        switch (type) {
            case FIGHT_START:
                broadcastToSpectators("§e⚔ §fCombat détecté entre §c" + player1.getName() +
                        " §fet §c" + player2.getName() + " §f!");
                createFightAlert(location, player1, player2);
                break;

            case INTENSE_FIGHT:
                broadcastToSpectators("§c🔥 §fCombat intense en cours !");
                break;

            case KILL:
                broadcastToSpectators("§4💀 §c" + player1.getName() + " §fa tué §c" + player2.getName() + " §f!");
                break;

            case FIGHT_END:
                break;
        }
    }

    private void createFightAlert(Location location, Player player1, Player player2) {
        World world = location.getWorld();

        InteractiveMessage fightAlert = new InteractiveMessage();

        fightAlert.add("§8§l» ");

        TextComponent teleportButton = new InteractiveMessageBuilder("§6§l[CLIQUEZ]")
                .setHoverMessage(
                        "§6§lTéléportation au combat\n",
                        "\n",
                        "§7Combat entre:\n",
                        "§c• " + player1.getName(),
                        "\n§c• " + player2.getName(),
                        "\n",
                        "\n§7Coordonnées:",
                        "\n§f• X: " + (int) location.getX(),
                        "\n§f• Y: " + (int) location.getY(),
                        "\n§f• Z: " + (int) location.getZ(),
                        "\n",
                        "\n§a➤ Cliquez pour vous téléporter !"
                )
                .setClickAction(ClickEvent.Action.RUN_COMMAND,
                        "/tp " + (int) location.getX() + " " + (int) location.getY() + " " + (int) location.getZ())
                .build();

        fightAlert.add(teleportButton);
        fightAlert.add(" §fpour vous téléporter au combat !");

        // Créer un message alternatif pour suivre un joueur spécifique
        InteractiveMessage followMessage = new InteractiveMessage();
        followMessage.add("§8§l» §7Suivre: ");

        // Bouton pour suivre le premier joueur
        TextComponent followPlayer1 = new InteractiveMessageBuilder("§c" + player1.getName())
                .setHoverMessage(
                        "\n§6§lSuivre " + player1.getName(),
                        "\n",
                        "\n§7Cliquez pour suivre ce joueur",
                        "\n§7automatiquement pendant le combat"
                )
                .setClickAction(ClickEvent.Action.RUN_COMMAND, "/spec follow " + player1.getName())
                .build();

        followMessage.add(followPlayer1);
        followMessage.add(" §7ou ");

        // Bouton pour suivre le deuxième joueur
        TextComponent followPlayer2 = new InteractiveMessageBuilder("§c" + player2.getName())
                .setHoverMessage(
                        "\n§6§lSuivre " + player2.getName(),
                        "\n",
                        "\n§7Cliquez pour suivre ce joueur",
                        "\n§7automatiquement pendant le combat"
                )
                .setClickAction(ClickEvent.Action.RUN_COMMAND, "/spec follow " + player2.getName())
                .build();

        followMessage.add(followPlayer2);

        // Envoyer les messages à tous les spectateurs
        for (UHCPlayer spec : UHC.getInstance().getSpecManager().getSpectators()) {
            Player spectator = spec.getBukkitPlayer();
            if (spectator != null && spectator.isOnline()) {
                fightAlert.sendMessage(spectator);
                followMessage.sendMessage(spectator);
            }
        }

        // Message supplémentaire avec informations détaillées
        InteractiveMessage detailsMessage = new InteractiveMessage();
        detailsMessage.add("§8§l» §7Infos: ");

        TextComponent detailsButton = new InteractiveMessageBuilder("§e[DÉTAILS]")
                .setHoverMessage(
                        "\n§6§lInformations du combat",
                        "\n",
                        "\n§7Joueurs impliqués:",
                        "\n§c• " + player1.getName() + " §7(" + (int) player1.getHealth() + "❤)",
                        "\n§c• " + player2.getName() + " §7(" + (int) player2.getHealth() + "❤)",
                        "\n",
                        "\n§7Distance entre les joueurs:",
                        "\n§f• " + String.format("%.1f", player1.getLocation().distance(player2.getLocation())) + " blocs",
                        "\n",
                        "\n§7Biome: §f" + location.getBlock().getBiome().toString(),
                        "\n",
                        "\n§a➤ Cliquez pour plus d'infos !"
                )
                .setClickAction(ClickEvent.Action.RUN_COMMAND, "/spec fightinfo " + player1.getName() + " " + player2.getName())
                .build();

        detailsMessage.add(detailsButton);

        // Envoyer le message de détails
        for (UHCPlayer spec : UHC.getInstance().getSpecManager().getSpectators()) {
            Player spectator = spec.getBukkitPlayer();
            if (spectator != null && spectator.isOnline()) {
                detailsMessage.sendMessage(spectator);
            }
        }
    }


    private void broadcastToSpectators(String message) {
        for (UHCPlayer spec : UHC.getInstance().getSpecManager().getSpectators()) {
            spec.sendModMessage(message);
        }
    }


    public void addFightEventListener(FightEventListener listener) {
        listeners.add(listener);
    }

    public void removeFightEventListener(FightEventListener listener) {
        listeners.remove(listener);
    }

    public Collection<FightSession> getActiveFights() {
        return new ArrayList<>(activeFights.values());
    }

    public boolean isPlayerInFight(UUID playerUuid) {
        return activeFights.values().stream()
                .anyMatch(fight -> fight.getParticipants().contains(playerUuid));
    }

    public FightSession getPlayerFight(UUID playerUuid) {
        return activeFights.values().stream()
                .filter(fight -> fight.getParticipants().contains(playerUuid))
                .findFirst()
                .orElse(null);
    }
}


enum FightEndReason {
    TIMEOUT, DEATH, DISCONNECT, MANUAL
}

