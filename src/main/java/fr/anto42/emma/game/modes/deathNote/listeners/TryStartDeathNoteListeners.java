package fr.anto42.emma.game.modes.deathNote.listeners;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.listeners.customListeners.TryStartEvent;
import fr.anto42.emma.coreManager.teams.UHCTeamManager;
import fr.anto42.emma.game.modes.deathNote.DeathNoteModule;
import fr.anto42.emma.utils.players.PlayersUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TryStartDeathNoteListeners implements Listener {
    private final DeathNoteModule module;

    public TryStartDeathNoteListeners(DeathNoteModule module) {
        this.module = module;
    }


    @EventHandler
    public void onTryStartListener(TryStartEvent event) {
        if (UHC.getInstance().getUhcManager().getGamemode() != module)
            return;
        if (!UHCTeamManager.getInstance().isActivated()) {
            PlayersUtils.broadcastMessage("§cL'activation des équipes est primordiale dans ce mode de jeu !");
            event.setCancelled(true);
        }
    }
}
