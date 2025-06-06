package fr.anto42.emma.game;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.tasks.StarterTask;
import fr.anto42.emma.coreManager.tasks.UHCTimer;
import fr.anto42.emma.game.impl.UHCData;
import fr.anto42.emma.game.impl.config.UHCConfig;
import fr.anto42.emma.utils.players.SoundUtils;
import fr.anto42.emma.utils.gameSaves.EventType;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static fr.anto42.emma.game.GameState.STARTING;
import static fr.anto42.emma.game.GameState.WAITING;

public class UHCGame {
    private final UHCData uhcData;
    private UHCConfig uhcConfig;
    private GameState gameState;
    private final UHCTimer uhcTimer = new UHCTimer(this);


    public UHCGame() {
        gameState = WAITING;
        this.uhcData = new UHCData();
        this.uhcConfig = new UHCConfig();
    }

    public UHCConfig getUhcConfig() {
        return uhcConfig;
    }

    public GameState getGameState() {
        return gameState;
    }


    public void setUhcConfig(UHCConfig uhcConfig) {
        this.uhcConfig = uhcConfig;
    }

    public UHCData getUhcData() {
        return uhcData;
    }

    public UHCTimer getUhcTimer() {
        return uhcTimer;
    }

    /*public StarterStuffConfig getStarterStuffConfig() {
        return uhcConfig.getStarterStuffConfig();
    }*/



    private boolean start = false;

    public void setGameState(GameState gameState) {
        UHC.getInstance().getGameSave().registerEvent(EventType.CORE, "Passage du statut de la partie vers: " + gameState.getString());
        this.gameState = gameState;
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }
    int timer = 20*10;
    public void startGame(){
        timer = 10*20;
        setStart(true);
        (new BukkitRunnable() {
            @Override
            public void run() {
                if (!start || Bukkit.getOnlinePlayers().isEmpty()){
                    Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §cLancement annulé !");
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        onlinePlayer.setLevel(0);
                    }
                    this.cancel();
                } else {
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        onlinePlayer.setLevel(timer/20 + 1);
                    }
                    if (timer == 10*20 || timer == 5*20 || timer == 3*20 || timer == 2*20 || timer == 20){
                        Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §aDémarrage de la partie dans §b" + timer/20 + "§a seconde" + (timer != 20 ? "s" : "") +  " !");
                        SoundUtils.playSoundToAll(Sound.ORB_PICKUP);
                    }
                    
                    if (timer == 0){
                        Bukkit.getOnlinePlayers().forEach(player -> {
                            player.setLevel(0);
                        });
                        gameState = STARTING;
                        new StarterTask().startUHC();
                        this.cancel();
                    }
                    timer--;
                }
            }
        }).runTaskTimer(UHC.getInstance(), 0L, 1L);
    }
}
