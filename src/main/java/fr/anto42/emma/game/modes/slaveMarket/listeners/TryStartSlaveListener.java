package fr.anto42.emma.game.modes.slaveMarket.listeners;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.listeners.customListeners.TryStartEvent;
import fr.anto42.emma.game.modes.slaveMarket.SlaveModule;
import fr.anto42.emma.game.modes.slaveMarket.impl.AuctionSTATE;
import fr.anto42.emma.utils.players.PlayersUtils;
import fr.anto42.emma.utils.players.SoundUtils;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TryStartSlaveListener implements Listener {
    private final SlaveModule module;


    public TryStartSlaveListener(SlaveModule module) {
        this.module = module;
    }


    @EventHandler
    public void onTryStart(TryStartEvent event) {
        if (UHC.getInstance().getUhcManager().getGamemode().equals(module) && module.getAuctionSTATE() != AuctionSTATE.FINISHED) {
            event.setCancelled(true);
            PlayersUtils.broadcastMessage("§cLes enchères doivent être terminés avant de débuter la partie.");
            SoundUtils.playSoundToAll(Sound.VILLAGER_NO);
        }
    }
}
