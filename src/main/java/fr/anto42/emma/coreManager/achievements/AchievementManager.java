package fr.anto42.emma.coreManager.achievements;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.anto42.emma.coreManager.achievements.succes.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import java.io.*;
import java.util.*;

public class AchievementManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File DATA_FOLDER = new File("plugins/UHC/achievements/");
    private static final Map<UUID, PlayerAchievementData> PLAYER_DATA = new HashMap<>();
    private static final List<Achievement> REGISTERED_ACHIEVEMENTS = new ArrayList<>();
    private static Plugin plugin;

    public static void init(Plugin mainPlugin) {
        plugin = mainPlugin;
        if (!DATA_FOLDER.exists() && !DATA_FOLDER.mkdirs()) {
            Bukkit.getLogger().severe("Failed to create achievements data folder!");
        }
        registerAchievements(
                new BingoWinner(),
                new Revive(),
                new Pacific(),
                new FirstKill(),
                new ProgressiveKillsGameAchievement.ThreeKills(),
                new ProgressiveKillsGameAchievement.FiveKills(),
                new ProgressiveKillsGameAchievement.TenKills(),
                new ProgressiveKillsGameAchievement.FifteenKills(),
                new ProgressiveKillsGameAchievement.TwentyKills(),
                new ProgressiveSaveConfigAchievement.OneSave(),
                new ProgressiveSaveConfigAchievement.ThreeSaves(),
                new ProgressiveSaveConfigAchievement.FiveSaves(),
                new ProgressiveSaveConfigAchievement.TenSaves(),
                new ProgressiveKillAchievement.OneKill(),
                new ProgressiveKillAchievement.TenKills(),
                new ProgressiveKillAchievement.TwentyFiveKills(),
                new ProgressiveKillAchievement.FiftyKills(),
                new ProgressiveKillAchievement.HundredKills(),
                new ProgressiveKillAchievement.FiveHundredKills(),
                new ProgressiveKillAchievement.ThousandKills(),
                new ProgressiveDiamondAchievement.OneDiamond(),
                new ProgressiveDiamondAchievement.TenDiamonds(),
                new ProgressiveDiamondAchievement.TwentyFiveDiamonds(),
                new ProgressiveDiamondAchievement.FiftyDiamonds(),
                new ProgressiveDiamondAchievement.HundredDiamonds(),
                new ProgressiveDiamondAchievement.FiveHundredDiamonds(),
                new ProgressiveDiamondAchievement.ThousandDiamonds(),
                new ProgressiveDiamondAchievement.FiveThousandDiamonds(),
                new ProgressiveDiamondAchievement.TenThousandDiamonds(),
                new ProgressiveWinAchievement.OneWin(),
                new ProgressiveWinAchievement.FiveWins(),
                new ProgressiveWinAchievement.TenWins(),
                new ProgressiveWinAchievement.TwentyFiveWins(),
                new ProgressiveWinAchievement.FiftyWins(),
                new ProgressiveWinAchievement.HundredWins(),
                new ProgressiveWinAchievement.FiveHundredWins(),
                new ProgressiveWinAchievement.ThousandWins(),
                new ProgressiveGamesPlayedAchievement.OneGame(),
                new ProgressiveGamesPlayedAchievement.TenGames(),
                new ProgressiveGamesPlayedAchievement.TwentyFiveGames(),
                new ProgressiveGamesPlayedAchievement.FiftyGames(),
                new ProgressiveGamesPlayedAchievement.HundredGames(),
                new ProgressiveGamesPlayedAchievement.FiveHundredGames(),
                new ProgressiveGamesPlayedAchievement.ThousandGames(),
                new ProgressiveGoldAchievement.OneGold(),
                new ProgressiveGoldAchievement.TenGolds(),
                new ProgressiveGoldAchievement.TwentyFiveGolds(),
                new ProgressiveGoldAchievement.FiftyGolds(),
                new ProgressiveGoldAchievement.HundredGolds(),
                new ProgressiveGoldAchievement.FiveHundredGolds(),
                new ProgressiveGoldAchievement.ThousandGolds(),
                new ProgressiveGoldAchievement.FiveThousandGolds(),
                new ProgressiveGoldAchievement.TenThousandGolds()
                );
    }

    public static Collection<PlayerAchievementData> getAllPlayerData() {
        return PLAYER_DATA.values();
    }
    public static int countPlayersWithAchievement(String achievementId) {
        int count = 0;
        for (PlayerAchievementData data : getAllPlayerData()) {
            PlayerAchievementData.AchievementProgress progress = data.getProgress(achievementId);
            if (progress != null && progress.isCompleted()) {
                count++;
            }
        }
        return count;
    }


    public static PlayerAchievementData loadPlayerAchievementData(UUID uuid) {
        File file = new File("plugins/UHC/achievements/", uuid.toString() + ".json");
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                return GSON.fromJson(reader, PlayerAchievementData.class);
            } catch (IOException e) {
                Bukkit.getLogger().severe("Erreur lors du chargement des succès pour " + uuid + " : " + e.getMessage());
                e.printStackTrace();
            }
        }
        return new PlayerAchievementData(uuid);
    }


    public static int countAllSavedPlayers() {
        File[] files = DATA_FOLDER.listFiles((dir, name) -> name.endsWith(".json"));
        return files != null ? files.length : 0;
    }


    public static void registerAchievements(Achievement... achievements) {
        REGISTERED_ACHIEVEMENTS.addAll(Arrays.asList(achievements));
        REGISTERED_ACHIEVEMENTS.forEach(achievement -> Bukkit.getPluginManager().registerEvents(achievement, plugin));
    }

    public static void registerPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        PLAYER_DATA.computeIfAbsent(uuid, k -> loadOrCreate(player));
        savePlayerData(player);
    }

    public static int countPlayersWithAchievementAll(String achievementId) {
        int count = 0;
        File[] files = DATA_FOLDER.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) return 0;
        for (File file : files) {
            try (Reader reader = new FileReader(file)) {
                PlayerAchievementData data = GSON.fromJson(reader, PlayerAchievementData.class);
                PlayerAchievementData.AchievementProgress progress = data.getProgress(achievementId);
                if (progress != null && progress.isCompleted()) {
                    count++;
                }
            } catch (IOException ignored) {
            }
        }
        return count;
    }
    public static void removeAchievementFromPlayer(String playerName, String achievementId) {
        UUID targetUUID = null;
        for (OfflinePlayer offlinePlayer : Bukkit.getOnlinePlayers()) {
            if (offlinePlayer.getName() != null && offlinePlayer.getName().equalsIgnoreCase(playerName)) {
                targetUUID = offlinePlayer.getUniqueId();
                break;
            }
        }
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            if (offlinePlayer.getName() != null && offlinePlayer.getName().equalsIgnoreCase(playerName)) {
                targetUUID = offlinePlayer.getUniqueId();
                break;
            }
        }
        if (targetUUID == null)
            return;

        removeAchievementFromPlayer(targetUUID, achievementId);
    }


    public static void removeAchievementFromPlayer(UUID playerUUID, String achievementId) {
        PlayerAchievementData data = PLAYER_DATA.containsKey(playerUUID)
                ? PLAYER_DATA.get(playerUUID)
                : loadPlayerAchievementData(playerUUID);

        PlayerAchievementData.AchievementProgress progress = data.getProgress(achievementId);
        if (progress == null || !progress.isCompleted()) {
            return;
        }

        data.resetProgress(achievementId);

        PLAYER_DATA.put(playerUUID, data);
        savePlayerData(playerUUID);
    }


    public static void savePlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerAchievementData data = PLAYER_DATA.get(uuid);
        if (data != null) {
            saveAsync(data);
        }
    }

    public static void savePlayerData(UUID uuid) {
        PlayerAchievementData data = PLAYER_DATA.get(uuid);
        if (data != null) {
            saveAsync(data);
        }
    }

    public static PlayerAchievementData getPlayerData(Player player) {
        return PLAYER_DATA.get(player.getUniqueId());
    }

    public static Optional<Achievement> getAchievementById(String id) {
        return REGISTERED_ACHIEVEMENTS.stream()
                .filter(a -> a.getId().equalsIgnoreCase(id))
                .findFirst();
    }

    private static PlayerAchievementData loadOrCreate(Player player) {
        File file = new File(DATA_FOLDER, player.getUniqueId() + ".json");

        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                return GSON.fromJson(reader, PlayerAchievementData.class);
            } catch (IOException e) {
                handleCorruptedFile(file, e);
            }
        }
        return new PlayerAchievementData(player.getUniqueId());
    }

    private static void saveAsync(PlayerAchievementData data) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            File file = new File(DATA_FOLDER, data.getPlayerUUID() + ".json");
            try (Writer writer = new FileWriter(file)) {
                GSON.toJson(data, writer);
            } catch (IOException e) {
                Bukkit.getLogger().severe("Erreur sauvegarde succès pour " + data.getPlayerUUID());
                e.printStackTrace();
            }
        });
    }

    public static List<Achievement> getAllAchievements() {
        return new ArrayList<>(REGISTERED_ACHIEVEMENTS);
    }

    private static void handleCorruptedFile(File file, Exception e) {
        Bukkit.getLogger().warning("Fichier corrompu détecté : " + file.getName());
        File backup = new File(file.getParentFile(), file.getName() + ".corrupted");
        if (file.renameTo(backup)) {
            Bukkit.getLogger().warning("Backup créé : " + backup.getName());
        }
    }
}
