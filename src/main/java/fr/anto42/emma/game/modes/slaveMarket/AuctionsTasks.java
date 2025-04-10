package fr.anto42.emma.game.modes.slaveMarket;

import fr.anto42.emma.UHC;
import fr.anto42.emma.game.modes.slaveMarket.impl.AuctionSTATE;
import fr.anto42.emma.utils.chat.Title;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class AuctionsTasks extends BukkitRunnable {
    private final SlaveModule module;

    public AuctionsTasks(SlaveModule module) {
        this.module = module;
    }


    int timer = 0;
    @Override
    public void run() {
        timer++;
        if (UHC.getInstance().getUhcManager().getGamemode() != module) {
            module.setAuctionSTATE(AuctionSTATE.WAITING);
            this.cancel();
        }
        if (timer >= module.getSlaveConfig().getAuctionDuration()) {
            module.getAuction().endBid();
            this.cancel();
            Bukkit.getScheduler().runTaskLater(UHC.getInstance(), module::newBid, 30L);
        }
        Title.sendActionBar("§8§l» §7Enchère sur §a" + module.getAuction().getSlave().getName() + " §8§l» §7Plus offrant: " + (module.getAuction().getActualOwner() == null ? "§cAucun" : "§a" + module.getAuction().getActualOwner().getName() + " §7pour §b" + module.getAuction().getPrice() + " diamants") + " §8§l» §e" + (module.getSlaveConfig().getAuctionDuration() - timer) + " secondes restantes");
    }
}
