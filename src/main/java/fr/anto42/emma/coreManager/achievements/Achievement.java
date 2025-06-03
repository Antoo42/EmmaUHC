package fr.anto42.emma.coreManager.achievements;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.utils.gameSaves.EventType;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class Achievement implements Listener {
    private final String id;
    private final String name;
    private final String description;
    private final int required;
    private boolean secret = false;

    public Achievement(String id, String name, String description, int required) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.required = required;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getRequired() { return required; }

    public void notifyAchievement(Player player) {
        UHCPlayer uhcPlayer = UHC.getUHCPlayer(player);
        UHC.getInstance().getGameSave().registerEvent(
                EventType.ACHIVEMENT,
                player.getDisplayName() + " a terminé le succès: " + getId()
        );

        int nbPlayers = AchievementManager.countPlayersWithAchievementAll(getId()) + 1;

        uhcPlayer.sendMessage("");
        uhcPlayer.sendMessage("§8§m§l-----------------------------");
        uhcPlayer.sendMessage("§8┃ §a§lSuccès débloqué !");
        uhcPlayer.sendMessage("§8┃");
        uhcPlayer.sendMessage("§8┃ §e§l" + getName());
        uhcPlayer.sendMessage("§8┃ §7" + getDescription());
        uhcPlayer.sendMessage("§8┃");
        uhcPlayer.sendMessage("§8┃ §7Tu es le §b" + nbPlayers + "§7ᵉ joueur à débloquer ce succès !");
        uhcPlayer.sendMessage("§8§m§l-----------------------------");
        uhcPlayer.sendMessage("");

        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
    }


    public void setSecret(boolean secret) {
        this.secret = secret;
    }

    public boolean isSecret() {
        return secret;
    }
    public void makeSecret(){
        this.secret = true;
    }
}
