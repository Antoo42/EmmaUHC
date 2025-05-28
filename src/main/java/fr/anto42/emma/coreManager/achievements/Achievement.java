package fr.anto42.emma.coreManager.achievements;

import fr.anto42.emma.UHC;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class Achievement implements Listener {
    private final String id;
    private final String name;
    private final String description;
    private final int required;

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
        UHC.getUHCPlayer(player).sendClassicMessage("§7Vous avez terminé le succès §a" + this.name + "§7 !");
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
    }
}
