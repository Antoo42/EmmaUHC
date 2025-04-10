package fr.anto42.emma.game.modes.slaveMarket.impl;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.game.modes.slaveMarket.SlaveModule;
import fr.anto42.emma.utils.gameSaves.EventType;
import fr.anto42.emma.utils.players.PlayersUtils;
import fr.anto42.emma.utils.players.SoundUtils;
import org.bukkit.Sound;

public class Auction {
    private final UHCPlayer slave;
    private int price = 0;
    private UHCPlayer actualOwner = null;
    private final SlaveModule module;

    public Auction(UHCPlayer slave, SlaveModule module) {
        this.slave = slave;
        this.module = module;
        PlayersUtils.broadcastMessage("");
        PlayersUtils.broadcastMessage("§aDébut de l'enchère sur §e" + slave.getName() + "§a !");
        PlayersUtils.broadcastMessage("");
    }

    public UHCPlayer getSlave() {
        return slave;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public UHCPlayer getActualOwner() {
        return actualOwner;
    }

    public void setActualOwner(UHCPlayer actualOwner) {
        this.actualOwner = actualOwner;
    }

    public void bid(UHCPlayer buyer, int newPrice) {
        if (newPrice <= price) {
            buyer.sendClassicMessage("§cVous devez au minimum enchérir pour " + (price + 1) + " diamants.");
            return;
        }
        if (actualOwner != null && buyer.getUuid().equals(actualOwner.getUuid()))
            return;
        if (module.getSlaveData().getDiamondsLeft(buyer) > price) {
            buyer.sendClassicMessage("§cVous n'avez plus assez de diamants ! Restants: " + module.getSlaveData().getDiamondsLeft());
            return;
        }
        setActualOwner(buyer);
        setPrice(newPrice);
        PlayersUtils.broadcastMessage("§a" + actualOwner.getName() + "§7 devient le nouveal enchérisseur le plus offrant de §e" + slave.getName() + " §7pour §b" + price + " diamants§7.");
        UHC.getInstance().getGameSave().registerEvent(EventType.MODULE, actualOwner.getName() + " devient le nouveal enchérisseur le plus offrant de " + slave.getName() + " pour " + price + " diamants.");
    }

    public void endBid() {
        PlayersUtils.broadcastMessage("");
        PlayersUtils.broadcastMessage("§aFin de l'enchère sur §e" + slave.getName());
        PlayersUtils.broadcastMessage("");
        UHC.getInstance().getGameSave().registerEvent(EventType.MODULE, "Fin de l'enchère sur: " + slave.getName());
        module.getSlaveConfig().getLeadersList().forEach(uhcPlayer -> {
            uhcPlayer.sendClassicMessage("§aIl vous reste §b" + module.getSlaveData().getDiamondsLeft(uhcPlayer) + " diamants§7.");
        });
        if (actualOwner == null) {
            PlayersUtils.broadcastMessage("§cPersonne ne s'est proposé pour acquérir §e" + slave.getName() + "§c, il sera donc distribué aléatoirement dans l'une des équipes.");
            UHC.getInstance().getGameSave().registerEvent(EventType.MODULE, slave.getName() + " n'a été choisit par personne.");
        } else {
            PlayersUtils.broadcastMessage("§7L'heureux propriétaire est donc §e" + actualOwner.getName() + " §7pour §b" + price + " diamants §7!");
            UHC.getInstance().getGameSave().registerEvent(EventType.MODULE, slave.getName() + " rejoint " + actualOwner.getName() + " pour " + price + " diamants.");
            slave.joinTeam(actualOwner.getUhcTeam());
            module.getSlaveData().getDiamondsLeft().put(actualOwner, module.getSlaveData().getDiamondsLeft(actualOwner) - price);
        }
        SoundUtils.playSoundToAll(Sound.VILLAGER_YES);
    }
}
