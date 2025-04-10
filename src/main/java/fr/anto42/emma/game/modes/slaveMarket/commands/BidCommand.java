package fr.anto42.emma.game.modes.slaveMarket.commands;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.game.modes.slaveMarket.impl.AuctionSTATE;
import fr.anto42.emma.game.modes.slaveMarket.SlaveModule;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BidCommand extends Command {
    private final SlaveModule module;

    public BidCommand(SlaveModule module) {
        super("bid");
        this.module = module;
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (module.getAuctionSTATE() != AuctionSTATE.PENDING) return false;
        UHCPlayer uhcPlayer = UHC.getUHCPlayer(((Player) commandSender));
        if (!module.getSlaveConfig().getLeadersList().contains(uhcPlayer)) return false;
        if (strings.length < 1)
            return false;
        int price = Integer.parseInt(strings[0]);
        module.getAuction().bid(uhcPlayer, price);
        return false;
    }
}
