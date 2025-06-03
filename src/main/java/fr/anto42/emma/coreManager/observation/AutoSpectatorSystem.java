package fr.anto42.emma.coreManager.observation;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.coreManager.spec.FightEvent;
import fr.anto42.emma.coreManager.spec.FightEventListener;
import fr.anto42.emma.coreManager.spec.FightSession;
import fr.anto42.emma.game.GameState;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static fr.anto42.emma.coreManager.spec.FightEventType.*;

public class AutoSpectatorSystem implements FightEventListener {

    private final Map<UUID, CameraMode> spectatorModes = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> spectatorTasks = new ConcurrentHashMap<>();
    private final Map<UUID, BukkitRunnable> cinematicTasks = new ConcurrentHashMap<>();
    private final Map<UUID, BukkitRunnable> followTasks = new ConcurrentHashMap<>();
    private final Map<UUID, Player> currentTargets = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lastActionTime = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lastTargetSwitch = new ConcurrentHashMap<>();
    private final Random random = new Random();

    // Configuration avancée
    private final int NORMAL_SWITCH_INTERVAL = 15; // secondes entre changements de cible
    private final int CUTSCENE_TRIGGER_DELAY = 45; // secondes d'inactivité avant cutscene
    private final double CAMERA_DISTANCE = 6.0;
    private final double FOLLOW_SMOOTHNESS = 0.2; // Plus fluide
    private final double HEIGHT_OFFSET = 2.5;
    private final double MAX_HEIGHT = 150; // Limite de hauteur pour éviter y=800
    private final double MAX_DISTANCE_FROM_CENTER = 1000.0; // Limite de distance du centre

    // Debug system
    private boolean debugEnabled = false;
    private final String DEBUG_PREFIX = "§b§lAUTO-SPEC §8§l» §7";
    private final Set<UUID> debugPlayers = ConcurrentHashMap.newKeySet();

    public enum CameraMode {
        THIRD_PERSON_FOLLOW, CINEMATIC_ORBIT, STATIC_OVERVIEW,
        CUTSCENE_TRAVELING, PLAYER_ROTATION, OVERVIEW_SWEEP,
        DRONE_SHOT, TRACKING_SHOT, CLOSE_COMBAT, CUTSCENE_CAVERN_FLIGHT, CUTSCENE_AERIAL_FLIGHT

    }

    // === SYSTÈME DE DEBUG ===

    public void toggleDebug(Player player) {
        if (debugPlayers.contains(player.getUniqueId())) {
            debugPlayers.remove(player.getUniqueId());
            player.sendMessage(DEBUG_PREFIX + "§cDebug désactivé");
        } else {
            debugPlayers.add(player.getUniqueId());
            player.sendMessage(DEBUG_PREFIX + "§aDebug activé");
        }
    }

    private void sendDebugMessage(String message) {
        if (!debugEnabled) return;
        UHC.getInstance().getLogger().info("[AUTO-SPEC] " + ChatColor.stripColor(message));
        Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.hasPermission("emma.debug") || debugPlayers.contains(p.getUniqueId()))
                .forEach(p -> p.sendMessage(DEBUG_PREFIX + message));
    }

    // === MÉTHODES UTILITAIRES ===

    private Location limitLocationToWorldBounds(Location location) {
        double x = location.getX();
        double z = location.getZ();

        // Limiter X entre -1000 et 1000
        if (x > MAX_DISTANCE_FROM_CENTER) {
            x = MAX_DISTANCE_FROM_CENTER;
        } else if (x < -MAX_DISTANCE_FROM_CENTER) {
            x = -MAX_DISTANCE_FROM_CENTER;
        }

        // Limiter Z entre -1000 et 1000
        if (z > MAX_DISTANCE_FROM_CENTER) {
            z = MAX_DISTANCE_FROM_CENTER;
        } else if (z < -MAX_DISTANCE_FROM_CENTER) {
            z = -MAX_DISTANCE_FROM_CENTER;
        }

        location.setX(x);
        location.setZ(z);
        return location;
    }

    private boolean isValidCameraPosition(Location cameraPos, Location targetPos) {
        if (cameraPos == null || targetPos == null) return false;

        // Vérifier que la caméra n'est pas trop loin du target
        double distance = cameraPos.distance(targetPos);
        if (distance > 50) return false;

        // Vérifier que la hauteur est raisonnable
        if (Math.abs(cameraPos.getY() - targetPos.getY()) > 30) return false;

        // Vérifier que les coordonnées sont dans les limites du monde
        if (Math.abs(cameraPos.getX()) > MAX_DISTANCE_FROM_CENTER ||
                Math.abs(cameraPos.getZ()) > MAX_DISTANCE_FROM_CENTER) return false;

        return true;
    }

    // === GESTION PRINCIPALE ===

    public void enableForPlayer(Player spectator) {
        UUID uuid = spectator.getUniqueId();
        sendDebugMessage("§aActivation système cinématique pour §e" + spectator.getName());

        disableForPlayer(spectator);

        // Configuration spectateur optimisée
        spectator.setGameMode(GameMode.SPECTATOR);
        spectator.setAllowFlight(true);
        spectator.setFlying(true);
        spectator.setFlySpeed(1.0f);

        CameraMode initialMode = CameraMode.THIRD_PERSON_FOLLOW;
        spectatorModes.put(uuid, initialMode);
        lastActionTime.put(uuid, System.currentTimeMillis());
        lastTargetSwitch.put(uuid, System.currentTimeMillis());

        sendDebugMessage("§7Mode initial: §b" + initialMode);

        startAdvancedSpectatorSystem(spectator);
        spectator.sendMessage("§8§l» §aMode cinématique activé !");
        spectator.sendMessage("§8§l» §7Système de réalisation automatique démarré");
    }

    private void startAdvancedSpectatorSystem(Player spectator) {
        sendDebugMessage("§7Démarrage système avancé pour §e" + spectator.getName());

        int taskId = new BukkitRunnable() {
            @Override
            public void run() {
                if (!spectator.isOnline()) {
                    sendDebugMessage("§c" + spectator.getName() + " déconnecté");
                    this.cancel();
                    return;
                }

                UHCPlayer uhcPlayer = UHC.getUHCPlayer(spectator);
                if (uhcPlayer == null || !UHC.getInstance().getSpecManager().isSpectator(spectator)) {
                    sendDebugMessage("§c" + spectator.getName() + " n'est plus spectateur");
                    this.cancel();
                    return;
                }

                analyzeAndSwitchCamera(spectator);
            }
        }.runTaskTimer(UHC.getInstance(), 0, 20L).getTaskId(); // 20 FPS

        spectatorTasks.put(spectator.getUniqueId(), taskId);
    }

    private void analyzeAndSwitchCamera(Player spectator) {
        UUID uuid = spectator.getUniqueId();
        long currentTime = System.currentTimeMillis();
        long timeSinceLastAction = currentTime - lastActionTime.getOrDefault(uuid, currentTime);
        long timeSinceLastSwitch = currentTime - lastTargetSwitch.getOrDefault(uuid, currentTime);

        sendDebugMessage("§7Analyse caméra pour §e" + spectator.getName() +
                " §7(inactif: §b" + (timeSinceLastAction / 1000) + "s§7, switch: §b" + (timeSinceLastSwitch / 1000) + "s§7)");

        // Vérifier s'il y a des combats actifs
        boolean hasFights = !UHC.getInstance().getSpecManager().getFightDetectionSystem().getActiveFights().isEmpty();

        if (hasFights) {
            sendDebugMessage("§6Combat détecté, focus combat");
            focusOnFight(spectator);
            return;
        }

        if (UHC.getInstance().getUhcGame().getGameState() != GameState.PLAYING) {
            if (timeSinceLastSwitch > 20000) {
                startCutsceneMode(spectator);
                return;
            }
        }

        // Si pas d'action depuis très longtemps ET pas de switch récent, cutscene
        if (timeSinceLastAction > CUTSCENE_TRIGGER_DELAY * 1000 && timeSinceLastSwitch > 20000) {
            sendDebugMessage("§dDéclenchement cutscene (inactif depuis " + (timeSinceLastAction / 1000) + "s)");
            startCutsceneMode(spectator);
            return;
        }

        // Mode normal : rotation entre joueurs toutes les 15 secondes
        if (timeSinceLastSwitch > NORMAL_SWITCH_INTERVAL * 1000) {
            List<Player> availablePlayers = getSpectatablePlayers();
            if (!availablePlayers.isEmpty()) {
                Player target = selectNextTarget(availablePlayers, spectator);
                if (target != null) {
                    sendDebugMessage("§7Changement de cible: §e" + target.getName());
                    switchToTarget(spectator, target);
                    lastTargetSwitch.put(uuid, currentTime);
                }
            }
        }
    }

    private List<Player> getSpectatablePlayers() {
        List<Player> players = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            UHCPlayer uhcPlayer = UHC.getUHCPlayer(p);
            if (uhcPlayer != null && !UHC.getInstance().getSpecManager().isSpectator(p) && uhcPlayer.isAlive()) {
                players.add(p);
            }
        }
        return players;
    }

    private Player selectNextTarget(List<Player> players, Player spectator) {
        if (players.isEmpty()) return null;

        // Éviter de reprendre la même cible
        Player currentTarget = currentTargets.get(spectator.getUniqueId());
        List<Player> otherPlayers = new ArrayList<>(players);
        if (currentTarget != null) {
            otherPlayers.remove(currentTarget);
        }

        if (otherPlayers.isEmpty()) {
            return players.get(random.nextInt(players.size()));
        }

        // Priorité aux joueurs avec activité intéressante
        return otherPlayers.stream()
                .max(Comparator.comparingDouble(this::calculatePlayerInterest))
                .orElse(otherPlayers.get(random.nextInt(otherPlayers.size())));
    }

    private double calculatePlayerInterest(Player player) {
        UHCPlayer uhcPlayer = UHC.getUHCPlayer(player);
        double interest = random.nextDouble() * 5; // Base aléatoire

        // Plus de points si vie basse (tension)
        interest += (20 - player.getHealth()) * 1.5;

        // Plus de points si beaucoup de ressources
        interest += uhcPlayer.getDiamondMined() * 3;
        interest += uhcPlayer.getGoldMined();

        // Plus de points si en mouvement
        if (player.getVelocity().lengthSquared() > 0.05) {
            interest += 8;
        }

        // Plus de points si underground (plus intéressant)
        if (player.getLocation().getY() < 40) {
            interest += 10;
        }

        return interest;
    }

    private void switchToTarget(Player spectator, Player target) {
        UUID uuid = spectator.getUniqueId();
        currentTargets.put(uuid, target);

        // Modes normaux avec les nouveaux
        CameraMode[] availableModes = {
                CameraMode.THIRD_PERSON_FOLLOW,
                CameraMode.CINEMATIC_ORBIT,
                CameraMode.PLAYER_ROTATION,
                CameraMode.DRONE_SHOT,
                CameraMode.TRACKING_SHOT
        };

        CameraMode mode = availableModes[random.nextInt(availableModes.length)];
        spectatorModes.put(uuid, mode);

        switch (mode) {
            case THIRD_PERSON_FOLLOW:
                startThirdPersonFollow(spectator, target);
                break;
            case CINEMATIC_ORBIT:
                startCinematicOrbit(spectator, target);
                break;
            case PLAYER_ROTATION:
                startPlayerRotation(spectator, target);
                break;
            case DRONE_SHOT:
                startDroneShot(spectator, target);
                break;
            case TRACKING_SHOT:
                startTrackingShot(spectator, target);
                break;
            case CLOSE_COMBAT:
                startCloseCombat(spectator, target);
                break;
        }
    }

    // === MODES DE CAMÉRA ===

    private void startThirdPersonFollow(Player spectator, Player target) {
        sendDebugMessage("§7Démarrage suivi 3ème personne sur §e" + target.getName());

        stopAllTasks(spectator);

        BukkitRunnable followTask = new BukkitRunnable() {
            private Vector lastTargetPos = target.getLocation().toVector();
            private Vector cameraVelocity = new Vector(0, 0, 0);
            private double currentDistance = 2.5; // Distance réduite de 6.0 à 2.5
            private Location lastValidCameraPos = spectator.getLocation();

            @Override
            public void run() {
                try {
                    // Vérifications de sécurité
                    if (!spectator.isOnline() || !target.isOnline()) {
                        sendDebugMessage("§cArrêt suivi: joueur déconnecté");
                        this.cancel();
                        return;
                    }

                    // Vérifier que les joueurs sont dans le même monde
                    if (!spectator.getWorld().equals(target.getWorld())) {
                        sendDebugMessage("§cArrêt suivi: mondes différents");
                        this.cancel();
                        return;
                    }

                    Vector targetPos = target.getLocation().toVector();
                    Vector targetMovement = targetPos.clone().subtract(lastTargetPos);

                    // Prédire la position future du joueur (réduit pour plus de stabilité)
                    Vector predictedPos = targetPos.clone().add(targetMovement.multiply(1.0)); // Réduit de 1.5 à 1.0

                    // Calculer la position idéale de la caméra (derrière le joueur)
                    Vector targetDirection = target.getLocation().getDirection();

                    // Protection contre les directions nulles
                    if (targetDirection.length() < 0.1) {
                        targetDirection = new Vector(0, 0, 1); // Direction par défaut
                    }

                    Vector idealCameraPos = predictedPos.clone()
                            .subtract(targetDirection.normalize().multiply(currentDistance))
                            .add(new Vector(0, 1.2, 0)); // Hauteur réduite de HEIGHT_OFFSET (2.5) à 1.2

                    // CORRECTION: Limiter la hauteur pour éviter y=800
                    double targetY = targetPos.getY();
                    if (idealCameraPos.getY() > MAX_HEIGHT) {
                        idealCameraPos.setY(MAX_HEIGHT);
                    }
                    if (idealCameraPos.getY() < targetY - 5) { // Réduit de -10 à -5
                        idealCameraPos.setY(targetY + 1.2);
                    }
                    if (idealCameraPos.getY() > targetY + 15) { // Réduit de +50 à +15
                        idealCameraPos.setY(targetY + 1.2);
                    }

                    // Ajuster la distance selon la vitesse du joueur (distances plus proches)
                    double speed = targetMovement.length();
                    if (speed > 0.2) {
                        currentDistance = Math.min(2.5 + 1.5, 5.0); // Max 5 blocs au lieu de 12
                    } else {
                        currentDistance = Math.max(2.0, currentDistance - 0.05); // Min 2 blocs au lieu de CAMERA_DISTANCE
                    }

                    // Mouvement fluide de la caméra avec inertie
                    Vector currentCameraPos = spectator.getLocation().toVector();
                    Vector desiredMovement = idealCameraPos.subtract(currentCameraPos);

                    // Limiter la distance de déplacement par tick (réduit pour plus de fluidité)
                    double maxDistancePerTick = 2.0; // Réduit de 3.0 à 2.0
                    if (desiredMovement.length() > maxDistancePerTick) {
                        desiredMovement.normalize().multiply(maxDistancePerTick);
                    }

                    // Appliquer de l'inertie pour un mouvement plus naturel (plus réactif)
                    cameraVelocity.multiply(0.85).add(desiredMovement.multiply(FOLLOW_SMOOTHNESS * 1.5)); // 1.5x plus réactif

                    // Limiter la vélocité pour éviter les mouvements trop rapides (réduit)
                    if (cameraVelocity.length() > 1.5) { // Réduit de 2.0 à 1.5
                        cameraVelocity.normalize().multiply(1.5);
                    }

                    Vector newCameraPos = currentCameraPos.add(cameraVelocity);

                    // Créer la nouvelle position avec rotation vers le joueur
                    Location newLoc = newCameraPos.toLocation(target.getWorld());

                    // Sécurité: Limiter aux bornes du monde
                    newLoc = limitLocationToWorldBounds(newLoc);

                    // Calculer la direction de regard avec protection (légèrement au-dessus du joueur)
                    Vector lookTarget = targetPos.clone().add(new Vector(0, 0.5, 0)); // Regarder légèrement au-dessus
                    Vector lookDirection = lookTarget.subtract(newCameraPos);
                    if (lookDirection.length() > 0.1) {
                        lookDirection.normalize();
                    } else {
                        // Si trop proche, utiliser la dernière direction valide
                        lookDirection = lastValidCameraPos.getDirection();
                    }
                    newLoc.setDirection(lookDirection);

                    // TÉLÉPORTER LE SPECTATEUR
                    spectator.teleport(newLoc);
                    lastValidCameraPos = newLoc.clone();

                    lastTargetPos = targetPos;

                    // Debug périodique avec plus d'informations
                    if (random.nextInt(200) == 0) {
                        sendDebugMessage("§7Suivi 3P proche: distance=§b" + String.format("%.1f", currentDistance) +
                                "§7, hauteur=§b" + String.format("%.1f", newLoc.getY()) +
                                "§7, vitesse=§b" + String.format("%.2f", speed) +
                                "§7, pos=§b" + (int)newLoc.getX() + "," + (int)newLoc.getZ());
                    }

                } catch (Exception e) {
                    sendDebugMessage("§cErreur dans suivi 3P: " + e.getMessage());
                    UHC.getInstance().getLogger().warning("Erreur dans startThirdPersonFollow: " + e.getMessage());
                    e.printStackTrace();
                    this.cancel();
                }
            }
        };

        followTask.runTaskTimer(UHC.getInstance(), 0, 1L); // 20 FPS
        followTasks.put(spectator.getUniqueId(), followTask);

        spectator.sendMessage("§8§l» §7Mode: §eThird Person Proche §7→ §6" + target.getName());
    }


    private void startPlayerRotation(Player spectator, Player target) {
        sendDebugMessage("§7Démarrage rotation autour de §e" + target.getName());

        stopAllTasks(spectator);

        BukkitRunnable rotationTask = new BukkitRunnable() {
            double angle = random.nextDouble() * Math.PI * 2;
            int ticks = 0;
            int maxTicks = 300; // 15 secondes

            @Override
            public void run() {
                if (!spectator.isOnline() || !target.isOnline() || ticks > maxTicks) {
                    sendDebugMessage("§cArrêt rotation: " + (ticks > maxTicks ? "durée écoulée" : "joueur déconnecté"));
                    this.cancel();
                    return;
                }

                angle += 0.02; // Rotation lente
                ticks++;

                Vector offset = new Vector(
                        Math.cos(angle) * CAMERA_DISTANCE,
                        HEIGHT_OFFSET,
                        Math.sin(angle) * CAMERA_DISTANCE
                );

                Location targetLoc = target.getLocation();
                Location cameraLoc = targetLoc.clone().add(offset);

                // Limiter la hauteur
                if (cameraLoc.getY() > MAX_HEIGHT) {
                    cameraLoc.setY(MAX_HEIGHT);
                }

                // Limiter aux bornes du monde
                cameraLoc = limitLocationToWorldBounds(cameraLoc);

                Vector direction = targetLoc.toVector().subtract(cameraLoc.toVector()).normalize();
                cameraLoc.setDirection(direction);

                spectator.teleport(cameraLoc);
            }
        };

        rotationTask.runTaskTimer(UHC.getInstance(), 0, 1L); // 20 FPS
        cinematicTasks.put(spectator.getUniqueId(), rotationTask);

        spectator.sendMessage("§8§l» §7Mode: §eRotation §7→ §6" + target.getName());
    }

    private void startCinematicOrbit(Player spectator, Player target) {
        sendDebugMessage("§7Démarrage orbite cinématique sur §e" + target.getName());

        stopAllTasks(spectator);

        BukkitRunnable orbitTask = new BukkitRunnable() {
            double angle = random.nextDouble() * Math.PI * 2;
            double heightPhase = 0;
            int ticks = 0;
            int maxTicks = 200; // 10 secondes

            @Override
            public void run() {
                if (!spectator.isOnline() || !target.isOnline() || ticks > maxTicks) {
                    sendDebugMessage("§cArrêt orbite: " + (ticks > maxTicks ? "durée écoulée" : "joueur déconnecté"));
                    this.cancel();
                    return;
                }

                angle += 0.03; // Rotation plus rapide
                heightPhase += 0.02;
                ticks++;

                Vector offset = new Vector(
                        Math.cos(angle) * (CAMERA_DISTANCE + 1),
                        HEIGHT_OFFSET + Math.sin(heightPhase) * 1,
                        Math.sin(angle) * (CAMERA_DISTANCE + 1)
                );

                Location targetLoc = target.getLocation();
                Location cameraLoc = targetLoc.clone().add(offset);

                // Limiter la hauteur
                if (cameraLoc.getY() > MAX_HEIGHT) {
                    cameraLoc.setY(MAX_HEIGHT);
                }

                // Limiter aux bornes du monde
                cameraLoc = limitLocationToWorldBounds(cameraLoc);

                Vector direction = targetLoc.toVector().subtract(cameraLoc.toVector()).normalize();
                cameraLoc.setDirection(direction);

                spectator.teleport(cameraLoc);
            }
        };

        orbitTask.runTaskTimer(UHC.getInstance(), 0, 1L); // 20 FPS
        cinematicTasks.put(spectator.getUniqueId(), orbitTask);

        spectator.sendMessage("§8§l» §7Mode: §eOrbite cinématique §7→ §6" + target.getName());
    }

    // === NOUVEAUX MODES DE CAMÉRA ===

    private void startDroneShot(Player spectator, Player target) {
        sendDebugMessage("§7Démarrage drone shot sur §e" + target.getName());

        stopAllTasks(spectator);

        BukkitRunnable droneTask = new BukkitRunnable() {
            double angle = 0;
            double height = 15;
            int ticks = 0;
            int maxTicks = 400;

            @Override
            public void run() {
                if (!spectator.isOnline() || !target.isOnline() || ticks > maxTicks) {
                    this.cancel();
                    return;
                }

                angle += 0.01; // Rotation très lente
                height += Math.sin(ticks * 0.02) * 0.3; // Variation de hauteur
                ticks++;

                Vector offset = new Vector(
                        Math.cos(angle) * 20,
                        height,
                        Math.sin(angle) * 20
                );

                Location targetLoc = target.getLocation();
                Location cameraLoc = targetLoc.clone().add(offset);

                if (cameraLoc.getY() > MAX_HEIGHT) cameraLoc.setY(MAX_HEIGHT);
                cameraLoc = limitLocationToWorldBounds(cameraLoc);

                // Regarder vers le joueur avec un angle légèrement incliné
                Vector direction = targetLoc.toVector().subtract(cameraLoc.toVector()).normalize();
                cameraLoc.setDirection(direction);

                spectator.teleport(cameraLoc);
            }
        };

        droneTask.runTaskTimer(UHC.getInstance(), 0, 1L);
        cinematicTasks.put(spectator.getUniqueId(), droneTask);

        spectator.sendMessage("§8§l» §7Mode: §eDrone Shot §7→ §6" + target.getName());
    }

    private void startTrackingShot(Player spectator, Player target) {
        sendDebugMessage("§7Démarrage tracking shot sur §e" + target.getName());

        stopAllTasks(spectator);

        BukkitRunnable trackingTask = new BukkitRunnable() {
            private Vector lastTargetPos = target.getLocation().toVector();
            private Vector cameraOffset = new Vector(8, 3, 0); // Décalage latéral

            @Override
            public void run() {
                if (!spectator.isOnline() || !target.isOnline()) {
                    this.cancel();
                    return;
                }

                Vector targetPos = target.getLocation().toVector();
                Vector targetMovement = targetPos.clone().subtract(lastTargetPos);

                // Adapter l'offset selon la direction du mouvement
                if (targetMovement.length() > 0.1) {
                    Vector perpendicular = targetMovement.clone().normalize().crossProduct(new Vector(0, 1, 0));
                    cameraOffset = perpendicular.multiply(8).add(new Vector(0, 3, 0));
                }

                Vector cameraPos = targetPos.clone().add(cameraOffset);
                Location cameraLoc = cameraPos.toLocation(target.getWorld());

                if (cameraLoc.getY() > MAX_HEIGHT) cameraLoc.setY(MAX_HEIGHT);
                cameraLoc = limitLocationToWorldBounds(cameraLoc);

                // Regarder vers le joueur
                Vector direction = targetPos.subtract(cameraPos).normalize();
                cameraLoc.setDirection(direction);

                spectator.teleport(cameraLoc);
                lastTargetPos = targetPos;
            }
        };

        trackingTask.runTaskTimer(UHC.getInstance(), 0, 1L);
        followTasks.put(spectator.getUniqueId(), trackingTask);

        spectator.sendMessage("§8§l» §7Mode: §eTracking Shot §7→ §6" + target.getName());
    }

    private void startCloseCombat(Player spectator, Player target) {
        sendDebugMessage("§7Démarrage close combat sur §e" + target.getName());

        stopAllTasks(spectator);

        BukkitRunnable combatTask = new BukkitRunnable() {
            private Vector lastTargetPos = target.getLocation().toVector();
            private double intensity = 1.0;

            @Override
            public void run() {
                if (!spectator.isOnline() || !target.isOnline()) {
                    this.cancel();
                    return;
                }

                Vector targetPos = target.getLocation().toVector();
                Vector targetMovement = targetPos.clone().subtract(lastTargetPos);

                // IGNORER les déplacements verticaux
                targetMovement.setY(0);

                // Plus de mouvement = caméra plus dynamique
                double speed = targetMovement.length();

                // Position de caméra très proche et dynamique
                double distance = 3;
                Vector targetDirection = target.getLocation().getDirection();

                // Ignorer la composante verticale de la direction du joueur
                targetDirection.setY(0);
                if (targetDirection.length() < 0.1) {
                    targetDirection = new Vector(0, 0, 1); // Direction par défaut
                }
                targetDirection.normalize();

                // Calculer la position de base (derrière le joueur)
                Vector cameraPos = targetPos.clone()
                        .subtract(targetDirection.multiply(distance));

                // DÉCALAGE SUR LA DROITE : Calculer le vecteur perpendiculaire vers la droite
                Vector rightVector = targetDirection.clone().crossProduct(new Vector(0, 1, 0)).normalize();
                cameraPos.add(rightVector.multiply(1.5)); // Décalage de 1.5 blocs vers la droite

                // HAUTEUR : Ajouter une hauteur fixe
                cameraPos.setY(targetPos.getY() + 2.0); // 2 blocs au-dessus du joueur

                Location cameraLoc = cameraPos.toLocation(target.getWorld());

                if (cameraLoc.getY() > MAX_HEIGHT) cameraLoc.setY(MAX_HEIGHT);
                cameraLoc = limitLocationToWorldBounds(cameraLoc);

                // Prédire où regarder selon le mouvement (seulement horizontal)
                Vector lookTarget = targetPos.clone();
                if (speed > 0.1) {
                    Vector horizontalMovement = targetMovement.clone();
                    horizontalMovement.setY(0); // Garder seulement le mouvement horizontal
                    lookTarget.add(horizontalMovement.multiply(3)); // Réduit de 5 à 3 pour plus de stabilité
                }

                // Regarder légèrement au-dessus du joueur pour un meilleur angle
                lookTarget.setY(targetPos.getY() + 1.0);

                Vector direction = lookTarget.subtract(cameraPos).normalize();
                cameraLoc.setDirection(direction);

                spectator.teleport(cameraLoc);

                // Mettre à jour la position précédente (en gardant Y pour éviter les sauts)
                lastTargetPos = targetPos.clone();
            }
        };

        combatTask.runTaskTimer(UHC.getInstance(), 0, 1L);
        followTasks.put(spectator.getUniqueId(), combatTask);

        spectator.sendMessage("§8§l» §7Mode: §eClose Combat §7→ §6" + target.getName());
    }

    // === CUTSCENES ===

    private void startCutsceneMode(Player spectator) {
        sendDebugMessage("§dDémarrage mode cutscene");

        stopAllTasks(spectator);

        // Choisir parmi tous les types de cutscenes
        CameraMode[] cutsceneModes = {
                CameraMode.CUTSCENE_TRAVELING,
                CameraMode.OVERVIEW_SWEEP,
                CameraMode.CUTSCENE_CAVERN_FLIGHT,
                CameraMode.CUTSCENE_AERIAL_FLIGHT
        };

        CameraMode cutsceneMode = cutsceneModes[random.nextInt(cutsceneModes.length)];
        spectatorModes.put(spectator.getUniqueId(), cutsceneMode);

        switch (cutsceneMode) {
            case CUTSCENE_TRAVELING:
                startTravelingCutscene(spectator);
                break;
            case OVERVIEW_SWEEP:
                startOverviewSweep(spectator);
                break;
            case CUTSCENE_CAVERN_FLIGHT:
                startCavernStraightFlight(spectator);
                break;
            case CUTSCENE_AERIAL_FLIGHT:
                startAerialStraightFlight(spectator);
                break;
        }

        lastActionTime.put(spectator.getUniqueId(), System.currentTimeMillis());
        lastTargetSwitch.put(spectator.getUniqueId(), System.currentTimeMillis());
    }

    private void startCavernStraightFlight(Player spectator) {
        sendDebugMessage("§dCutscene caverne - Vol en ligne droite");

        stopAllTasks(spectator);

        World world = spectator.getWorld();

        // Points de départ et d'arrivée souterrains (Y entre 10 et 40)
        Location startLoc = new Location(world,
                random.nextInt(1600) - 800, // -800 à +800
                15 + random.nextInt(25),     // Y entre 15 et 40
                random.nextInt(1600) - 800); // -800 à +800

        Location endLoc = new Location(world,
                random.nextInt(1600) - 800,
                10 + random.nextInt(30),     // Y entre 10 et 40
                random.nextInt(1600) - 800);

        // Sécurité : Limiter les positions
        startLoc = limitLocationToWorldBounds(startLoc);
        endLoc = limitLocationToWorldBounds(endLoc);

        Location finalStartLoc = startLoc;
        Location finalEndLoc = endLoc;
        BukkitRunnable cavernTask = new BukkitRunnable() {
            int ticks = 0;
            int maxTicks = 600; // 30 secondes
            Vector startPos = finalStartLoc.toVector();
            Vector endPos = finalEndLoc.toVector();
            Vector direction = endPos.clone().subtract(startPos).normalize();

            @Override
            public void run() {
                if (!spectator.isOnline() || ticks > maxTicks) {
                    this.cancel();
                    return;
                }

                double progress = (double) ticks / maxTicks;

                // Mouvement en ligne droite avec légères variations
                Vector currentPos = startPos.clone().add(direction.clone().multiply(progress * startPos.distance(endPos)));

                // Ajouter de légères oscillations latérales pour simuler la navigation dans les cavernes
                double sideOffset = Math.sin(progress * Math.PI * 8) * 2; // Oscillation de ±2 blocs
                Vector perpendicular = direction.clone().crossProduct(new Vector(0, 1, 0)).normalize();
                currentPos.add(perpendicular.multiply(sideOffset));

                // Légère variation verticale pour éviter les obstacles
                double verticalOffset = Math.sin(progress * Math.PI * 6) * 1.5; // ±1.5 blocs
                currentPos.setY(currentPos.getY() + verticalOffset);

                Location newLoc = currentPos.toLocation(world);
                newLoc = limitLocationToWorldBounds(newLoc);

                // Regarder dans la direction du mouvement avec inclinaison vers le bas
                Vector lookDirection = direction.clone();
                lookDirection.setY(lookDirection.getY() - 0.2); // Inclinaison vers le bas
                newLoc.setDirection(lookDirection);

                spectator.teleport(newLoc);
                ticks++;

                if (ticks % 150 == 0) {
                    sendDebugMessage("§dCaverne flight: " + (int)(progress * 100) + "% - Y=" +
                            (int)newLoc.getY() + " - Profondeur souterraine");
                }
            }
        };

        spectator.teleport(startLoc);
        cavernTask.runTaskTimer(UHC.getInstance(), 0, 1L);
        cinematicTasks.put(spectator.getUniqueId(), cavernTask);

        spectator.sendMessage("§8§l» §7Mode: §dCutscene Caverne §7- Exploration souterraine");
    }

    private void startAerialStraightFlight(Player spectator) {
        sendDebugMessage("§dCutscene aérienne - Vol en ligne droite");

        stopAllTasks(spectator);

        World world = spectator.getWorld();

        // Points de départ et d'arrivée aériens (Y entre 100 et 140)
        Location startLoc = new Location(world,
                random.nextInt(1600) - 800, // -800 à +800
                100 + random.nextInt(40),    // Y entre 100 et 140
                random.nextInt(1600) - 800); // -800 à +800

        Location endLoc = new Location(world,
                random.nextInt(1600) - 800,
                110 + random.nextInt(30),    // Y entre 110 et 140
                random.nextInt(1600) - 800);

        // Sécurité : Limiter les positions
        startLoc = limitLocationToWorldBounds(startLoc);
        endLoc = limitLocationToWorldBounds(endLoc);

        Location finalStartLoc = startLoc;
        Location finalEndLoc = endLoc;
        BukkitRunnable aerialTask = new BukkitRunnable() {
            int ticks = 0;
            int maxTicks = 500; // 25 secondes
            Vector startPos = finalStartLoc.toVector();
            Vector endPos = finalEndLoc.toVector();
            Vector direction = endPos.clone().subtract(startPos).normalize();

            @Override
            public void run() {
                if (!spectator.isOnline() || ticks > maxTicks) {
                    this.cancel();
                    return;
                }

                double progress = (double) ticks / maxTicks;

                // Mouvement en ligne droite avec effet de vol d'oiseau
                Vector currentPos = startPos.clone().add(direction.clone().multiply(progress * startPos.distance(endPos)));

                // Mouvement de vol réaliste avec battements d'ailes simulés
                double wingBeat = Math.sin(progress * Math.PI * 20); // Oscillation rapide ±0.8 blocs
                currentPos.setY(currentPos.getY() + wingBeat);

                // Légère dérive latérale due au vent
                double windDrift = Math.sin(progress * Math.PI * 3) * 1.5; // Dérive de ±1.5 blocs
                Vector perpendicular = direction.clone().crossProduct(new Vector(0, 5, 0)).normalize();
                currentPos.add(perpendicular.multiply(windDrift));

                Location newLoc = currentPos.toLocation(world);

                // Limiter la hauteur maximale
                if (newLoc.getY() > MAX_HEIGHT) {
                    newLoc.setY(MAX_HEIGHT);
                }
                newLoc = limitLocationToWorldBounds(newLoc);

                // Regarder dans la direction du mouvement avec inclinaison vers le bas pour voir le paysage
                Vector lookDirection = direction.clone();
                lookDirection.setY(lookDirection.getY() - 0.3); // Inclinaison vers le bas pour voir le sol
                newLoc.setDirection(lookDirection);

                spectator.teleport(newLoc);
                ticks++;

                if (ticks % 125 == 0) {
                    sendDebugMessage("§dAerial flight: " + (int)(progress * 100) + "% - Y=" +
                            (int)newLoc.getY() + " - Vol aérien");
                }
            }
        };

        spectator.teleport(startLoc);
        aerialTask.runTaskTimer(UHC.getInstance(), 0, 1L);
        cinematicTasks.put(spectator.getUniqueId(), aerialTask);

        spectator.sendMessage("§8§l» §7Mode: §dCutscene Aérienne §7- Vol panoramique");
    }


    private void startTravelingCutscene(Player spectator) {
        sendDebugMessage("§dCutscene traveling");

        World world = spectator.getWorld();

        // Point de départ et d'arrivée LIMITÉS
        Location startLoc = new Location(world,
                random.nextInt(1600) - 800, // -800 à +800
                80 + random.nextInt(40),
                random.nextInt(1600) - 800); // -800 à +800

        Location endLoc = new Location(world,
                random.nextInt(1600) - 800, // -800 à +800
                60 + random.nextInt(60),
                random.nextInt(1600) - 800); // -800 à +800

        // SÉCURITÉ : Limiter les positions
        startLoc = limitLocationToWorldBounds(startLoc);
        endLoc = limitLocationToWorldBounds(endLoc);

        Location finalEndLoc = endLoc;
        Location finalStartLoc = startLoc;
        BukkitRunnable travelTask = new BukkitRunnable() {
            int ticks = 0;
            int maxTicks = 400; // 20 secondes
            Vector startPos = finalStartLoc.toVector();
            Vector endPos = finalEndLoc.toVector();

            @Override
            public void run() {
                if (!spectator.isOnline() || ticks > maxTicks) {
                    this.cancel();
                    return;
                }

                double progress = (double) ticks / maxTicks;

                // Interpolation linéaire entre start et end
                Vector currentPos = startPos.clone().multiply(1 - progress).add(endPos.clone().multiply(progress));

                // Ajouter une courbe pour rendre le mouvement plus naturel
                double curve = Math.sin(progress * Math.PI) * 20;
                currentPos.setY(currentPos.getY() + curve);

                Location newLoc = currentPos.toLocation(world);

                // SÉCURITÉ : Vérifier et limiter la position finale
                newLoc = limitLocationToWorldBounds(newLoc);

                // Regarder dans la direction du mouvement
                Vector direction = endPos.clone().subtract(startPos).normalize();
                direction.setY(direction.getY() - 0.1); // Légèrement vers le bas
                newLoc.setDirection(direction);

                spectator.teleport(newLoc);
                ticks++;

                if (ticks % 100 == 0) {
                    sendDebugMessage("§dCutscene traveling: " + (int)(progress * 100) + "% - Pos: X=" +
                            (int)newLoc.getX() + " Z=" + (int)newLoc.getZ());
                }
            }
        };

        spectator.teleport(startLoc);
        travelTask.runTaskTimer(UHC.getInstance(), 0, 1L); // 20 FPS
        cinematicTasks.put(spectator.getUniqueId(), travelTask);

        spectator.sendMessage("§8§l» §7Mode: §dCutscene traveling §7- Vol panoramique");
    }

    private void startOverviewSweep(Player spectator) {
        sendDebugMessage("§dOverview sweep");

        World world = spectator.getWorld();

        // Centre LIMITÉ (au cas où tu veux changer le centre)
        Location centerLoc = new Location(world, 0, 120, 0);
        centerLoc = limitLocationToWorldBounds(centerLoc);

        Location finalCenterLoc = centerLoc;
        BukkitRunnable sweepTask = new BukkitRunnable() {
            int ticks = 0;
            int maxTicks = 300; // 15 secondes
            double radius = Math.min(100, MAX_DISTANCE_FROM_CENTER * 0.8); // Rayon limité à 80% de la distance max
            double startAngle = random.nextDouble() * Math.PI * 2;

            @Override
            public void run() {
                if (!spectator.isOnline() || ticks > maxTicks) {
                    this.cancel();
                    return;
                }

                double progress = (double) ticks / maxTicks;
                double angle = startAngle + (progress * Math.PI); // Demi-cercle

                Vector offset = new Vector(
                        Math.cos(angle) * radius,
                        Math.sin(progress * Math.PI) * 30, // Mouvement vertical en arc
                        Math.sin(angle) * radius
                );

                Location cameraLoc = finalCenterLoc.clone().add(offset);

                // SÉCURITÉ : Limiter la position
                cameraLoc = limitLocationToWorldBounds(cameraLoc);

                // Regarder vers le centre
                Vector direction = finalCenterLoc.toVector().subtract(cameraLoc.toVector()).normalize();
                cameraLoc.setDirection(direction);

                spectator.teleport(cameraLoc);
                ticks++;

                if (ticks % 100 == 0) {
                    sendDebugMessage("§dOverview sweep: " + (int)(progress * 100) + "% - Pos: X=" +
                            (int)cameraLoc.getX() + " Z=" + (int)cameraLoc.getZ());
                }
            }
        };

        sweepTask.runTaskTimer(UHC.getInstance(), 0, 1L); // 20 FPS
        cinematicTasks.put(spectator.getUniqueId(), sweepTask);

        spectator.sendMessage("§8§l» §7Mode: §dOverview sweep §7- Vue d'ensemble");
    }

    // === GESTION DES ÉVÉNEMENTS DE COMBAT ===

    @Override
    public void onFightEvent(FightEvent event) {
        sendDebugMessage("§6Événement combat: §b" + event.getType());

        // Mettre à jour le timestamp d'action pour tous les spectateurs
        for (UUID uuid : spectatorModes.keySet()) {
            lastActionTime.put(uuid, System.currentTimeMillis());
        }

        switch (event.getType()) {
            case FIGHT_START:
                handleFightStart(event);
                break;
            case DAMAGE_DEALT:
                break;
            case INTENSE_FIGHT:
                handleIntenseFight(event);
                break;
            case KILL:
                handleKill(event);
                break;
            case FIGHT_END:
                handleFightEnd(event);
                break;
            case PLAYER_DIED:
                break;
            case PLAYER_DISCONNECTED:
                break;
            case PLAYER_NEAR_FIGHT:
                break;
            default:
                sendDebugMessage("§7Événement ignoré: " + event.getType());
                break;
        }
    }

    private void focusOnFight(Player spectator) {
        // Trouver le combat le plus intéressant et s'y concentrer
        Collection<FightSession> fights = UHC.getInstance().getSpecManager().getFightDetectionSystem().getActiveFights();
        if (!fights.isEmpty()) {
            FightSession fight = fights.iterator().next();
            Set<UUID> participants = fight.getParticipants();
            if (!participants.isEmpty()) {
                Player target = Bukkit.getPlayer(participants.iterator().next());
                if (target != null) {
                    // Utiliser Close Combat pour les fights
                    spectatorModes.put(spectator.getUniqueId(), CameraMode.CLOSE_COMBAT);
                    startCloseCombat(spectator, target);
                    currentTargets.put(spectator.getUniqueId(), target);

                    sendDebugMessage("§6Focus combat: mode Close Combat sur §e" + target.getName());
                }
            }
        }
    }

    private void handleFightStart(FightEvent event) {
        sendDebugMessage("§6Début de combat - basculement de tous les spectateurs");

        for (UUID uuid : spectatorModes.keySet()) {
            Player spectator = Bukkit.getPlayer(uuid);
            if (spectator != null) {
                Player target = event.getPlayer1() != null ? event.getPlayer1() : event.getPlayer2();
                if (target != null) {
                    spectatorModes.put(uuid, CameraMode.CLOSE_COMBAT);
                    startCloseCombat(spectator, target);
                    currentTargets.put(uuid, target);

                    // Empêcher le changement de cible pendant le combat
                    lastTargetSwitch.put(uuid, System.currentTimeMillis() + 30000); // +30 secondes

                    spectator.sendMessage("§8§l» §c⚔ COMBAT DÉTECTÉ ! §7Mode combat activé");
                }
            }
        }
    }

    private void handleIntenseFight(FightEvent event) {
        sendDebugMessage("§cCombat intense - effets dramatiques");

        for (UUID uuid : spectatorModes.keySet()) {
            Player spectator = Bukkit.getPlayer(uuid);
            if (spectator != null) {
                spectator.sendTitle("§c⚔ COMBAT INTENSE ⚔", "§7Action en cours !");
                spectator.playSound(spectator.getLocation(), Sound.ENDERDRAGON_GROWL, 1f, 1f);
            }
        }
    }

    private void handleKill(FightEvent event) {
        if (event.getPlayer1() != null && event.getPlayer2() != null) {
            sendDebugMessage("§4KILL: " + event.getPlayer1().getName() + " → " + event.getPlayer2().getName());

            for (UUID uuid : spectatorModes.keySet()) {
                Player spectator = Bukkit.getPlayer(uuid);
                if (spectator != null) {
                    spectator.sendTitle("§4💀 ÉLIMINATION 💀",
                            "§c" + event.getPlayer1().getName() + " §7→ §c" + event.getPlayer2().getName());
                    spectator.playSound(spectator.getLocation(), Sound.WITHER_SPAWN, 0.8f, 0.8f);
                }
            }
        }
    }

    private void handleFightEnd(FightEvent event) {
        sendDebugMessage("§7Fin de combat - retour au mode normal dans 3 secondes");

        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID uuid : spectatorModes.keySet()) {
                    Player spectator = Bukkit.getPlayer(uuid);
                    if (spectator != null) {
                        spectatorModes.put(uuid, CameraMode.THIRD_PERSON_FOLLOW);
                        lastTargetSwitch.put(uuid, System.currentTimeMillis() - 10000); // Force un changement rapide
                        spectator.sendMessage("§8§l» §7Combat terminé, retour au mode normal");
                    }
                }
            }
        }.runTaskLater(UHC.getInstance(), 60L); // 3 secondes
    }

    // === MÉTHODES UTILITAIRES ===

    private void stopAllTasks(Player spectator) {
        UUID uuid = spectator.getUniqueId();

        // Libérer du mode première personne
        //spectator.setSpectatorTarget(null);

        BukkitRunnable followTask = followTasks.remove(uuid);
        if (followTask != null) {
            followTask.cancel();
            sendDebugMessage("§7Arrêt tâche de suivi pour §e" + spectator.getName());
        }

        BukkitRunnable cinematicTask = cinematicTasks.remove(uuid);
        if (cinematicTask != null) {
            cinematicTask.cancel();
            sendDebugMessage("§7Arrêt tâche cinématique pour §e" + spectator.getName());
        }
    }

    public void disableForPlayer(Player spectator) {
        UUID uuid = spectator.getUniqueId();
        sendDebugMessage("§cDésactivation système pour §e" + spectator.getName());

        if (spectatorTasks.containsKey(uuid)) {
            Bukkit.getScheduler().cancelTask(spectatorTasks.get(uuid));
            spectatorTasks.remove(uuid);
        }

        stopAllTasks(spectator);
        spectatorModes.remove(uuid);
        currentTargets.remove(uuid);
        lastActionTime.remove(uuid);
        lastTargetSwitch.remove(uuid);

        spectator.sendMessage("§8§l» §cSystème cinématique désactivé");
    }

    public boolean isEnabled(Player spectator) {
        return spectatorTasks.containsKey(spectator.getUniqueId());
    }

    public CameraMode getCurrentMode(Player spectator) {
        return spectatorModes.get(spectator.getUniqueId());
    }

    public void setDebugEnabled(boolean enabled) {
        this.debugEnabled = enabled;
        sendDebugMessage("Debug " + (enabled ? "activé" : "désactivé") + " globalement");
    }

    public Set<UUID> getActiveSpectators() {
        return new HashSet<>(spectatorTasks.keySet());
    }
}
