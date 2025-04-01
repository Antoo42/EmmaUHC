package fr.anto42.emma.coreManager.listeners.customListeners;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.coreManager.players.UHCPlayerStates;
import fr.anto42.emma.utils.TimeUtils;
import fr.anto42.emma.utils.gameSaves.EventType;
import org.bukkit.Bukkit;

public class ReviveEvent extends UHCEvent{
    private final UHCPlayer revived;

    public ReviveEvent(UHCPlayer revived) {
        this.revived = revived;
        UHC.getInstance().getGameSave().registerEvent(EventType.CORE, "Revive de" + revived.getName());
        Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §a" + revived.getName() + "§7 a été réssucité !");
        revived.setPlayerState(UHCPlayerStates.ALIVE);
    }

    public UHCPlayer getUHCPlayer() {
        return revived;
    }
}
