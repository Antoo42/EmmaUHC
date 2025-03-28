package fr.anto42.emma;


import fr.anto42.emma.coreManager.UHCManager;
import fr.anto42.emma.coreManager.commands.*;
import fr.anto42.emma.coreManager.enchants.EnchantsManager;
import fr.anto42.emma.coreManager.enchants.config.EnchantConfiguration;
import fr.anto42.emma.coreManager.listeners.CoreListeners;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.coreManager.players.messages.MessageManager;
import fr.anto42.emma.coreManager.scoreboard.ScoreboardManager;
import fr.anto42.emma.coreManager.teams.UHCTeamManager;
import fr.anto42.emma.coreManager.worldManager.BiomeChanger;
import fr.anto42.emma.coreManager.worldManager.WorldManager;
import fr.anto42.emma.game.GameState;
import fr.anto42.emma.game.UHCGame;
import fr.anto42.emma.utils.CommandUtils;
import fr.anto42.emma.utils.discord.DiscordEvents;
import fr.anto42.emma.utils.discord.DiscordManager;
import fr.anto42.emma.utils.gameSaves.GameSave;
import fr.anto42.emma.utils.saves.SaveSerializationManager;
import fr.blendman974.kinventory.KInventoryManager;
import me.daddychurchill.CityWorld.CityWorld;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public final class UHC extends JavaPlugin {

    private static UHC instance;
    private UHCManager uhcManager;
    private UHCGame uhcGame;
    private EnchantsManager enchantsManager;
    private DiscordManager discordManager;
    private MessageManager messageManager;
    private WorldManager worldManager;

    private int version;

    private static final int MIN_VERSION = 8;
    private static final int MAX_VERSION = 12;

    private ScoreboardManager scoreboardManager;
    private ScheduledExecutorService executorMonoThread;
    private ScheduledExecutorService scheduledExecutorService;

    private SaveSerializationManager saveSerializationManager;
    private String prefix;

    private CityWorld cityWorldPlugin;

    private GameSave gameSave;
    private String ip;

    @Override
    public void onEnable() {
        getLogger().info("");
        getLogger().info("§b§lStarting initialize UHCCore by Anto42_");
        getLogger().info("");

        saveDefaultConfig();
        instance = this;
        prefix = getConfig().getString("generalPrefix").replace("&", "§");
        ip = getConfig().getString("ip");
        cityWorldPlugin = ((CityWorld) Bukkit.getPluginManager().getPlugin("CityWorld"));

        getLogger().info("§eInitialisation des gestionnaires...");

        KInventoryManager.init(this);
        BiomeChanger.init();

        worldManager = new WorldManager();
        worldManager.init();
        getLogger().info("§aWorldManager initialisé.");

        enchantsManager = new EnchantsManager();
        saveSerializationManager = new SaveSerializationManager();
        getLogger().info("§aEnchantsManager et SaveSerializationManager initialisés.");

        PluginManager pluginManager = Bukkit.getPluginManager();
        uhcGame = new UHCGame();
        uhcManager = new UHCManager();
        getLogger().info("§aUHCGame et UHCManager initialisés.");

        if (getConfig().getBoolean("discordbot")) {
            discordManager = new DiscordManager();
            pluginManager.registerEvents(new DiscordEvents(), this);
            getLogger().info("§aDiscordManager activé et événements enregistrés.");
        }

        messageManager = new MessageManager();
        pluginManager.registerEvents(new CoreListeners(), this);
        getLogger().info("§aMessageManager et CoreListeners enregistrés.");

        getLogger().info("§eEnregistrement des commandes en cours...");

        CommandUtils.registerCommand("uhc", new SetHostComand());
        //CommandUtils.registerCommand("uhc", new ColorCommand());
        CommandUtils.registerCommand("uhc", new CreditsCommand());
        CommandUtils.registerCommand("uhc", new HCommand());
        CommandUtils.registerCommand("uhc", new SaveCommand());
        CommandUtils.registerCommand("uhc", new GodCommand());
        CommandUtils.registerCommand("uhc", new SpecCommand());
        CommandUtils.registerCommand("uhc", new HelpopCommand());
        CommandUtils.registerCommand("uhc", new MSGCommand());
        CommandUtils.registerCommand("uhc", new ReplyCommand());
        CommandUtils.registerCommand("uhc", new DocCommand());
        CommandUtils.registerCommand("uhc", new RulesCommand());
        CommandUtils.registerCommand("uhc", new VoteCommand());
        CommandUtils.registerCommand("uhc", new EnchantConfiguration());
        CommandUtils.registerCommand("uhc", new LagCommand());
        CommandUtils.registerCommand("uhc", new TCCommand());
        CommandUtils.registerCommand("uhc", new ScenariosCommand());
        CommandUtils.registerCommand("uhc", new DevCommand());
        CommandUtils.registerCommand("uhc", new WhitelistCommand());
        CommandUtils.registerCommand("uhc", new SpawnCommand());
        CommandUtils.registerCommand("uhc", new BackupCommand());
        CommandUtils.registerCommand("uhc", new WinTest());
        CommandUtils.registerCommand("uhc", new StatsCommand());

        getLogger().info("§aToutes les commandes ont été enregistrées avec succès.");

        scheduledExecutorService = Executors.newScheduledThreadPool(16);
        executorMonoThread = Executors.newScheduledThreadPool(1);
        scoreboardManager = new ScoreboardManager();
        getLogger().info("§aGestionnaires d'exécution et ScoreboardManager initialisés.");

        UHCTeamManager.getInstance().createTeams();
        getLogger().info("§aUHCTeamManager : équipes créées.");

        loadServerVersion();
        getUhcGame().getUhcData().setWhiteList(getConfig().getBoolean("whiteListOnTheStart"));
        getLogger().info("§aVersion du serveur chargée et whitelist définie.");

        gameSave = new GameSave();
        getLogger().info("§aGameSave initialisé.");

        Bukkit.getScheduler().runTaskTimer(this, this::updateTabList, 2L, 2L);
        getLogger().info("§aMise à jour du TabList programmée.");

        getLogger().info("");
        getLogger().info("§a§lSuccessfully initialized UHCCore");
        getLogger().info("");
    }


    public void updateTabList() {
        if (UHC.getInstance().getUhcGame().getUhcData().getHostPlayer() == null)
            return;
        for (Player p : Bukkit.getOnlinePlayers()) {
            UHCPlayer uhcPlayer = UHC.getUHCPlayer(p);

            if (uhcGame.getUhcConfig().isLifeTab() && uhcGame.getGameState().equals(GameState.PLAYING)) {
                int healthPercent = (int) (p.getHealth() / p.getMaxHealth() * 100.0);
                p.setPlayerListName((uhcPlayer.getUhcTeam() != null ? uhcPlayer.getUhcTeam().getPrefix() : "") + "§7" + p.getName() + "§e (" + healthPercent + "%)");
            }
            else {
                p.setPlayerListName((uhcPlayer.isHost() ? "§6§lHOST §8┃ " : "") + (uhcPlayer.isSpec() ? "§c§lSPEC §8┃ " : "") + (uhcPlayer.getUhcTeam() != null ? uhcPlayer.getUhcTeam().getDisplayName() : "§f") + " " + p.getName());
            }
        }
    }


    private void loadServerVersion() {
        String versionString = Bukkit.getBukkitVersion();
        version = 0;

        for (int i = MIN_VERSION; i <= MAX_VERSION; i++) {
            if (versionString.contains("1." + i)) {
                version = i;
            }
        }

        if (version == 0) {
            version = MIN_VERSION;
            Bukkit.getLogger().warning("UHC: Failed to detect server version! " + versionString + "? For support contact Anto42_ or @Antooooo42 !");
        } else {
            Bukkit.getLogger().info("UHC: 1." + version + " Server detected!");
        }
    }

    public UHCGame getUhcGame() {
        return uhcGame;
    }

    public UHCManager getUhcManager() {
        return uhcManager;
    }

    public static UHC getInstance() {
        return instance;
    }

    public EnchantsManager getEnchantsManager() {
        return enchantsManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public DiscordManager getDiscordManager() {
        return discordManager;
    }

    @Override
    public void onDisable() {
        if (getConfig().getBoolean("discordbot"))
            discordManager.sendShutdownAnounce();
    }

    public static void registerPlayer(Player player) {
        if (uhcPlayerMap.get(player.getUniqueId()) != null) {
            uhcPlayerMap.get(player.getUniqueId()).setPlayer(player);
            return;
        }
        uhcPlayerMap.put(player.getUniqueId(), new UHCPlayer(player.getUniqueId(), player.getName(), player));
    }

    public static void unregisterPlayer(Player player) {
        uhcPlayerMap.remove(player.getUniqueId());
    }

    private final static Map<UUID, UHCPlayer> uhcPlayerMap = new HashMap<>();

    public static Map<UUID, UHCPlayer> getUHCPlayerMap() {
        return uhcPlayerMap;
    }

    public static UHCPlayer getUHCPlayer(Player player) {
        return uhcPlayerMap.get(player.getUniqueId());
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public ScheduledExecutorService getExecutorMonoThread() {
        return executorMonoThread;
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public WorldManager getWorldManager() {
        return worldManager;
    }

    public SaveSerializationManager getSaveSerializationManager() {
        return saveSerializationManager;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }


    public String getIp() {
        return ip;
    }

    public GameSave getGameSave() {
        return gameSave;
    }

    public void setGameSave(GameSave gameSave) {
        this.gameSave = gameSave;
    }

    public CityWorld getCityWorldPlugin() {
        return cityWorldPlugin;
    }

    public void setCityWorldPlugin(CityWorld cityWorldPlugin) {
        this.cityWorldPlugin = cityWorldPlugin;
    }
}
