package fr.anto42.emma.coreManager.listeners.customListeners;

import fr.anto42.emma.UHC;
import fr.anto42.emma.utils.TimeUtils;
import fr.anto42.emma.utils.gameSaves.EventType;
import fr.anto42.emma.utils.gameSaves.GameSave;
import org.bukkit.Bukkit;

public class RolesEvent extends UHCEvent {
    public RolesEvent() {
        GameSave gameSave = UHC.getInstance().getGameSave();
        gameSave.registerEvent(EventType.CORE, "Répartition des rôles");
        Bukkit.getScheduler().runTaskLater(UHC.getInstance(), () -> {
            UHC.getInstance().getUhcGame().getUhcData().getUhcPlayerList().forEach(uhcPlayer -> {
                gameSave.registerEvent(EventType.ROLE, (uhcPlayer.getRole() == null ? uhcPlayer.getName() + " n'a reçu aucun rôle" : uhcPlayer.getName() + "a reçu le rôle " + uhcPlayer.getRole().getName()));
            });
        }, TimeUtils.seconds(3));
    }
}
