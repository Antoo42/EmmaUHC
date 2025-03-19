package fr.anto42.emma.coreManager.tasks;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.listeners.customListeners.StartEvent;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.coreManager.scenarios.UHCScenario;
import fr.anto42.emma.coreManager.teams.UHCTeam;
import fr.anto42.emma.coreManager.teams.UHCTeamManager;
import fr.anto42.emma.coreManager.worldManager.WorldManager;
import fr.anto42.emma.game.GameState;
import fr.anto42.emma.game.UHCGame;
import fr.anto42.emma.utils.Cuboid;
import fr.anto42.emma.utils.chat.Title;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.SoundUtils;
import fr.anto42.emma.utils.TimeUtils;
import fr.anto42.emma.utils.saves.ItemStackToString;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class StarterTask {
    private final UHCGame uhc = UHC.getInstance().getUhcGame();
    private final List<Location> locationList = new ArrayList<>();
    private final Random random = new Random();
    private int playerCount = 0;

    public void startUHC() {
        List<UHCPlayer> players = new ArrayList<>(uhc.getUhcData().getUhcPlayerList());
        int totalPlayers = players.size();
        int borderSize = uhc.getUhcConfig().getStartBorderSize();
        boolean isTeamMode = UHCTeamManager.getInstance().isActivated();

        if (isTeamMode) {
            prepareTeams(players);
            teleportEntities(new ArrayList<>(UHCTeamManager.getInstance().getUhcTeams()), totalPlayers, borderSize, true);
        } else {
            teleportEntities(players, totalPlayers, borderSize, false);
        }
        WorldManager.getGameWorld().getWorldBorder().setSize(borderSize*2);
        WorldManager.getNetherWorld().getWorldBorder().setSize(borderSize*2);
    }

    private void prepareTeams(List<UHCPlayer> players) {
        UHCTeamManager teamManager = UHCTeamManager.getInstance();

        players.stream()
                .filter(p -> p.getUhcTeam() == null)
                .forEach(p -> Optional.ofNullable(teamManager.getRandomFreeTeam()).ifPresent(p::joinTeam));

        teamManager.setUhcTeams(new ArrayList<>(teamManager.getUhcTeams().stream()
                .filter(UHCTeam::isAlive)
                .collect(Collectors.toList())));    }

    private void teleportEntities(List<?> entities, int totalPlayers, int borderSize, boolean isTeamMode) {
        Collections.shuffle(entities);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!checkTPS() || entities.isEmpty()) return;

                Object entity = entities.remove(0);
                Location loc = generateLocation(borderSize);
                locationList.add(loc);
                createSpawnPlatform(loc);

                if (isTeamMode) {
                    UHCTeam team = (UHCTeam) entity;
                    team.setStartLoc(loc);
                    team.getAliveUhcPlayers().stream()
                            .map(UHCPlayer::getBukkitPlayer)
                            .filter(Objects::nonNull)
                            .forEach(p -> teleportPlayer(p, loc, totalPlayers));
                } else {
                    teleportPlayer(((UHCPlayer) entity).getBukkitPlayer(), loc, totalPlayers);
                }

                if (entities.isEmpty()) {
                    preStart();
                    cancel();
                }
            }
        }.runTaskTimer(UHC.getInstance(), 5L, 15L);
    }

    private boolean checkTPS() {
        return MinecraftServer.getServer().recentTps[0] > 17.5D;
    }

    private Location generateLocation(int borderSize) {
        int x = random.nextInt((borderSize - 20) ) - (borderSize - 20);
        int z = random.nextInt((borderSize - 20) ) - (borderSize - 20);
        return new Location(WorldManager.getGameWorld(), x, 200, z);
    }

    private void createSpawnPlatform(Location loc) {
        int y = 200;
        World world = loc.getWorld();
        Cuboid platform = new Cuboid(world, loc.getBlockX() - 5, y, loc.getBlockZ() - 5, loc.getBlockX() + 5, y, loc.getBlockZ() + 5);
        platform.forEach(block -> block.setType(Material.STAINED_GLASS));

        for (int i = -5; i <= 5; i++) {
            world.getBlockAt(loc.getBlockX() + i, y + 2, loc.getBlockZ() - 5).setType(Material.BARRIER);
            world.getBlockAt(loc.getBlockX() + i, y + 2, loc.getBlockZ() + 5).setType(Material.BARRIER);
            world.getBlockAt(loc.getBlockX() - 5, y + 2, loc.getBlockZ() + i).setType(Material.BARRIER);
            world.getBlockAt(loc.getBlockX() + 5, y + 2, loc.getBlockZ() + i).setType(Material.BARRIER);
        }
    }

    private void teleportPlayer(Player player, Location loc, int totalPlayers) {
        if (player == null) return;

        playerCount++;
        uhc.getUhcData().getWhiteListPlayer().add(player.getUniqueId());
        player.teleport(loc.add(0, 1, 0));
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().clear();

        Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §7Téléportation de §6" + player.getName() + " §7(" + playerCount + "/" + totalPlayers + ")");
    }

    private void preStart() {
        Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §7Stabilisation des §aTPS §7en cours...");
        if (!UHC.getInstance().getConfig().getBoolean("customSpawn")) {
            World gameWorld = WorldManager.getGameWorld();
            Cuboid cuboid = new Cuboid(gameWorld, -20, 200, 20, 20, 200, -20);
            cuboid.forEach(block -> {
                block.setType(Material.AIR);
            });
            cuboid = new Cuboid(gameWorld, -20, 201, 20, 20, 203, 20);
            cuboid.forEach(block -> {
                block.setType(Material.AIR);
            });
            cuboid = new Cuboid(gameWorld, 20, 201, -20, 20, 203, 20);
            cuboid.forEach(block -> {
                block.setType(Material.AIR);
            });
            cuboid = new Cuboid(gameWorld, 20, 201, -20, -20, 203, -20);
            cuboid.forEach(block -> {
                block.setType(Material.AIR);
            });
            cuboid = new Cuboid(gameWorld, -20, 201, -20, -20, 203, 20);
            cuboid.forEach(block -> {
                block.setType(Material.AIR);
            });
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if (checkTPS()) {
                    cancel();
                    startGame();
                }
            }
        }.runTaskTimer(UHC.getInstance(), 100L, 20L);
    }

    private void destroySpawnPlatforms() {
        for (Location loc : locationList) {
            int y = 200;
            World world = loc.getWorld();

            Cuboid platform = new Cuboid(world, loc.getBlockX() - 5, y, loc.getBlockZ() - 5, loc.getBlockX() + 5, y, loc.getBlockZ() + 5);
            platform.forEach(block -> block.setType(Material.AIR));

            for (int i = -5; i <= 5; i++) {
                world.getBlockAt(loc.getBlockX() + i, y + 2, loc.getBlockZ() - 5).setType(Material.AIR);
                world.getBlockAt(loc.getBlockX() + i, y + 2, loc.getBlockZ() + 5).setType(Material.AIR);
                world.getBlockAt(loc.getBlockX() - 5, y + 2, loc.getBlockZ() + i).setType(Material.AIR);
                world.getBlockAt(loc.getBlockX() + 5, y + 2, loc.getBlockZ() + i).setType(Material.AIR);
            }
        }
    }


    private void startGame() {
        destroySpawnPlatforms();
        Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §aDébut de la partie !");
        uhc.setGameState(GameState.PLAYING);
        uhc.getUhcTimer().runTaskTimer(UHC.getInstance(), 0L, 20L);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            Title.sendTitle(onlinePlayer, 0, 5*20, 20, "§8§l» §aGLHF", "§cEmmaUHC §7par §b@Anto42_");
            SoundUtils.playSoundToPlayer(onlinePlayer, Sound.ANVIL_LAND);
        }
        uhc.getUhcData().getUhcPlayerList().forEach(p -> {
            Player player = p.getBukkitPlayer();
            if (player != null) {
                player.setLevel(0);
                p.setDamageable(false);
                if (!uhc.getUhcConfig().getStarterStuffConfig().getHead().contains("BARRIER")) {
                    player.getInventory().setHelmet(ItemStackToString.ItemStackFromString(uhc.getUhcConfig().getStarterStuffConfig().getHead()));
                }
                if (!uhc.getUhcConfig().getStarterStuffConfig().getBody().contains("BARRIER")) {
                    player.getInventory().setChestplate(ItemStackToString.ItemStackFromString(uhc.getUhcConfig().getStarterStuffConfig().getBody()));
                }
                if (!uhc.getUhcConfig().getStarterStuffConfig().getLeggins().contains("BARRIER")) {
                    player.getInventory().setLeggings(ItemStackToString.ItemStackFromString(uhc.getUhcConfig().getStarterStuffConfig().getLeggins()));
                }
                if (!uhc.getUhcConfig().getStarterStuffConfig().getBoots().contains("BARRIER")) {
                    player.getInventory().setBoots(ItemStackToString.ItemStackFromString(uhc.getUhcConfig().getStarterStuffConfig().getBoots()));
                }
                for (String s : uhc.getUhcConfig().getStarterStuffConfig().getStartInv()) {
                    player.getInventory().addItem(ItemStackToString.ItemStackFromString(s));
                }
                for (ItemStack content : player.getInventory().getContents()) {
                    if (content == null) return;
                    if (content.getType().equals(Material.BARRIER)) {
                        player.getInventory().removeItem(content);
                    }
                }
            }
        });

        Bukkit.getScheduler().runTaskLater(UHC.getInstance(), () -> {
            Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §7Les §cdegâts (PvE) §7sont désormais §cactifs §7!");
            uhc.getUhcData().getUhcPlayerList().forEach(p -> p.setDamageable(true));
            SoundUtils.playSoundToAll(Sound.ORB_PICKUP);
        }, TimeUtils.seconds(uhc.getUhcConfig().getGodStart()));

        Bukkit.getServer().getPluginManager().callEvent(new StartEvent());
        new ArrowTask().runTaskTimer(UHC.getInstance(), 0L, 3L);
        new NoSpecLagTask().runTaskTimer(UHC.getInstance(), 0L, 2L);
    }

}
